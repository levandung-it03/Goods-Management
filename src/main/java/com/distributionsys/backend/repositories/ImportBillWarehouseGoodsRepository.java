package com.distributionsys.backend.repositories;

import com.distributionsys.backend.dtos.utils.ImportBillWarehouseGoodsFilterRequest;
import com.distributionsys.backend.dtos.utils.WarehouseGoodsFilterRequest;
import com.distributionsys.backend.entities.sql.relationships.ImportBillWarehouseGoods;
import com.distributionsys.backend.entities.sql.relationships.WarehouseGoods;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImportBillWarehouseGoodsRepository extends JpaRepository<ImportBillWarehouseGoods, Long> {

    @Query("""
        SELECT CASE WHEN COUNT(b) > 0 THEN TRUE ELSE FALSE END FROM ImportBillWarehouseGoods b
        WHERE b.warehouseGoods.goods.goodsId = :goodsId
    """)
    boolean existsGoodsByGoodsId(@Param("goodsId") Long goodsId);

    @Query("SELECT r.warehouseGoods FROM ImportBillWarehouseGoods r WHERE r.importBill.importBillId = :billId")
    Page<WarehouseGoods> findWarehouseGoodsAllByImportBillId(@Param("billId") Long id, Pageable pageableCf);

    @Query("""
        SELECT r.warehouseGoods FROM ImportBillWarehouseGoods r
        WHERE r.importBill.importBillId = :billId
        AND (:#{#filterObj.warehouseName} IS NULL
            OR r.warehouseGoods.warehouse.warehouseName LIKE CONCAT('%',:#{#filterObj.warehouseName},'%'))
        AND (:#{#filterObj.goodsName} IS NULL
            OR r.warehouseGoods.goods.goodsName LIKE CONCAT('%',:#{#filterObj.goodsName},'%'))
        AND (:#{#filterObj.supplierName} IS NULL
            OR r.warehouseGoods.goods.supplier.supplierName LIKE CONCAT('%',:#{#filterObj.supplierName},'%'))
        AND (:#{#filterObj.address} IS NULL
            OR r.warehouseGoods.warehouse.address LIKE CONCAT('%',:#{#filterObj.address},'%'))
        AND (:#{#filterObj.unitPrice} IS NULL OR r.warehouseGoods.goods.unitPrice = :#{#filterObj.unitPrice})
        AND (:#{#filterObj.currentQuantity} IS NULL
            OR r.warehouseGoods.currentQuantity = :#{#filterObj.currentQuantity})
        ORDER BY r.warehouseGoods.goods.goodsName ASC
    """)
    Page<WarehouseGoods> findWarehouseGoodsAllByImportBillId(
        @Param("billId") Long id,
        @Param("filterObj") WarehouseGoodsFilterRequest filterObj,
        Pageable pageableCf);

    List<ImportBillWarehouseGoods> findByImportBill_ImportBillId(Long importBillId);

    @Query("""
        SELECT i FROM ImportBillWarehouseGoods i
        WHERE i.importBill.clientInfo.clientInfoId = :clientInfoId
        AND i.importBill.importBillId = :importBillId
    """)
    Page<ImportBillWarehouseGoods> findAllByClientInfoIdAndImportBillId(
        @Param("clientInfoId") Long clientInfoId,
        @Param("importBillId") Long importBillId,
        Pageable pageableCf);

    @Query("""
        SELECT i FROM ImportBillWarehouseGoods i
        WHERE i.importBill.clientInfo.clientInfoId = :clientInfoId
        AND i.importBill.importBillId = :importBillId
        AND (:#{#filterObj.importBillWarehouseGoodsId} IS NULL
            OR i.importBillWarehouseGoodsId = :#{#filterObj.importBillWarehouseGoodsId})
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
    Page<ImportBillWarehouseGoods> findAllByClientInfoIdAndImportBillIdAndFilterInfo(
        @Param("filterObj") ImportBillWarehouseGoodsFilterRequest filterInfo,
        @Param("clientInfoId") Long clientInfoId,
        @Param("importBillId") Long importBillId,
        Pageable pageableCf);
}
