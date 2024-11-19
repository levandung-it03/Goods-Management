package com.distributionsys.backend.mappers;

import com.distributionsys.backend.dtos.request.NewSupplierRequest;
import com.distributionsys.backend.dtos.request.UpdateSupplierRequest;
import com.distributionsys.backend.entities.sql.Supplier;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SupplierMappers {

    @Mapping(target = "supplierId", ignore = true)
    Supplier newToSupplier(NewSupplierRequest supplier);

    @Mapping(target = "supplierId", ignore = true)
    void updateSupplier(@MappingTarget Supplier updatedSupplier, UpdateSupplierRequest newInfo);
}
