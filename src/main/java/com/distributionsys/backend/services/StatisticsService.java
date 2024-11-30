package com.distributionsys.backend.services;

import com.distributionsys.backend.dtos.response.ExportImportTrendResponse;
import com.distributionsys.backend.dtos.response.GoodsQuantityResponse;
import com.distributionsys.backend.dtos.response.StatisticsResponse;
import com.distributionsys.backend.entities.sql.Goods;
import com.distributionsys.backend.entities.sql.relationships.WarehouseGoods;
import com.distributionsys.backend.enums.ErrorCodes;
import com.distributionsys.backend.exceptions.ApplicationException;
import com.distributionsys.backend.repositories.*;
import com.distributionsys.backend.services.auth.JwtService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatisticsService {

    ImportBillRepository importBillRepository;
    ExportBillRepository exportBillRepository;
    GoodsRepository goodsRepository;
    SupplierRepository supplierRepository;
    JwtService jwtService;
    ClientInfoRepository clientInfoRepository;
    WarehouseGoodsRepository warehouseGoodsRepository;
    /**
     * Lấy thống kê dữ liệu.
     */
    public StatisticsResponse getStatistics(String accessToken) {
        // Lấy thông tin người dùng từ accessToken
        String email = jwtService.readPayload(accessToken).get("sub").toString();
        var clientInfo = clientInfoRepository.findByUserEmail(email)
                .orElseThrow(() -> new ApplicationException(ErrorCodes.INVALID_TOKEN));

        Long clientId = clientInfo.getClientInfoId();

        // Thống kê
        Long totalImportBills = importBillRepository.countImportBillsByClientInfoId(clientId);
        Long totalExportBills = exportBillRepository.countExportBillsByClientInfoId(clientId);
        Long totalGoods = goodsRepository.count();
        Long totalSuppliers = supplierRepository.count();

        return StatisticsResponse.builder()
                .totalImportBills(totalImportBills)
                .totalExportBills(totalExportBills)
                .totalGoods(totalGoods)
                .totalSuppliers(totalSuppliers)
                .build();
    }

    public List<ExportImportTrendResponse> getExportImportTrend(LocalDateTime startDate, LocalDateTime endDate) {
        // Query the data for export and import bills within the specified date range
        System.out.print(startDate.toString() + "============");
        List<Object[]> exportData = exportBillRepository.findExportDataByDateRange(startDate, endDate);
        List<Object[]> importData = importBillRepository.findImportDataByDateRange(startDate, endDate);

        // Combine data by date
        return exportData.stream()
                .map(export -> {
                    String date = (String) export[0];
                    Long quantityExported = (Long) export[1];
                    Long quantityImported = importData.stream()
                            .filter(importRecord -> ((String) importRecord[0]).equals(date))
                            .map(importRecord -> (Long) importRecord[1])
                            .findFirst().orElse(0L);

                    return ExportImportTrendResponse.builder()
                            .date(date)
                            .quantityExported(quantityExported)
                            .quantityImported(quantityImported)
                            .build();
                })
                .collect(Collectors.toList());
    }

    public List<GoodsQuantityResponse> getAllGoodsWithQuantity() {
        List<WarehouseGoods> warehouseGoodsList = warehouseGoodsRepository.findAll();
        Map<Long, Long> productQuantities = new HashMap<>();

        // Duyệt qua tất cả WarehouseGoods để tính tổng số lượng cho mỗi sản phẩm
        for (WarehouseGoods warehouseGoods : warehouseGoodsList) {
            Long goodsId = warehouseGoods.getGoods().getGoodsId();
            Long currentQuantity = warehouseGoods.getCurrentQuantity();

            // Cộng dồn số lượng cho sản phẩm cùng id
            productQuantities.put(goodsId, productQuantities.getOrDefault(goodsId, 0L) + currentQuantity);
        }

        // Chuyển đổi dữ liệu từ Map sang DTO để trả về
        List<GoodsQuantityResponse> goodsQuantityDTOList = new ArrayList<>();
        for (Map.Entry<Long, Long> entry : productQuantities.entrySet()) {
            // Lấy thông tin sản phẩm từ goodsId
            Goods goods = warehouseGoodsRepository.findById(entry.getKey()).get().getGoods();
            goodsQuantityDTOList.add(new GoodsQuantityResponse(goods.getGoodsName(), entry.getValue()));
        }

        return goodsQuantityDTOList;
    }
}