package com.distributionsys.backend.entities.redis;

import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.RedisHash;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash(value = "FluxedGoodsFromWarehouse")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FluxedGoodsFromWarehouse {
    @Id
    String id;
    String userEmail;
    Long goodsFromWarehouseId;
    Long currentQuantity;
}
