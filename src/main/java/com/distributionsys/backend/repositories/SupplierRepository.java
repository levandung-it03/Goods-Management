package com.distributionsys.backend.repositories;

import com.distributionsys.backend.dtos.request.SupplierRequest;
import com.distributionsys.backend.entities.sql.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    @Query("""
        SELECT s FROM Supplier s
        WHERE (:#{#filterObj.supplierName} IS NULL OR s.supplierName LIKE CONCAT('%', :#{#filterObj.supplierName}, '%'))
    """)
    Page<Supplier> findAllBySupplierFilterInfo(
        @Param("filterObj") SupplierRequest supplierInfo,
        Pageable pageableCf
    );

    @Query("""
        UPDATE FROM Supplier s SET s.supplierName = :#{#newInfo.supplierName}
        WHERE s.supplierId = :#{#newInfo.supplierId}
    """)
    @Modifying
    @Transactional
    void updateSupplierBySupplierInfo(@Param("newInfo") Supplier supplier);

    boolean existsBySupplierName(String supplierName);
}
