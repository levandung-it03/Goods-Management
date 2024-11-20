package com.distributionsys.backend.mappers;

import com.distributionsys.backend.dtos.request.NewWarehouseRequest;
import com.distributionsys.backend.dtos.request.UpdateWarehouseRequest;
import com.distributionsys.backend.entities.sql.Warehouse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface WarehouseMappers {

    @Mapping(target = "warehouseId", ignore = true)
    Warehouse newToWarehouse(NewWarehouseRequest request);

    @Mapping(target = "warehouseId", ignore = true)
    void updateWarehouse(@MappingTarget Warehouse updatedWarehouse, UpdateWarehouseRequest request);
}
