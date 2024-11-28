package com.distributionsys.backend.repositories;

import com.distributionsys.backend.annotations.dev.Overload;
import com.distributionsys.backend.dtos.utils.ImportBillFilterRequest;
import com.distributionsys.backend.entities.sql.ImportBill;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ImportBillRepository extends JpaRepository<ImportBill, Long> {

    Page<ImportBill> findAllByClientInfoClientInfoId(Long clientInfoId, Pageable pageableCf);

    @Overload
    @Query("""
        SELECT b FROM ImportBill b
        WHERE b.clientInfo.clientInfoId = :clientInfoId
        AND (:#{#filterObj.fromCreatedTime} IS NULL OR :#{#filterObj.fromCreatedTime} <= b.createdTime)
        AND (:#{#filterObj.toCreatedTime} IS NULL OR b.createdTime <= :#{#filterObj.toCreatedTime})
        ORDER BY b.createdTime DESC
    """)
    Page<ImportBill> findAllByClientInfoClientInfoId(
        @Param("clientInfoId") Long clientInfoId,
        @Param("filterObj") ImportBillFilterRequest filterObj,
        Pageable pageableCf);
}
