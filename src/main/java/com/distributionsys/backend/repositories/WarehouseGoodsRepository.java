package com.distributionsys.backend.repositories;

import com.distributionsys.backend.entities.sql.relationships.WarehouseGoods;
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

    List<WarehouseGoods> findAll();
}
