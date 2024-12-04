package com.distributionsys.backend.controllers;

import com.distributionsys.backend.dtos.request.NewImportBillRequest;
import com.distributionsys.backend.dtos.request.PaginatedRelationshipRequest;
import com.distributionsys.backend.dtos.request.PaginatedTableRequest;
import com.distributionsys.backend.dtos.response.ApiResponseObject;
import com.distributionsys.backend.dtos.response.ImportBillDetailsResponse;
import com.distributionsys.backend.dtos.response.TablePagesResponse;
import com.distributionsys.backend.entities.sql.ImportBill;
import com.distributionsys.backend.entities.sql.relationships.ImportBillWarehouseGoods;
import com.distributionsys.backend.enums.SucceedCodes;
import com.distributionsys.backend.services.ImportBillService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/private")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ImportBillControllers {
    ImportBillService importBillService;

    @GetMapping("/user/v1/total-import-bill/{importBillId}")
    public ResponseEntity<ApiResponseObject<String>> createImportBill(
        @PathVariable Long importBillId) {
        var data = this.importBillService.getTotalImport(importBillId);
        Double result = Objects.isNull(data) ? Double.valueOf(0) : data;
        DecimalFormat df = new DecimalFormat("#,###.##");
        String formattedData = df.format(result);
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.TOTAL_IMPORT_BILL, formattedData);
    }

    @GetMapping("/user/v1/get-import-bill-pages")
    public ResponseEntity<ApiResponseObject<TablePagesResponse<ImportBill>>> getImportBillPages(
        @RequestHeader("Authorization") String accessToken,
        @Valid PaginatedTableRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_IMPORT_BILL_PAGES,
            importBillService.getImportBillPages(accessToken, request));
    }

    @GetMapping("/user/v1/get-warehouse-goods-of-import-bill-pages")
    public ResponseEntity<ApiResponseObject<TablePagesResponse<ImportBillWarehouseGoods>>> getImportBillWarehouseGoods(
        @RequestHeader("Authorization") String accessToken,
        @Valid PaginatedRelationshipRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_IMPORT_BILL_DETAIL,
            importBillService.getImportBillWarehouseGoods(accessToken, request));
    }

    @PostMapping("/user/v1/create-import-bill")
    public ResponseEntity<ApiResponseObject<Void>> createImportBill(
        @RequestHeader("Authorization") String accessToken,
        @Valid @RequestBody NewImportBillRequest request) {
        importBillService.createImportBill(accessToken, request);
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.CREATE_IMPORT_BILL);
    }

    @GetMapping("/user/v1/get-import-bill-top5")
    public ResponseEntity<ApiResponseObject<List<ImportBill>>> getTop3ImportBills(
        @RequestHeader("Authorization") String accessToken) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_RECENT_IMPORT_BILL_LIST,
            importBillService.getTop5ImportBills(accessToken));
    }

    @GetMapping("/user/v1/imports-bill/{id}")
    public ResponseEntity<ApiResponseObject<List<ImportBillDetailsResponse>>> getExportBillDetail(@PathVariable Long id) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_IMPORT_BILL_DETAIL,
            importBillService.getImportBillDetails(id));
    }
}
