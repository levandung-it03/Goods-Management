package com.distributionsys.backend.repositories;

import com.distributionsys.backend.annotations.dev.Overload;
import com.distributionsys.backend.dtos.utils.ExportBillFilterRequest;
import com.distributionsys.backend.entities.sql.ExportBill;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ExportBillRepository extends JpaRepository<ExportBill, Long> {

    Page<ExportBill> findAllByClientInfoClientInfoId(Long clientInfoId, Pageable pageableCf);

    @Overload
    @Query("""
        SELECT b FROM ExportBill b
        WHERE b.clientInfo.clientInfoId = :clientInfoId
        AND (:#{#filterObj.receiverName} IS NULL OR b.receiverName LIKE CONCAT('%',:#{#filterObj.receiverName},'%'))
        AND (:#{#filterObj.fromCreatedTime} IS NULL OR :#{#filterObj.fromCreatedTime} <= b.createdTime)
        AND (:#{#filterObj.toCreatedTime} IS NULL OR b.createdTime <= :#{#filterObj.toCreatedTime})
        AND (:#{#filterObj.exportBillStatus} IS NULL OR b.exportBillStatus = :#{#filterObj.exportBillStatus})
        ORDER BY b.createdTime DESC
    """)
    Page<ExportBill> findAllByClientInfoClientInfoId(
        @Param("clientInfoId") Long clientInfoId,
        @Param("filterObj") ExportBillFilterRequest filterObj,
        Pageable pageableCf);
}
