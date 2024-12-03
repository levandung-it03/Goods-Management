package com.distributionsys.backend.repositories;

import com.distributionsys.backend.dtos.utils.ExportBillWarehouseGoodsFilterRequest;
import com.distributionsys.backend.entities.sql.relationships.ExportBillWarehouseGoods;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExportBillWarehouseGoodsRepository extends JpaRepository<ExportBillWarehouseGoods, Long> {

    @Query("""
        SELECT CASE WHEN COUNT(b) > 0 THEN TRUE ELSE FALSE END FROM ExportBillWarehouseGoods b
        WHERE b.warehouseGoods.goods.goodsId = :goodsId
    """)
    boolean existsGoodsByGoodsId(@Param("goodsId") Long goodsId);

    List<ExportBillWarehouseGoods> findByExportBill_ExportBillId(Long exportBillId);

    @Query("""
        SELECT i FROM ExportBillWarehouseGoods i
        WHERE i.exportBill.clientInfo.clientInfoId = :clientInfoId
        AND i.exportBill.exportBillId = :exportBillId
    """)
    Page<ExportBillWarehouseGoods> findAllByClientInfoIdAndExportBillId(
        @Param("clientInfoId") Long clientInfoId,
        @Param("exportBillId") Long importBillId,
        Pageable pageableCf);

    @Query("""
        SELECT i FROM ExportBillWarehouseGoods i
        WHERE i.exportBill.clientInfo.clientInfoId = :clientInfoId
        AND i.exportBill.exportBillId = :exportBillId
        AND (:#{#filterObj.exportBillWarehouseGoodsId} IS NULL
            OR i.exportBillWarehouseGoodsId = :#{#filterObj.exportBillWarehouseGoodsId})
        AND (:#{#filterObj.goodsId} IS NULL OR i.warehouseGoods.goods.goodsId = :#{#filterObj.goodsId})
        AND (:#{#filterObj.goodsName} IS NULL
            OR i.warehouseGoods.goods.goodsName LIKE CONCAT('%',:#{#filterObj.goodsName},'%'))
        AND (:#{#filterObj.unitPrice} IS NULL OR i.warehouseGoods.goods.unitPrice = :#{#filterObj.unitPrice})
        AND (:#{#filterObj.supplierName} IS NULL
            OR i.warehouseGoods.goods.supplier.supplierName LIKE CONCAT('%',:#{#filterObj.supplierName},'%'))
        AND (:#{#filterObj.warehouseName} IS NULL
            OR i.warehouseGoods.warehouse.warehouseName LIKE CONCAT('%',:#{#filterObj.warehouseName},'%'))
        AND (:#{#filterObj.goodsQuantity} IS NULL OR i.goodsQuantity = :#{#filterObj.goodsQuantity})
    """)
    Page<ExportBillWarehouseGoods> findAllByClientInfoIdAndExportBillIdAndFilterInfo(
        @Param("filterObj") ExportBillWarehouseGoodsFilterRequest filterInfo,
        @Param("clientInfoId") Long clientInfoId,
        @Param("exportBillId") Long importBillId,
        Pageable pageableCf);
}
