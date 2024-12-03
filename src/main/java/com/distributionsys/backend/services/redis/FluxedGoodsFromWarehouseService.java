package com.distributionsys.backend.services.redis;

import com.distributionsys.backend.dtos.response.FluxedGoodsQuantityResponse;
import com.distributionsys.backend.entities.redis.FluxedGoodsFromWarehouse;
import com.distributionsys.backend.entities.sql.relationships.WarehouseGoods;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FluxedGoodsFromWarehouseService {
    private final ReactiveRedisConnectionFactory factory;
    private final ReactiveRedisOperations<String, FluxedGoodsFromWarehouse> warehouseGoodsOps;

    public void saveFluxedGoodsOfCurrentSession(String email, List<WarehouseGoods> goodsFromWarehouses) {
        factory.getReactiveConnection()
            .serverCommands()
            .flushAll()
            .thenMany(Flux.fromIterable(goodsFromWarehouses)
                .map(warehouseGoods -> FluxedGoodsFromWarehouse.builder()
                    .id(UUID.randomUUID().toString())
                    .goodsFromWarehouseId(warehouseGoods.getWarehouse().getWarehouseId())
                    .currentQuantity(warehouseGoods.getCurrentQuantity())
                    .userEmail(email)
                    .build())
            ).flatMap(savedObj -> Mono.when(
                warehouseGoodsOps.opsForValue().set(savedObj.getId(), savedObj),
                warehouseGoodsOps.opsForHash()
                    .put("FluxedGoods:userEmail:" + savedObj.getUserEmail(), savedObj.getUserEmail(), savedObj)
            )).subscribe(System.out::println);
    }

    public Mono<Void> clearFluxedGoodsOfCurrentSession(String userEmail) {
        return warehouseGoodsOps.keys("FluxedGoodsFromWarehouse:" + userEmail + "_*")
            .flatMap(warehouseGoodsOps::delete)
            .then();
    }

    public Flux<List<FluxedGoodsQuantityResponse>> getFluxedGoodsByUserEmail(String userEmail) {
        return warehouseGoodsOps.keys("FluxedGoodsFromWarehouse:" + userEmail + "_*")
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
