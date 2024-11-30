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
public class GoodsFilterRequest {
    Long goodsId;
    String goodsName;
    Float unitPrice;
    String supplierName;

    public static GoodsFilterRequest buildFromFilterHashMap(HashMap<String, Object> map)
        throws NullPointerException, NoSuchFieldException, IllegalArgumentException {
        for (String key: map.keySet())
            if (Arrays.stream(GoodsFilterRequest.class.getDeclaredFields())
                .noneMatch(f -> f.getName().equals(key)))
                throw new NoSuchFieldException();

        var result = new GoodsFilterRequest();
        result.setGoodsId(map.containsKey("goodsId") ? Long.parseLong(map.get("goodsId").toString()) : null);
        result.setGoodsName(map.containsKey("goodsName") ? map.get("goodsName").toString() : null);
        result.setUnitPrice(map.containsKey("unitPrice") ? Float.parseFloat(map.get("unitPrice").toString()) : null);
        result.setSupplierName(map.containsKey("supplierName") ? map.get("supplierName").toString() : null);
        return result;
    }
}
