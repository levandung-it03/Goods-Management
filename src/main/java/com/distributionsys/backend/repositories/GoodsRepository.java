package com.distributionsys.backend.repositories;

import com.distributionsys.backend.annotations.dev.OptimizedQuery;
import com.distributionsys.backend.entities.sql.Goods;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoodsRepository extends JpaRepository<Goods, Long> {
    boolean existsBySupplierSupplierId(Long supplierId);

    @OptimizedQuery
    @Query(value = """
        SELECT g.goods_id, g.goods_name, w.warehouse_name, s.supplier_name FROM goods g
        INNER JOIN (
            SELECT gs.goods_id FROM goods gs
            WHERE :goodsName IS NULL OR gs.goods_name LIKE CONCAT('%', :goodsName, '%')
            ORDER BY gs.goods_name, gs.goods_id
            LIMIT :pageSize OFFSET :currentPage
        ) AS found_ids
        ON found_ids.goods_id = g.goods_id
        INNER JOIN warehouse_goods wg ON wg.goods_id = g.goods_id
        INNER JOIN warehouse w ON w.warehouse_id = wg.warehouse_id
        INNER JOIN supplier s ON s.supplier_id = g.supplier_id
    """, nativeQuery = true)
    List<Object[]> findAllSimpleGoodsInfoByGoodsName(
        @Param("goodsName") String goodsName,
        @Param("pageSize") Integer pageSize,
        @Param("currentPage") Integer currentPage);

    @Query("""
        SELECT CASE WHEN COUNT(g) > 0 THEN TRUE ELSE FALSE END FROM Goods g
        WHERE LOWER(g.goodsName) = LOWER(:findingName) AND LOWER(g.goodsName) <> LOWER(:ignoredName)
    """)
    boolean existsByGoodsNameIgnoreUpdatedCase(
        @Param("findingName") String findingName,
        @Param("ignoredName") String ignoredName);

    boolean existsByGoodsName(String goodsName);

    @Transactional
    @Modifying
    @Query("""
        UPDATE Goods g
        SET g.goodsName = :#{#newInfo.goodsName},
            g.supplier = :#{#newInfo.supplier},
            g.unitPrice = :#{#newInfo.unitPrice}
        WHERE g.goodsId = :#{#newInfo.goodsId}
    """)
    void updateGoodsByGoodsInfo(@Param("newInfo") Goods build);
}
