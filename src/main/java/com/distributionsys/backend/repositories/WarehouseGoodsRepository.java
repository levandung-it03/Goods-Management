package com.distributionsys.backend.repositories;

import com.distributionsys.backend.dtos.utils.WarehouseGoodsFilterRequest;
import com.distributionsys.backend.entities.sql.relationships.WarehouseGoods;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarehouseGoodsRepository extends JpaRepository<WarehouseGoods, Long> {
    boolean existsByWarehouseWarehouseId(Long id);

    @Query("""
        SELECT wg FROM WarehouseGoods wg
        WHERE (:#{#filterObj.warehouseName} IS NULL OR wg.warehouse.warehouseName LIKE CONCAT('%',:#{#filterObj.warehouseName},'%'))
        AND (:#{#filterObj.goodsName} IS NULL OR wg.goods.goodsName LIKE CONCAT('%',:#{#filterObj.goodsName},'%'))
        AND (:#{#filterObj.supplierName} IS NULL OR wg.goods.supplier.supplierName LIKE CONCAT('%',:#{#filterObj.supplierName},'%'))
        AND (:#{#filterObj.address} IS NULL OR wg.warehouse.address LIKE CONCAT('%',:#{#filterObj.address},'%'))
        AND (:#{#filterObj.unitPrice} IS NULL OR wg.goods.unitPrice = :#{#filterObj.unitPrice})
        AND (:#{#filterObj.currentQuantity} IS NULL OR wg.currentQuantity = :#{#filterObj.currentQuantity})
        ORDER BY wg.goods.goodsId ASC
    """)
    Page<WarehouseGoods> findAllByWarehouseGoodsFilterInfo(
        @Param("filterObj") WarehouseGoodsFilterRequest warehouseGoodsInfo,
        Pageable pageableCf);

    @Query("""
        SELECT wg FROM WarehouseGoods wg
        WHERE CONCAT(wg.goods.goodsId, ",", wg.warehouse.warehouseId) IN :request
    """)
    List<WarehouseGoods> findAllByGoodsIdAndWarehouseIdPairs(@Param("request") List<String> warehouseGoodsIdPairs);
}
