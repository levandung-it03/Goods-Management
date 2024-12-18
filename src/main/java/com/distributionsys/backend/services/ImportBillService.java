package com.distributionsys.backend.services;

import com.distributionsys.backend.dtos.request.NewImportBillRequest;
import com.distributionsys.backend.dtos.request.PaginatedRelationshipRequest;
import com.distributionsys.backend.dtos.request.PaginatedTableRequest;
import com.distributionsys.backend.dtos.response.ImportBillDetailsResponse;
import com.distributionsys.backend.dtos.response.TablePagesResponse;
import com.distributionsys.backend.dtos.utils.ImportBillFilterRequest;
import com.distributionsys.backend.dtos.utils.ImportBillWarehouseGoodsFilterRequest;
import com.distributionsys.backend.entities.sql.Goods;
import com.distributionsys.backend.entities.sql.ImportBill;
import com.distributionsys.backend.entities.sql.Warehouse;
import com.distributionsys.backend.entities.sql.relationships.ImportBillWarehouseGoods;
import com.distributionsys.backend.entities.sql.relationships.WarehouseGoods;
import com.distributionsys.backend.enums.ErrorCodes;
import com.distributionsys.backend.exceptions.ApplicationException;
import com.distributionsys.backend.mappers.PageMappers;
import com.distributionsys.backend.repositories.*;
import com.distributionsys.backend.services.auth.JwtService;
import com.distributionsys.backend.services.webflux.FluxedAsyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImportBillService {
    private final WarehouseGoodsRepository warehouseGoodsRepository;
    private final ImportBillWarehouseGoodsRepository importBillWarehouseGoodsRepository;
    private final ImportBillRepository importBillRepository;
    private final ClientInfoRepository clientInfoRepository;
    private final GoodsRepository goodsRepository;
    private final WarehouseRepository warehouseRepository;
    private final JwtService jwtService;
    private final FluxedAsyncService fluxedAsyncService;
    private final PageMappers pageMappers;

    @Value("${services.bills.max-retry-on-creation}")
    private Long MAX_RETRY;

    public Double getTotalImport(Long importBillId) {
//        importBillRepository.temp();
        return this.importBillRepository.totalImportBillByImportId(importBillId);
    }

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

    public void createImportBill(String accessToken, NewImportBillRequest request) {
        //--Input: [{goods, warehouse, quantity},...]
        var email = jwtService.readPayload(accessToken).get("sub");
        var clientInfo = clientInfoRepository.findByUserEmail(email)
            .orElseThrow(() -> new ApplicationException(ErrorCodes.INVALID_TOKEN));
        var goodsMap = goodsRepository
            .findAllById(request.getImportedWarehouseGoods().stream()
                .map(NewImportBillRequest.ImportedWarehouseGoodsDto::getGoodsId)
                .toList())
            .stream().collect(Collectors.toMap(Goods::getGoodsId, goods -> goods));
        var warehouseMap = warehouseRepository
            .findAllById(request.getImportedWarehouseGoods().stream()
                .map(NewImportBillRequest.ImportedWarehouseGoodsDto::getWarehouseId)
                .toList())
            .stream().collect(Collectors.toMap(Warehouse::getWarehouseId, warehouse -> warehouse));
        var builtWarehouseGoodsIdPairs = new HashMap<String, Long>();
        for (NewImportBillRequest.ImportedWarehouseGoodsDto reqObj : request.getImportedWarehouseGoods())
            builtWarehouseGoodsIdPairs
                .put(reqObj.getGoodsId() + "," + reqObj.getWarehouseId(), reqObj.getImportedGoodsQuantity());

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
            for (NewImportBillRequest.ImportedWarehouseGoodsDto whGoodsInp: request.getImportedWarehouseGoods()) {
                var updatedWarehouseGoods = updatedWhGoodsMarkers
                    .get(whGoodsInp.getGoodsId() + "," + whGoodsInp.getWarehouseId());
                if (Objects.isNull(updatedWarehouseGoods))
                    savedWarehouseGoodsList.add(WarehouseGoods.builder()
                        .goods(goodsMap.get(whGoodsInp.getGoodsId()))
                        .warehouse(warehouseMap.get(whGoodsInp.getWarehouseId()))
                        .currentQuantity(whGoodsInp.getImportedGoodsQuantity())
                        .build());
                else savedWarehouseGoodsList.add(updatedWarehouseGoods);
            }

            try {
                List<WarehouseGoods> newWarehouseGoodsList;
                try {
                    //--May throw DataIntegrityViolationException when saving (null-id-value entities)
                    //--May throw OptimisticLockException by @Version when updating (existing-id-value entities)
                    newWarehouseGoodsList = warehouseGoodsRepository.saveAll(savedWarehouseGoodsList);
                } catch (ObjectOptimisticLockingFailureException | DataIntegrityViolationException e) {
                    log.info("Retry creating ImportBill by: {}", clientInfo.getClientInfoId());
                    continue;   //--Do it again
                }

                //--Save the rest data after all @Version entities are saved correctly.
                var newImportBill = importBillRepository.save(ImportBill.builder()
                    .clientInfo(clientInfo).createdTime(LocalDateTime.now()).build());
                importBillWarehouseGoodsRepository.saveAll(newWarehouseGoodsList.stream().map(whGoods ->
                    ImportBillWarehouseGoods.builder()
                        .warehouseGoods(whGoods)
                        .importBill(newImportBill)
                        .goodsQuantity(builtWarehouseGoodsIdPairs
                            .get(whGoods.getGoods().getGoodsId() + "," + whGoods.getWarehouse().getWarehouseId()))
                        .build()).toList());

                //--Update Warehouse Goods or another fluxed streaming.
                fluxedAsyncService.updateFluxedGoodsFromWarehouseQuantityInRedis(savedWarehouseGoodsList);
                return;
            } catch (RuntimeException e) {
                log.info("Unaware exception throw when working with Hibernate, ImportBill Transactional rollback!");
                throw new ApplicationException(ErrorCodes.UNAWARE_ERR);
            }
        }

        log.info("Too many threads on ImportBill");
        throw new ApplicationException(ErrorCodes.RETRY_TOO_MANY_TIMES);
    }

    public List<ImportBill> getTop5ImportBills(String accessToken) {
        var clientInfo = clientInfoRepository
            .findByUserEmail(jwtService.readPayload(accessToken).get("sub"))
            .orElseThrow(() -> new ApplicationException(ErrorCodes.INVALID_TOKEN));
        return importBillRepository.findTop5ByClientInfoIdOrderByCreatedTimeDesc(clientInfo.getClientInfoId(),
            PageRequest.of(0, 5));
    }

    public List<ImportBillDetailsResponse> getImportBillDetails(Long importBillId) {
        return importBillWarehouseGoodsRepository.findByImportBill_ImportBillId(importBillId).stream()
            .map(goods -> new ImportBillDetailsResponse(
                goods.getImportBillWarehouseGoodsId(),
                goods.getWarehouseGoods(),
                goods.getGoodsQuantity()))
            .collect(Collectors.toList());
    }

    public TablePagesResponse<ImportBillWarehouseGoods> getImportBillWarehouseGoods(
        String accessToken, PaginatedRelationshipRequest request) {
        Pageable pageableCf = pageMappers.relationshipPageRequestToPageable(request)
            .toPageable(ImportBillWarehouseGoods.class);
        var clientInfo = clientInfoRepository
            .findByUserEmail(jwtService.readPayload(accessToken).get("sub"))
            .orElseThrow(() -> new ApplicationException(ErrorCodes.INVALID_TOKEN));

        if (Objects.isNull(request.getFilterFields()) || request.getFilterFields().isEmpty()) {
            Page<ImportBillWarehouseGoods> repoRes = importBillWarehouseGoodsRepository
                .findAllByClientInfoIdAndImportBillId(clientInfo.getClientInfoId(), request.getId(), pageableCf);
            return TablePagesResponse.<ImportBillWarehouseGoods>builder()
                .data(repoRes.stream().toList())
                .totalPages(repoRes.getTotalPages())
                .currentPage(request.getPage())
                .build();
        }
        try {
            var filterInfo = ImportBillWarehouseGoodsFilterRequest.buildFromFilterHashMap(request.getFilterFields());
            var repoRes = importBillWarehouseGoodsRepository.findAllByClientInfoIdAndImportBillIdAndFilterInfo(
                filterInfo, clientInfo.getClientInfoId(), request.getId(), pageableCf);
            return TablePagesResponse.<ImportBillWarehouseGoods>builder()
                .data(repoRes.stream().toList())
                .totalPages(repoRes.getTotalPages())
                .currentPage(request.getPage())
                .build();
        } catch (NoSuchFieldException | IllegalArgumentException | NullPointerException e) {
            throw new ApplicationException(ErrorCodes.INVALID_FILTERING_FIELD_OR_VALUE);
        }
    }
}
