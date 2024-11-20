package com.distributionsys.backend.services;

import com.distributionsys.backend.dtos.general.ByIdDto;
import com.distributionsys.backend.dtos.request.NewWarehouseRequest;
import com.distributionsys.backend.dtos.request.PaginatedTableRequest;
import com.distributionsys.backend.dtos.request.UpdateWarehouseRequest;
import com.distributionsys.backend.dtos.response.TablePagesResponse;
import com.distributionsys.backend.entities.sql.Warehouse;
import com.distributionsys.backend.enums.ErrorCodes;
import com.distributionsys.backend.exceptions.ApplicationException;
import com.distributionsys.backend.mappers.PageMappers;
import com.distributionsys.backend.mappers.WarehouseMappers;
import com.distributionsys.backend.repositories.WarehouseGoodsRepository;
import com.distributionsys.backend.repositories.WarehouseRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WarehouseService {
    WarehouseRepository warehouseRepository;
    WarehouseGoodsRepository warehouseGoodsRepository;
    PageMappers pageMappers;
    WarehouseMappers warehouseMappers;

    public TablePagesResponse<Warehouse> getWarehousesPages(PaginatedTableRequest request) {
        Pageable pageableCf = pageMappers.tablePageRequestToPageable(request).toPageable(Warehouse.class);
        if (Objects.isNull(request) || request.getFilterFields().isEmpty()) {
            Page<Warehouse> repoRes = warehouseRepository.findAll(pageableCf);
            return TablePagesResponse.<Warehouse>builder()
                .data(repoRes.stream().toList())
                .totalPages(repoRes.getTotalPages())
                .currentPage(request.getPage())
                .build();
        }
        try {
            var warehouseInfo = Warehouse.buildFormFilterHashMap(request.getFilterFields());
            Page<Warehouse> repoRes = warehouseRepository.findAllByWarehouseFilterInfo(warehouseInfo, pageableCf);
            return TablePagesResponse.<Warehouse>builder()
                .data(repoRes.stream().toList())
                .totalPages(repoRes.getTotalPages())
                .currentPage(request.getPage())
                .build();
        } catch (ApplicationException | NullPointerException | IllegalArgumentException | NoSuchFieldException e) {
            throw new ApplicationException(ErrorCodes.INVALID_FILTERING_FIELD_OR_VALUE);
        }
    }

    public void addWarehouse(NewWarehouseRequest request) {
        if (warehouseRepository.existsByWarehouseName(request.getWarehouseName()))
            throw new ApplicationException(ErrorCodes.DUPLICATE_WAREHOUSE);
        warehouseRepository.save(warehouseMappers.newToWarehouse(request));
    }

    public void deleteWarehouse(ByIdDto request) {
        if (warehouseGoodsRepository.existsByWarehouseWarehouseId(request.getId()))
            throw new ApplicationException(ErrorCodes.DELETE_WAREHOUSE);
        if (warehouseRepository.existsById(request.getId()))
            throw new ApplicationException(ErrorCodes.INVALID_PRIMARY);
        warehouseRepository.deleteById(request.getId());
    }

    public void updateWarehouse(UpdateWarehouseRequest request) {
        if (warehouseGoodsRepository.existsByWarehouseWarehouseId(request.getWarehouseId()))
            throw new ApplicationException(ErrorCodes.UPDATE_WAREHOUSE);
        Warehouse updatedWarehouse = warehouseRepository
            .findById(request.getWarehouseId())
            .orElseThrow(() -> new ApplicationException(ErrorCodes.INVALID_PRIMARY));
        warehouseMappers.updateWarehouse(updatedWarehouse, request);
        warehouseRepository.updateWarehouseByWarehouseInfo(updatedWarehouse);
    }
}
