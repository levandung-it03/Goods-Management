package com.distributionsys.backend.controllers;

import com.distributionsys.backend.dtos.response.ApiResponseObject;
import com.distributionsys.backend.dtos.response.ExportImportTrendResponse;
import com.distributionsys.backend.dtos.response.GoodsQuantityResponse;
import com.distributionsys.backend.dtos.response.StatisticsResponse;
import com.distributionsys.backend.enums.SucceedCodes;
import com.distributionsys.backend.services.StatisticsService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/private")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatisticsControllers {
    @Autowired
    private StatisticsService statisticsService;
    /**
     * API lấy thống kê dữ liệu.
     */
    @GetMapping("/user/v1/statistics")
    public ResponseEntity<ApiResponseObject<StatisticsResponse>> getStatistics(
            @RequestHeader("Authorization") String accessToken) {
        StatisticsResponse statistics = statisticsService.getStatistics(accessToken);
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_STATISTICS_SUCCESS, statistics);
    }

    @GetMapping("/user/v1/export-import-trend")
    public ResponseEntity<ApiResponseObject<List<ExportImportTrendResponse>>> getExportImportTrend(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

//        Timestamp startTimestamp = Timestamp.valueOf(startDate + " 00:00:00");
//        Timestamp endTimestamp = Timestamp.valueOf(endDate + " 23:59:59");
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        System.out.println((startDateTime.toString()));

        List<ExportImportTrendResponse> trend = statisticsService.getExportImportTrend(startDateTime, endDateTime);
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_EXPORT_IMPORT_TREND, trend);
    }

    @GetMapping("/user/v1/goods-quantity")
    public ResponseEntity<ApiResponseObject<List<GoodsQuantityResponse>>> getAllGoodsWithQuantity() {
        List<GoodsQuantityResponse> list = statisticsService.getAllGoodsWithQuantity();
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_GOODS_QUANTITY, list);
    }
}
