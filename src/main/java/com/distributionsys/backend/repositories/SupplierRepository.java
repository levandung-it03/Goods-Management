package com.distributionsys.backend.repositories;

import com.distributionsys.backend.annotations.dev.OptimizedQuery;
import com.distributionsys.backend.entities.sql.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    @Query("""
        SELECT s FROM Supplier s
        WHERE (:#{#filterObj.supplierId} IS NULL OR s.supplierId = :#{#filterObj.supplierId})
        AND (:#{#filterObj.supplierName} IS NULL OR s.supplierName LIKE CONCAT('%', :#{#filterObj.supplierName}, '%'))
    """)
    Page<Supplier> findAllBySupplierFilterInfo(@Param("filterObj") Supplier supplierInfo, Pageable pageableCf);

    @Transactional
    @Modifying
    @Query("UPDATE Supplier s SET s.supplierName = :#{#newInfo.supplierName} WHERE s.supplierId = :#{#newInfo.supplierId}")
    void updateSupplierBySupplierInfo(@Param("newInfo") Supplier supplier);

    @Query("""
        SELECT CASE WHEN COUNT(s) > 0 THEN TRUE ELSE FALSE END FROM Supplier s
        WHERE LOWER(s.supplierName) = LOWER(:findingName) AND LOWER(s.supplierName) <> LOWER(:ignoredName)
    """)
    boolean existsBySupplierNameIgnoreUpdatedCase(
        @Param("findingName") String findingName,
        @Param("ignoredName") String ignoredName);

    boolean existsBySupplierName(String supplierName);

    @OptimizedQuery
    @Query(value = """
        SELECT s.supplier_id, s.supplier_name FROM supplier s
        INNER JOIN (
            SELECT sp.supplier_id FROM supplier sp
            WHERE :supplierName IS NULL OR TRIM(:supplierName) = '' OR sp.supplier_name LIKE CONCAT('%', :supplierName, '%')
            ORDER BY sp.supplier_id, sp.supplier_name
            LIMIT :pageSize OFFSET :offset
        ) AS found_ids
        ON found_ids.supplier_id = s.supplier_id
    """, nativeQuery = true)
    List<Object[]> findAllSimpleSupplierInfoBySupplierName(
        @Param("supplierName") String supplierName,
        @Param("pageSize") Integer pageSize,
        @Param("offset") Integer offset);
}
