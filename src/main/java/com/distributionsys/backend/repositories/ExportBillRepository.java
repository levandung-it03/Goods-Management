package com.distributionsys.backend.repositories;

import com.distributionsys.backend.annotations.dev.Overload;
import com.distributionsys.backend.dtos.response.ExportImportTrendResponse;
import com.distributionsys.backend.dtos.utils.ExportBillFilterRequest;
import com.distributionsys.backend.entities.sql.ExportBill;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExportBillRepository extends JpaRepository<ExportBill, Long> {

    Page<ExportBill> findAllByClientInfoClientInfoId(Long clientInfoId, Pageable pageableCf);

    @Overload
    @Query("""
        SELECT b FROM ExportBill b
        WHERE b.clientInfo.clientInfoId = :clientInfoId
        AND (:#{#filterObj.exportBillId} IS NULL OR b.exportBillId = :#{#filterObj.exportBillId})
        AND (:#{#filterObj.receiverName} IS NULL OR b.receiverName LIKE CONCAT('%',:#{#filterObj.receiverName},'%'))
        AND (:#{#filterObj.fromCreatedTime} IS NULL OR :#{#filterObj.fromCreatedTime} <= b.createdTime)
        AND (:#{#filterObj.toCreatedTime} IS NULL OR b.createdTime <= :#{#filterObj.toCreatedTime})
        ORDER BY b.createdTime DESC
    """)
    Page<ExportBill> findAllByClientInfoClientInfoId(
        @Param("clientInfoId") Long clientInfoId,
        @Param("filterObj") ExportBillFilterRequest filterObj,
        Pageable pageableCf);

    // @Query("""
    //     SELECT
    //         SUM(g.unitPrice * selected_g.goodsQuantity)
    //     FROM
    //         Goods as g
    //     INNER JOIN (
    //         SELECT
    //             eb.exportBillId AS exportBillId,
    //             eb_detail.warehouseGoods.id AS goodsId,
    //             eb_detail.goodsQuantity AS goodsQuantity
    //         FROM
    //             ExportBill AS eb
    //         INNER JOIN
    //             ExportBillWarehouseGoods AS eb_detail
    //                 ON eb.exportBillId = eb_detail.exportBill.exportBillId
    //     ) AS selected_g
    //         ON g.goodsId = selected_g.goodsId
    //     WHERE
    //         selected_g.exportBillId = :exportId
    // """)

    @Query("""
        SELECT SUM(ewhg.warehouseGoods.goods.unitPrice * ewhg.goodsQuantity) FROM ExportBillWarehouseGoods ewhg
        WHERE ewhg.exportBill.exportBillId = :exportId
    """)
    Double totalExportBillByExportId(@Param("exportId") Long exportId);

    @Query("SELECT eb FROM ExportBill eb WHERE eb.clientInfo.clientInfoId = :clientInfoId ORDER BY eb.createdTime DESC")
    List<ExportBill> findTop5ByClientInfoIdOrderByCreatedTimeDesc(Long clientInfoId, PageRequest pageRequest);

    @Query("SELECT COUNT(e) FROM ExportBill e WHERE e.clientInfo.clientInfoId = :clientInfoId")
    Long countExportBillsByClientInfoId(@Param("clientInfoId") Long clientInfoId);

    ExportBill findByExportBillId(Long exportBillId);

    @Query("SELECT FUNCTION('DATE_FORMAT', e.createdTime, '%Y-%m') AS month, SUM(ebg.goodsQuantity) AS quantityExported " +
            "FROM ExportBill e " +
            "JOIN ExportBillWarehouseGoods ebg ON e.exportBillId = ebg.exportBill.exportBillId " +
            "WHERE e.createdTime BETWEEN :startDate AND :endDate " +
            "GROUP BY FUNCTION('DATE_FORMAT', e.createdTime, '%Y-%m') " +
            "ORDER BY month")
    List<Object[]> findExportDataByDateRange(@Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate);





}
