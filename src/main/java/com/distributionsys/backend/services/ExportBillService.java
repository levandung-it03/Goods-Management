package com.distributionsys.backend.services;

import com.distributionsys.backend.dtos.request.NewExportBillRequest;
import com.distributionsys.backend.dtos.request.NewExportBillRequest;
import com.distributionsys.backend.dtos.request.PaginatedTableRequest;
import com.distributionsys.backend.dtos.response.TablePagesResponse;
import com.distributionsys.backend.dtos.utils.ExportBillFilterRequest;
import com.distributionsys.backend.entities.sql.ExportBill;
import com.distributionsys.backend.entities.sql.ExportBill;
import com.distributionsys.backend.entities.sql.relationships.ExportBillWarehouseGoods;
import com.distributionsys.backend.entities.sql.relationships.WarehouseGoods;
import com.distributionsys.backend.enums.ErrorCodes;
import com.distributionsys.backend.exceptions.ApplicationException;
import com.distributionsys.backend.mappers.PageMappers;
import com.distributionsys.backend.repositories.*;
import com.distributionsys.backend.services.auth.JwtService;
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
public class ExportBillService {
    PlatformTransactionManager transactionManager;
    WarehouseGoodsRepository warehouseGoodsRepository;
    ExportBillWarehouseGoodsRepository exportBillWarehouseGoodsRepository;
    ExportBillRepository exportBillRepository;
    ClientInfoRepository clientInfoRepository;
    JwtService jwtService;
    PageMappers pageMappers;

    public TablePagesResponse<ExportBill> getExportBillPages(String accessToken,
                                                             PaginatedTableRequest request) {
        Pageable pageableCf = pageMappers.tablePageRequestToPageable(request).toPageable(ExportBill.class);
        var clientInfo = clientInfoRepository
            .findByUserEmail(jwtService.readPayload(accessToken).get("sub"))
            .orElseThrow(() -> new ApplicationException(ErrorCodes.INVALID_TOKEN));

        if (Objects.isNull(request.getFilterFields()) || request.getFilterFields().isEmpty()) {
            Page<ExportBill> repoRes = exportBillRepository.findAllByClientInfoClientInfoId(
                clientInfo.getClientInfoId(), pageableCf);
            return TablePagesResponse.<ExportBill>builder()
                .data(repoRes.stream().toList())
                .totalPages(repoRes.getTotalPages())
                .currentPage(request.getPage())
                .build();
        }
        try {
            var filterInfo = ExportBillFilterRequest.buildFromFilterHashMap(request.getFilterFields());
            var repoRes = exportBillRepository.findAllByClientInfoClientInfoId(
                clientInfo.getClientInfoId(), filterInfo, pageableCf);
            return TablePagesResponse.<ExportBill>builder()
                .data(repoRes.stream().toList())
                .totalPages(repoRes.getTotalPages())
                .currentPage(request.getPage())
                .build();
        } catch (NoSuchFieldException | IllegalArgumentException | NullPointerException e) {
            throw new ApplicationException(ErrorCodes.INVALID_FILTERING_FIELD_OR_VALUE);
        }
    }

    @Async
    public void createExportBill(String accessToken, NewExportBillRequest request) {
        var clientInfo = clientInfoRepository
            .findByUserEmail(jwtService.readPayload(accessToken).get("sub"))
            .orElseThrow(() -> new ApplicationException(ErrorCodes.INVALID_TOKEN));

        int MAX_RETRY = 10;
        for (int times = 1; times <= MAX_RETRY; times++) {
            var updatedWarehouesGoodsList = warehouseGoodsRepository.findAllById(request.getExportedWarehouseGoods()
                .stream().map(NewExportBillRequest.ExportedWarehouseGoodsDto::getWarehouseGoodsId).toList());
            for (int index = 0; index < updatedWarehouesGoodsList.size(); index++)
                updatedWarehouesGoodsList.get(index).setCurrentQuantity(
                    updatedWarehouesGoodsList.get(index).getCurrentQuantity()
                        + request.getExportedWarehouseGoods().get(index).getExportedGoodsQuantity());

            var transDef = new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRED);
            TransactionStatus transStatus = transactionManager.getTransaction(transDef);
            try {
                try {
                    //--May throw OptimisticLockException by @Version when updating (existing-id-value entities)
                    warehouseGoodsRepository.saveAll(updatedWarehouesGoodsList);
                } catch (OptimisticLockException e) {
                    log.info("Retry creating ExportBill by: {}", clientInfo.getClientInfoId());
                    transactionManager.rollback(transStatus);   //--Clear Transaction
                    continue;   //--Do it again
                }
                //--Save the rest data after all @Version entities are saved correctly.
                var newExportBill = exportBillRepository.save(ExportBill.builder()
                    .clientInfo(clientInfo).createdTime(LocalDateTime.now()).receiverName(request.getReceiverName())
                    .exportBillStatus(true).build());
                var newExportBillWhGoodsRelationship = new ArrayList<ExportBillWarehouseGoods>();
                for (int index = 0; index < updatedWarehouesGoodsList.size(); index++) {
                    newExportBillWhGoodsRelationship.add(ExportBillWarehouseGoods.builder()
                        .warehouseGoods(updatedWarehouesGoodsList.get(index))
                        .exportBill(newExportBill)
                        .goodsQuantity(request.getExportedWarehouseGoods().get(index).getExportedGoodsQuantity())
                        .build());
                }
                exportBillWarehouseGoodsRepository.saveAll(newExportBillWhGoodsRelationship);
                transactionManager.commit(transStatus);
                break;
            } catch (RuntimeException e) {
                transactionManager.rollback(transStatus);
                log.info("Unaware exception throw when working with Hibernate, ExportBill Transactional rollback!");
                throw new ApplicationException(ErrorCodes.UNAWARE_ERR);
            }
        }

        log.info("Too many threads on ExportBill");
        throw new ApplicationException(ErrorCodes.RETRY_TOO_MANY_TIMES);
    }
}
