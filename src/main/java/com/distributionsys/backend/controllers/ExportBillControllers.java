package com.distributionsys.backend.controllers;

import com.distributionsys.backend.dtos.request.NewExportBillRequest;
import com.distributionsys.backend.dtos.request.PaginatedTableRequest;
import com.distributionsys.backend.dtos.response.ApiResponseObject;
import com.distributionsys.backend.dtos.response.ExportBillDetailsResponse;
import com.distributionsys.backend.dtos.response.TablePagesResponse;
import com.distributionsys.backend.entities.sql.ExportBill;
import com.distributionsys.backend.enums.SucceedCodes;
import com.distributionsys.backend.services.ExportBillService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/private")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExportBillControllers {
    ExportBillService exportBillService;

    @GetMapping("/user/v1/get-export-bill-pages")
    public ResponseEntity<ApiResponseObject<TablePagesResponse<ExportBill>>> getExportBillPages(
        @RequestHeader("Authorization") String accessToken,
        @Valid PaginatedTableRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_EXPORT_BILL_PAGES,
            exportBillService.getExportBillPages(accessToken, request));
    }

    @PostMapping("/user/v1/create-export-bill")
    public ResponseEntity<ApiResponseObject<Void>> createExportBill(
        @RequestHeader("Authorization") String accessToken,
        @Valid @RequestBody NewExportBillRequest request) {
        exportBillService.createExportBill(accessToken, request);
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.PENDING_EXPORT_BILL);
    }

    @GetMapping("/user/v1/get-export-bill-top5")
    public ResponseEntity<ApiResponseObject<List<ExportBill>>> getTop3ExportBills(
            @RequestHeader("Authorization") String accessToken) {

        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_RECENT_EXPORT_BILL_LIST  ,
                exportBillService.getTop5ExportBills(accessToken));
    }
    @GetMapping("/user/v1/exports-bill/{id}")
    public ResponseEntity<ApiResponseObject<List<ExportBillDetailsResponse>>> getExportBillDetail(@PathVariable Long id) {
        List<ExportBillDetailsResponse> goodsList = exportBillService.getExportBillDetails(id);
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_EXPORT_BILL_DETAIL, goodsList);
    }
}
