package com.distributionsys.backend.services;

import com.distributionsys.backend.dtos.general.ByIdDto;
import com.distributionsys.backend.dtos.general.SimpleSearchingDto;
import com.distributionsys.backend.dtos.request.NewSupplierRequest;
import com.distributionsys.backend.dtos.request.PaginatedTableRequest;
import com.distributionsys.backend.dtos.request.UpdateSupplierRequest;
import com.distributionsys.backend.dtos.response.TablePagesResponse;
import com.distributionsys.backend.entities.sql.Supplier;
import com.distributionsys.backend.enums.ErrorCodes;
import com.distributionsys.backend.enums.PageEnum;
import com.distributionsys.backend.exceptions.ApplicationException;
import com.distributionsys.backend.mappers.PageMappers;
import com.distributionsys.backend.mappers.SupplierMappers;
import com.distributionsys.backend.repositories.GoodsRepository;
import com.distributionsys.backend.repositories.SupplierRepository;
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
public class SupplierService {
    SupplierRepository supplierRepository;
    GoodsRepository goodsRepository;
    PageMappers pageMappers;
    SupplierMappers supplierMappers;

    public TablePagesResponse<Supplier> getSuppliersPages(PaginatedTableRequest request) {
        Pageable pageableCf = pageMappers.tablePageRequestToPageable(request).toPageable(Supplier.class);
        if (Objects.isNull(request.getFilterFields()) || request.getFilterFields().isEmpty()) {
            Page<Supplier> repoRes = supplierRepository.findAll(pageableCf);
            return TablePagesResponse.<Supplier>builder()
                .data(repoRes.stream().toList())
                .totalPages(repoRes.getTotalPages())
                .currentPage(request.getPage())
                .build();
        }
        try {
            var supplierInfo = Supplier.buildFromFilterHashMap(request.getFilterFields());
            Page<Supplier> repoRes = supplierRepository.findAllBySupplierFilterInfo(supplierInfo, pageableCf);
            return TablePagesResponse.<Supplier>builder()
                .data(repoRes.stream().toList())
                .totalPages(repoRes.getTotalPages())
                .currentPage(request.getPage())
                .build();
        } catch (ApplicationException | NullPointerException | IllegalArgumentException | NoSuchFieldException e) {
            throw new ApplicationException(ErrorCodes.INVALID_FILTERING_FIELD_OR_VALUE);
        }
    }

    public void addSupplier(NewSupplierRequest request) {
        if (supplierRepository.existsBySupplierName(request.getSupplierName()))
            throw new ApplicationException(ErrorCodes.DUPLICATE_SUPPLIER);
        supplierRepository.save(supplierMappers.newToSupplier(request));
    }

    public void deleteSupplier(ByIdDto request) {
        if (goodsRepository.existsBySupplierSupplierId(request.getId()))
            throw new ApplicationException(ErrorCodes.DELETE_SUPPLIER);
        //--Catch error before JPA does it to make returned message more meaningful.
        if (!supplierRepository.existsById(request.getId()))
            throw new ApplicationException(ErrorCodes.INVALID_PRIMARY);
        supplierRepository.deleteById(request.getId());
    }

    public void updateSupplier(UpdateSupplierRequest request) {
        Supplier updatedSupplier = supplierRepository
            .findById(request.getSupplierId())
            .orElseThrow(() -> new ApplicationException(ErrorCodes.INVALID_PRIMARY));
        if (goodsRepository.existsBySupplierSupplierId(request.getSupplierId()))
            throw new ApplicationException(ErrorCodes.UPDATE_SUPPLIER);
        if (supplierRepository.existsBySupplierNameIgnoreUpdatedCase(request.getSupplierName(),
            updatedSupplier.getSupplierName()))
            throw new ApplicationException(ErrorCodes.DUPLICATE_SUPPLIER);
        supplierMappers.updateSupplier(updatedSupplier, request);
        supplierRepository.updateSupplierBySupplierInfo(updatedSupplier);
    }
}
