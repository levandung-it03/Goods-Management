package com.distributionsys.backend.services.redis;

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
        return warehouseGoodsOps.opsForSet()
            .members("FluxedGoods:userEmail:" + userEmail)
            .flatMap(e -> warehouseGoodsOps.opsForHash().delete("FluxedGoods:" + e.getId()))
            .then();
    }

    public Flux<List<FluxedGoodsFromWarehouse>> getFluxedGoodsByUserEmail(String userEmail) {
        return warehouseGoodsOps.opsForSet()
            .members("FluxedGoods:userEmail:" + userEmail)
            .flatMap(e ->
                warehouseGoodsOps.opsForValue()
                    .get("FluxedGoods:" + e.getId())
                    .switchIfEmpty(Mono.empty())
            ).collectList().flux()
            .filter(l -> !l.isEmpty());
    }
}
