package com.distributionsys.backend.controllers;

import com.distributionsys.backend.dtos.request.NewImportBillRequest;
import com.distributionsys.backend.dtos.request.PaginatedTableRequest;
import com.distributionsys.backend.dtos.response.ApiResponseObject;
import com.distributionsys.backend.dtos.response.TablePagesResponse;
import com.distributionsys.backend.entities.sql.ImportBill;
import com.distributionsys.backend.enums.SucceedCodes;
import com.distributionsys.backend.services.ImportBillService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/private")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ImportBillControllers {
    ImportBillService importBillService;

    @GetMapping("/user/v1/get-import-bill-pages")
    public ResponseEntity<ApiResponseObject<TablePagesResponse<ImportBill>>> getImportBillPages(
        @RequestHeader("Authorization") String accessToken,
        @Valid PaginatedTableRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_IMPORT_BILL_PAGES,
            importBillService.getImportBillPages(accessToken, request));
    }

    @GetMapping("/user/v1/create-import-bill")
    public ResponseEntity<ApiResponseObject<Void>> createImportBill(
        @RequestHeader("Authorization") String accessToken,
        @Valid @RequestBody NewImportBillRequest request) {
        importBillService.createImportBill(accessToken, request);
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.CREATE_IMPORT_BILL);
    }
}