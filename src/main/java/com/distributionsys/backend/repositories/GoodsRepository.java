package com.distributionsys.backend.repositories;

import com.distributionsys.backend.entities.sql.Goods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoodsRepository extends JpaRepository<Goods, Long> {
    boolean existsBySupplierSupplierId(Long supplierId);
}
