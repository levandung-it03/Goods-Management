package com.distributionsys.backend.controllers;

import com.distributionsys.backend.dtos.general.ByIdDto;
import com.distributionsys.backend.dtos.request.NewWarehouseRequest;
import com.distributionsys.backend.dtos.request.PaginatedTableRequest;
import com.distributionsys.backend.dtos.request.UpdateWarehouseRequest;
import com.distributionsys.backend.dtos.response.ApiResponseObject;
import com.distributionsys.backend.dtos.response.TablePagesResponse;
import com.distributionsys.backend.entities.sql.Warehouse;
import com.distributionsys.backend.enums.SucceedCodes;
import com.distributionsys.backend.services.WarehouseService;
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
public class WarehouseControllers {
    WarehouseService warehouseService;

    @GetMapping("/admin/v1/get-warehouses-pages")
    public ResponseEntity<ApiResponseObject<TablePagesResponse<Warehouse>>> getWarehousesPages(
        @Valid PaginatedTableRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_WAREHOUSES_PAGES,
            warehouseService.getWarehousesPages(request));
    }

    @PostMapping("/admin/v1/add-warehouse")
    public ResponseEntity<ApiResponseObject<Void>> addWarehouse(@Valid @RequestBody NewWarehouseRequest request) {
        warehouseService.addWarehouse(request);
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.ADD_WAREHOUSE);
    }

    @PutMapping("/admin/v1/update-warehouse")
    public ResponseEntity<ApiResponseObject<Void>> updateWarehouse(@Valid @RequestBody UpdateWarehouseRequest request) {
        warehouseService.updateWarehouse(request);
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.UPDATE_WAREHOUSE);
    }

    @PostMapping("/admin/v1/delete-warehouse")
    public ResponseEntity<ApiResponseObject<Void>> deleteWarehouse(@Valid @RequestBody ByIdDto request) {
        warehouseService.deleteWarehouse(request);
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.DELETE_WAREHOUSE);
    }
}