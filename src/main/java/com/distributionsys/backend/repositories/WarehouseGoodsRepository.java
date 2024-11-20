package com.distributionsys.backend.repositories;

import com.distributionsys.backend.entities.sql.relationships.WarehouseGoods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WarehouseGoodsRepository extends JpaRepository<WarehouseGoods, Long> {
    boolean existsByWarehouseWarehouseId(Long id);
}
