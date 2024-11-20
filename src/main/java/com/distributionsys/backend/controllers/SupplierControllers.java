package com.distributionsys.backend.controllers;

import com.distributionsys.backend.dtos.general.ByIdDto;
import com.distributionsys.backend.dtos.request.NewSupplierRequest;
import com.distributionsys.backend.dtos.request.PaginatedTableRequest;
import com.distributionsys.backend.dtos.request.UpdateSupplierRequest;
import com.distributionsys.backend.dtos.response.ApiResponseObject;
import com.distributionsys.backend.dtos.response.TablePagesResponse;
import com.distributionsys.backend.entities.sql.Supplier;
import com.distributionsys.backend.enums.SucceedCodes;
import com.distributionsys.backend.services.SupplierService;
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
public class SupplierControllers {
    SupplierService supplierService;

    @GetMapping("/admin/v1/get-suppliers-pages")
    public ResponseEntity<ApiResponseObject<TablePagesResponse<Supplier>>> getSuppliersPages(
        @Valid PaginatedTableRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_SUPPLIERS_PAGES,
            supplierService.getSuppliersPages(request));
    }

    @PostMapping("/admin/v1/add-supplier")
    public ResponseEntity<ApiResponseObject<Void>> addSupplier(@Valid @RequestBody NewSupplierRequest request) {
        supplierService.addSupplier(request);
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.ADD_SUPPLIER);
    }

    @PutMapping("/admin/v1/update-supplier")
    public ResponseEntity<ApiResponseObject<Void>> updateSupplier(@Valid @RequestBody UpdateSupplierRequest request) {
        supplierService.updateSupplier(request);
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.UPDATE_SUPPLIER);
    }

    @PostMapping("/admin/v1/delete-supplier")
    public ResponseEntity<ApiResponseObject<Void>> deleteSupplier(@Valid @RequestBody ByIdDto request) {
        supplierService.deleteSupplier(request);
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.DELETE_SUPPLIER);
    }
}
