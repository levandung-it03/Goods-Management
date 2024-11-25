package com.distributionsys.backend.repositories;

import com.distributionsys.backend.entities.sql.relationships.ExportBillWarehouseGoods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExportBillWarehouseGoodsRepository extends JpaRepository<ExportBillWarehouseGoods, Long> {

    @Query("""
        SELECT CASE WHEN COUNT(b) > 0 THEN TRUE ELSE FALSE END FROM ExportBillWarehouseGoods b
        WHERE b.warehouseGoods.goods.goodsId = :goodsId
    """)
    boolean existsGoodsByGoodsId(@Param("goodsId") Long goodsId);
}
