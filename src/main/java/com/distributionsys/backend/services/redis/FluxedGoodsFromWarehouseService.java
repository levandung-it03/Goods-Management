package com.distributionsys.backend.services.redis;

import com.distributionsys.backend.entities.redis.FluxedGoodsFromWarehouse;
import com.distributionsys.backend.entities.sql.relationships.WarehouseGoods;
import com.distributionsys.backend.repositories.FluxedGoodsFromWarehouseCrud;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FluxedGoodsFromWarehouseService {
    FluxedGoodsFromWarehouseCrud fluxedGoodsFromWarehouseCrud;

    public void saveFluxedGoodsOfCurrentSession(
        Long userId, List<WarehouseGoods> goodsFromWarehouses) {
        List<FluxedGoodsFromWarehouse> savedList = goodsFromWarehouses.stream()
            .map(obj -> FluxedGoodsFromWarehouse.builder()
                .userId(userId)
                .goodsFromWarehouseId(obj.getId())
                .currentQuantity(obj.getCurrentQuantity())
                .build())
            .toList();
        fluxedGoodsFromWarehouseCrud.saveAll(savedList);
    }

    public void clearFluxedGoodsOfCurrentSession(Long userId) {
        fluxedGoodsFromWarehouseCrud.deleteAllByUserId(userId);
    }

    public List<FluxedGoodsFromWarehouse> findAllByUserId(Long userId) {
        return fluxedGoodsFromWarehouseCrud.findAllByUserId(userId);
    }
}
