package com.distributionsys.backend.repositories;

import com.distributionsys.backend.dtos.utils.GoodsFromWarehouseFilterRequest;
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
        WHERE CONCAT(wg.goods.goodsId, ",", wg.warehouse.warehouseId) IN :request
    """)
    List<WarehouseGoods> findAllByGoodsIdAndWarehouseIdPairs(@Param("request") List<String> warehouseGoodsIdPairs);

    Page<WarehouseGoods> findAllByGoodsGoodsId(Long goodsId, Pageable pageableCf);

    @Query("""
        SELECT wg FROM WarehouseGoods wg
        WHERE wg.goods.goodsId = :goodsId
        AND (:#{#filterObj.warehouseGoodsId} IS NULL OR wg.warehouseGoodsId = :#{#filterObj.warehouseGoodsId})
        AND (:#{#filterObj.warehouseId} IS NULL OR wg.warehouse.warehouseId = :#{#filterObj.warehouseId})
        AND (:#{#filterObj.warehouseName} IS NULL OR wg.warehouse.warehouseName LIKE CONCAT('%',:#{#filterObj.warehouseName},'%'))
        AND (:#{#filterObj.address} IS NULL OR wg.warehouse.address LIKE CONCAT('%',:#{#filterObj.address},'%'))
        AND (:#{#filterObj.currentQuantity} IS NULL OR wg.currentQuantity = :#{#filterObj.currentQuantity})
    """)
    Page<WarehouseGoods> findAllByGoodsGoodsIdAndFilterData(
        @Param("goodsId") Long goodsId,
        @Param("filterObj") GoodsFromWarehouseFilterRequest filterInfo,
        Pageable pageableCf);
}
