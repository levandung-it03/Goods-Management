package com.distributionsys.backend.repositories;

import com.distributionsys.backend.entities.redis.FluxedGoodsFromWarehouse;
import org.reactivestreams.Publisher;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FluxedGoodsFromWarehouseCrud extends CrudRepository<FluxedGoodsFromWarehouse, String> {
}
