package com.distributionsys.backend.repositories;

import com.distributionsys.backend.annotations.dev.Overload;
import com.distributionsys.backend.dtos.utils.ImportBillFilterRequest;
import com.distributionsys.backend.entities.sql.ImportBill;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ImportBillRepository extends JpaRepository<ImportBill, Long> {

    Page<ImportBill> findAllByClientInfoClientInfoId(Long clientInfoId, Pageable pageableCf);

    @Overload
    @Query("""
        SELECT b FROM ImportBill b
        WHERE b.clientInfo.clientInfoId = :clientInfoId
        AND (:#{#filterObj.importBillId} IS NULL OR b.importBillId = :#{#filterObj.importBillId})
        AND (:#{#filterObj.fromCreatedTime} IS NULL OR :#{#filterObj.fromCreatedTime} <= b.createdTime)
        AND (:#{#filterObj.toCreatedTime} IS NULL OR b.createdTime <= :#{#filterObj.toCreatedTime})
        ORDER BY b.createdTime DESC
    """)
    Page<ImportBill> findAllByClientInfoClientInfoId(
        @Param("clientInfoId") Long clientInfoId,
        @Param("filterObj") ImportBillFilterRequest filterObj,
        Pageable pageableCf);

    @Query("SELECT ib FROM ImportBill ib WHERE ib.clientInfo.clientInfoId = :clientInfoId ORDER BY ib.createdTime DESC")
    List<ImportBill> findTop5ByClientInfoIdOrderByCreatedTimeDesc(Long clientInfoId, PageRequest pageRequest);

    @Query("""
        SELECT
            SUM(g.unitPrice * selected_g.goodsQuantity)
        FROM
            Goods as g
        INNER JOIN (
            SELECT
                ib.importBillId AS importBillId,
                ib_detail.warehouseGoods.id AS goodsId,
                ib_detail.goodsQuantity AS goodsQuantity
            FROM
                ImportBill AS ib
            INNER JOIN
                ImportBillWarehouseGoods AS ib_detail
                    ON ib.importBillId = ib_detail.importBill.importBillId
        ) AS selected_g
            ON g.goodsId = selected_g.goodsId
        WHERE
            selected_g.importBillId = :importId
    """)
    Double totalImportBillByImportId(@Param("importId") Long importId);

    @Query("SELECT COUNT(i) FROM ImportBill i WHERE i.clientInfo.clientInfoId = :clientInfoId")
    Long countImportBillsByClientInfoId(@Param("clientInfoId") Long clientInfoId);

    @Query("SELECT FUNCTION('DATE_FORMAT', i.createdTime, '%Y-%m') AS month, SUM(ibw.goodsQuantity) AS quantityImported " +
            "FROM ImportBill i " +
            "JOIN ImportBillWarehouseGoods ibw ON i.importBillId = ibw.importBill.importBillId " +
            "WHERE i.createdTime BETWEEN :startDate AND :endDate " +
            "GROUP BY FUNCTION('DATE_FORMAT', i.createdTime, '%Y-%m') " +
            "ORDER BY month")
    List<Object[]> findImportDataByDateRange(@Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate);

}
