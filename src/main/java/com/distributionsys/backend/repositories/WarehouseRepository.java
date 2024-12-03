package com.distributionsys.backend.repositories;

import com.distributionsys.backend.annotations.dev.OptimizedQuery;
import com.distributionsys.backend.entities.sql.Warehouse;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    @Query("""
        SELECT w FROM Warehouse w
        WHERE (:#{#filterObj.warehouseId} IS NULL OR w.warehouseId = :#{#filterObj.warehouseId})
        AND (:#{#filterObj.warehouseName} IS NULL OR w.warehouseName LIKE CONCAT('%',:#{#filterObj.warehouseName},'%'))
        AND (:#{#filterObj.address} IS NULL OR w.address LIKE CONCAT('%',:#{#filterObj.address},'%'))
    """)
    Page<Warehouse> findAllByWarehouseFilterInfo(@Param("filterObj") Warehouse warehouseInfo, Pageable pageableCf);

    boolean existsByWarehouseName(String warehouseName);

    @Query("""
        UPDATE Warehouse w
        SET w.warehouseName = :#{#filterObj.warehouseName}, w.address = :#{#filterObj.address}
        WHERE w.warehouseId = :#{#filterObj.warehouseId}
    """)

    @Modifying
    @Transactional
    void updateWarehouseByWarehouseInfo(@Param("filterObj") Warehouse updatedWarehouse);

    @OptimizedQuery
    @Query(value = """
        SELECT w.warehouse_id, w.warehouse_name FROM warehouse w
        INNER JOIN (
            SELECT wh.warehouse_id FROM warehouse wh
            WHERE :warehouseName IS NULL OR TRIM(:warehouseName) = ''
                OR wh.warehouse_name LIKE CONCAT('%', :warehouseName, '%')
            ORDER BY wh.warehouse_id, wh.warehouse_name
            LIMIT :pageSize OFFSET :offset
        ) AS found_ids
        ON found_ids.warehouse_id = w.warehouse_id
    """, nativeQuery = true)
    List<Object[]> findAllSimpleWarehouseInfoByWarehouseName(
        @Param("warehouseName") String warehouseName,
        @Param("pageSize") Integer pageSize,
        @Param("offset") Integer offset);
}
