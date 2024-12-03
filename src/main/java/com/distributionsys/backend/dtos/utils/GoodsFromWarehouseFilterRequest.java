package com.distributionsys.backend.dtos.utils;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;
import java.util.HashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GoodsFromWarehouseFilterRequest {
    Long warehouseGoodsId;
    Long warehouseId;
    String warehouseName;
    String address;
    Long currentQuantity;

    public static GoodsFromWarehouseFilterRequest buildFromFilterHashMap(HashMap<String, Object> map)
        throws NullPointerException, NoSuchFieldException, IllegalArgumentException {
        for (String key: map.keySet())
            if (Arrays.stream(GoodsFromWarehouseFilterRequest.class.getDeclaredFields())
                .noneMatch(f -> f.getName().equals(key)))
                throw new NoSuchFieldException();

        var result = new GoodsFromWarehouseFilterRequest();
        result.setWarehouseGoodsId(!map.containsKey("warehouseGoodsId") ? null
            : Long.parseLong(map.get("warehouseGoodsId").toString()));
        result.setWarehouseId(map.containsKey("warehouseId") ? Long.parseLong(map.get("warehouseId").toString()) : null);
        result.setWarehouseName(map.containsKey("warehouseName") ? map.get("warehouseName").toString() : null);
        result.setAddress(map.containsKey("address") ? map.get("address").toString() : null);
        result.setCurrentQuantity(map.containsKey("currentQuantity")
            ? Long.parseLong(map.get("currentQuantity").toString())
            : null);
        return result;
    }
}
