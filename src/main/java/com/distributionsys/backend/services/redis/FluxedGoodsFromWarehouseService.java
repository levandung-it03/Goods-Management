package com.distributionsys.backend.services.redis;

import com.distributionsys.backend.dtos.response.FluxedGoodsQuantityResponse;
import com.distributionsys.backend.entities.redis.FluxedGoodsFromWarehouse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FluxedGoodsFromWarehouseService {
    private final ReactiveRedisOperations<String, FluxedGoodsFromWarehouse> warehouseGoodsOps;

    public Mono<Void> clearFluxedGoodsOfCurrentSession(String userEmail) {
        return warehouseGoodsOps.keys(FluxedGoodsFromWarehouse.NAME + ":" + userEmail + "*")
            .flatMap(warehouseGoodsOps::delete)
            .then();
    }

    public Flux<List<FluxedGoodsQuantityResponse>> getFluxedGoodsByUserEmail(String userEmail) {
        return warehouseGoodsOps.keys(FluxedGoodsFromWarehouse.NAME + ":" + userEmail + "*")
            .flatMap(key -> warehouseGoodsOps.opsForHash()
                .entries(key)
                .collectMap(Map.Entry::getKey, Map.Entry::getValue)
                .map(entry -> FluxedGoodsQuantityResponse.builder()
                    .warehouseGoodsId(Long.parseLong(entry.get("goodsFromWarehouseId").toString()))
                    .currentQuantity(Long.parseLong(entry.get("currentQuantity").toString()))
                    .build())
            ).collectList().flux();
    }
}
