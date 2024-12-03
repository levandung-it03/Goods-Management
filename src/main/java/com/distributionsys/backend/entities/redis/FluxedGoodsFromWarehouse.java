package com.distributionsys.backend.entities.redis;

import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.RedisHash;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash(value = "FGFWH")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FluxedGoodsFromWarehouse {
    public static String NAME = "FGFWH";
    public static String GOODS_FROM_WAREHOUSE_ID = NAME + "&GFWHID";

    @Id
    String id;
    Long goodsFromWarehouseId;
    Long currentQuantity;
}
