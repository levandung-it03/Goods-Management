package com.distributionsys.backend.repositories;

import com.distributionsys.backend.entities.sql.Warehouse;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    @Query("""
        SELECT w FROM Warehouse w
        WHERE (:#{#filterObj.warehouseName} IS NULL OR w.warehouseName LIKE CONCAT('%',:#{#filterObj.warehouseName},'%'))
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

}
