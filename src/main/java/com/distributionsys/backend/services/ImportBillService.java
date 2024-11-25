package com.distributionsys.backend.services;

import com.distributionsys.backend.dtos.request.NewImportBillRequest;
import com.distributionsys.backend.dtos.request.PaginatedTableRequest;
import com.distributionsys.backend.dtos.response.TablePagesResponse;
import com.distributionsys.backend.dtos.utils.ImportBillFilterRequest;
import com.distributionsys.backend.entities.sql.ImportBill;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ImportBillService {
    WarehouseGoodsRepository warehouseGoodsRepository;
    ImportBillRepository importBillRepository;
    ClientInfoRepository clientInfoRepository;
    GoodsRepository goodsRepository;
    WarehouseRepository warehouseRepository;
    JwtService jwtService;
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

    public void createImportBill(String accessToken, NewImportBillRequest request) {
        var clientInfo = clientInfoRepository
            .findByUserEmail(jwtService.readPayload(accessToken).get("sub"))
            .orElseThrow(() -> new ApplicationException(ErrorCodes.INVALID_TOKEN));
        var goodsListInOrder = goodsRepository.findAllById(request.getImportedWarehouseGoods().stream()
            .map(NewImportBillRequest.ImportedWarehouseGoodsDto::getGoodsId).toList());
        var warehouseListInOrder = warehouseRepository.findAllById(request.getImportedWarehouseGoods().stream()
            .map(NewImportBillRequest.ImportedWarehouseGoodsDto::getWarehouseId).toList());
        var builtWarehouseGoodsIdPairs = new HashMap<String, Long>();
        request.getImportedWarehouseGoods().forEach(reqObj -> builtWarehouseGoodsIdPairs.put(
            reqObj.getGoodsId() + "," + reqObj.getWarehouseId(),
            reqObj.getImportedGoodsQuantity()
        ));

        while (true) {
            try {
                var foundWarehouseGoodsList = warehouseGoodsRepository.findAllByGoodsIdAndWarehouseIdPairs(
                    builtWarehouseGoodsIdPairs.keySet().stream().toList());
                //--Use Hashmap Markers for better finding id-pairs performance
                var warehouseGoodsMarkers = new HashMap<String, WarehouseGoods>();
                for (WarehouseGoods warehouseGoods : foundWarehouseGoodsList) {
                    var key = warehouseGoods.getGoods().getGoodsId()
                        + "," + warehouseGoods.getWarehouse().getWarehouseId();
                    //--Create existing WarehouseGoods Markers
                    warehouseGoodsMarkers.put(key, warehouseGoods);
                    //--Borrow this iteration to update goodsQuantity in Warehouse.
                    warehouseGoods.setCurrentQuantity(
                        warehouseGoods.getCurrentQuantity() + builtWarehouseGoodsIdPairs.get(key));
                }
                var savedWarehouseGoodsList = new ArrayList<WarehouseGoods>();
                for (int index = 0; index < request.getImportedWarehouseGoods().size(); index++) {
                    var updatedWarehouseGoods = warehouseGoodsMarkers.get(
                        request.getImportedWarehouseGoods().get(index).getGoodsId()
                            + "," + request.getImportedWarehouseGoods().get(index).getWarehouseId());
                    if (Objects.isNull(updatedWarehouseGoods))
                        savedWarehouseGoodsList.add(WarehouseGoods.builder()
                            .goods(goodsListInOrder.get(index))
                            .warehouse(warehouseListInOrder.get(index))
                            .currentQuantity(request.getImportedWarehouseGoods().get(index).getImportedGoodsQuantity())
                            .build());
                    else
                        savedWarehouseGoodsList.add(updatedWarehouseGoods);
                }
                //--May throw DataIntegrityViolationException when saving (null-id-value entities)
                //--May throw OptimisticLockException by @Version when updating (existing-id-value entities)
                warehouseGoodsRepository.saveAll(savedWarehouseGoodsList);
                break;
            } catch (OptimisticLockException | DataIntegrityViolationException e) {
                System.out.println("Retry create ImportBill by: " + clientInfo.getFirstName() + clientInfo.getLastName());
            } catch (RuntimeException e) {
                throw new ApplicationException(ErrorCodes.UNAWARE_ERR);
            }
        }
    }
}
