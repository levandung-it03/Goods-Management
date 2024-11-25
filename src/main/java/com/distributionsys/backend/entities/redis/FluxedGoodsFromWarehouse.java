package com.distributionsys.backend.entities.redis;

import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.RedisHash;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash(value = "FluxedGoods")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FluxedGoodsFromWarehouse {
    @Id
    Long id;
    Long userId;
    Long goodsFromWarehouseId;
    Long currentQuantity;
}
