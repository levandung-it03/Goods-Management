package com.distributionsys.backend.services;

import com.distributionsys.backend.dtos.request.NewImportBillRequest;
import com.distributionsys.backend.dtos.request.PaginatedTableRequest;
import com.distributionsys.backend.dtos.response.TablePagesResponse;
import com.distributionsys.backend.dtos.utils.ImportBillFilterRequest;
import com.distributionsys.backend.entities.sql.ImportBill;
import com.distributionsys.backend.entities.sql.relationships.ImportBillWarehouseGoods;
import com.distributionsys.backend.entities.sql.relationships.WarehouseGoods;
import com.distributionsys.backend.enums.ErrorCodes;
import com.distributionsys.backend.exceptions.ApplicationException;
import com.distributionsys.backend.mappers.PageMappers;
import com.distributionsys.backend.repositories.*;
import com.distributionsys.backend.services.auth.JwtService;
import com.distributionsys.backend.services.webflux.FluxedAsyncService;
import jakarta.persistence.OptimisticLockException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

@Service
@EnableAsync
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ImportBillService {
    PlatformTransactionManager transactionManager;
    WarehouseGoodsRepository warehouseGoodsRepository;
    ImportBillWarehouseGoodsRepository importBillWarehouseGoodsRepository;
    ImportBillRepository importBillRepository;
    ClientInfoRepository clientInfoRepository;
    GoodsRepository goodsRepository;
    WarehouseRepository warehouseRepository;
    JwtService jwtService;
    FluxedAsyncService fluxedAsyncService;
    PageMappers pageMappers;

    public TablePagesResponse<ImportBill> getImportBillPages(String accessToken,
                                                             PaginatedTableRequest request) {
        Pageable pageableCf = pageMappers.tablePageRequestToPageable(request).toPageable(ImportBill.class);
        var clientInfo = clientInfoRepository
            .findByUserEmail(jwtService.readPayload(accessToken).get("sub"))
            .orElseThrow(() -> new ApplicationException(ErrorCodes.INVALID_TOKEN));

        if (Objects.isNull(request.getFilterFields()) || request.getFilterFields().isEmpty()) {
            Page<ImportBill> repoRes = importBillRepository.findAllByClientInfoClientInfoId(
                clientInfo.getClientInfoId(), pageableCf);
            return TablePagesResponse.<ImportBill>builder()
                .data(repoRes.stream().toList())
                .totalPages(repoRes.getTotalPages())
                .currentPage(request.getPage())
                .build();
        }
        try {
            var filterInfo = ImportBillFilterRequest.buildFromFilterHashMap(request.getFilterFields());
            var repoRes = importBillRepository.findAllByClientInfoClientInfoId(
                clientInfo.getClientInfoId(), filterInfo, pageableCf);
            return TablePagesResponse.<ImportBill>builder()
                .data(repoRes.stream().toList())
                .totalPages(repoRes.getTotalPages())
                .currentPage(request.getPage())
                .build();
        } catch (NoSuchFieldException | IllegalArgumentException | NullPointerException e) {
            throw new ApplicationException(ErrorCodes.INVALID_FILTERING_FIELD_OR_VALUE);
        }
    }

    @Async
    public void createImportBill(String accessToken, NewImportBillRequest request) {
        var clientInfo = clientInfoRepository
            .findByUserEmail(jwtService.readPayload(accessToken).get("sub"))
            .orElseThrow(() -> new ApplicationException(ErrorCodes.INVALID_TOKEN));
        var goodsListInOrder = goodsRepository.findAllById(request.getImportedWarehouseGoods().stream()
            .map(NewImportBillRequest.ImportedWarehouseGoodsDto::getGoodsId).toList());
        var warehouseListInOrder = warehouseRepository.findAllById(request.getImportedWarehouseGoods().stream()
            .map(NewImportBillRequest.ImportedWarehouseGoodsDto::getWarehouseId).toList());
        var builtWarehouseGoodsIdPairs = new HashMap<String, Long>();
        for (NewImportBillRequest.ImportedWarehouseGoodsDto reqObj : request.getImportedWarehouseGoods())
            builtWarehouseGoodsIdPairs
                .put(reqObj.getGoodsId() + "," + reqObj.getWarehouseId(), reqObj.getImportedGoodsQuantity());

        int MAX_RETRY = 10;
        for (int times = 1; times <= MAX_RETRY; times++) {
            var foundWarehouseGoodsList = warehouseGoodsRepository.findAllByGoodsIdAndWarehouseIdPairs(
                builtWarehouseGoodsIdPairs.keySet().stream().toList());
            //--Use Hashmap Markers for better finding id-pairs performance
            var updatedWhGoodsMarkers = new HashMap<String, WarehouseGoods>();
            for (WarehouseGoods foundObj : foundWarehouseGoodsList) {
                var key = foundObj.getGoods().getGoodsId() + "," + foundObj.getWarehouse().getWarehouseId();
                //--Create existing WarehouseGoods Markers
                updatedWhGoodsMarkers.put(key, foundObj);
                //--Borrow this iteration to update goodsQuantity in Warehouse.
                foundObj.setCurrentQuantity(foundObj.getCurrentQuantity() + builtWarehouseGoodsIdPairs.get(key));
            }
            var savedWarehouseGoodsList = new ArrayList<WarehouseGoods>();
            for (int index = 0; index < request.getImportedWarehouseGoods().size(); index++) {
                var currentWarehouseGoods = request.getImportedWarehouseGoods().get(index);
                var updatedWarehouseGoods = updatedWhGoodsMarkers
                    .get(currentWarehouseGoods.getGoodsId() + "," + currentWarehouseGoods.getWarehouseId());
                if (Objects.isNull(updatedWarehouseGoods))
                    savedWarehouseGoodsList.add(WarehouseGoods.builder()
                        .goods(goodsListInOrder.get(index))
                        .warehouse(warehouseListInOrder.get(index))
                        .currentQuantity(currentWarehouseGoods.getImportedGoodsQuantity())
                        .build());
                else    savedWarehouseGoodsList.add(updatedWarehouseGoods);
            }

            var transDef = new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRED);
            TransactionStatus transStatus = transactionManager.getTransaction(transDef);
            try {
                try {
                    //--May throw DataIntegrityViolationException when saving (null-id-value entities)
                    //--May throw OptimisticLockException by @Version when updating (existing-id-value entities)
                    warehouseGoodsRepository.saveAll(savedWarehouseGoodsList);
                } catch (OptimisticLockException | DataIntegrityViolationException e) {
                    log.info("Retry creating ImportBill by: {}", clientInfo.getClientInfoId());
                    transactionManager.rollback(transStatus);   //--Clear Transaction
                    continue;   //--Do it again
                }

                //--Save the rest data after all @Version entities are saved correctly.
                var newImportBill = importBillRepository.save(ImportBill.builder()
                    .clientInfo(clientInfo).createdTime(LocalDateTime.now()).build());
                importBillWarehouseGoodsRepository.saveAll(savedWarehouseGoodsList.stream().map(whGoods ->
                    ImportBillWarehouseGoods.builder()
                        .warehouseGoods(whGoods)
                        .importBill(newImportBill)
                        .goodsQuantity(builtWarehouseGoodsIdPairs
                            .get(whGoods.getGoods().getGoodsId() + "," + whGoods.getWarehouse().getWarehouseId()))
                        .build()).toList());
                transactionManager.commit(transStatus);
                //--Update Warehouse Goods or another fluxed streaming.
                fluxedAsyncService.updateFluxedGoodsFromWarehouseQuantityInRedis(savedWarehouseGoodsList);
                return;
            } catch (RuntimeException e) {
                transactionManager.rollback(transStatus);
                log.info("Unaware exception throw when working with Hibernate, ImportBill Transactional rollback!");
                throw new ApplicationException(ErrorCodes.UNAWARE_ERR);
            }
        }

        log.info("Too many threads on ImportBill");
        throw new ApplicationException(ErrorCodes.RETRY_TOO_MANY_TIMES);
    }
}
