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
public class WarehouseGoodsFilterRequest {
    String goodsName;
    Float unitPrice;
    String supplierName;
    String warehouseName;
    String address;
    Long currentQuantity;

    public static WarehouseGoodsFilterRequest builderFormFilterHashMap(HashMap<String, Object> map)
        throws NullPointerException, NoSuchFieldException, IllegalArgumentException {
        for (String key: map.keySet())
            if (Arrays.stream(WarehouseGoodsFilterRequest.class.getDeclaredFields())
                .noneMatch(f -> f.getName().equals(key)))
                    throw new NoSuchFieldException();

        var result = new WarehouseGoodsFilterRequest();
        result.setGoodsName(map.containsKey("goodsName") ? map.get("goodsName").toString() : null);
        result.setUnitPrice(map.containsKey("unitPrice") ? Float.parseFloat(map.get("unitPrice").toString()) : null);
        result.setSupplierName(map.containsKey("supplierName") ? map.get("supplierName").toString() : null);
        result.setWarehouseName(map.containsKey("warehouseName") ? map.get("warehouseName").toString() : null);
        result.setAddress(map.containsKey("address") ? map.get("address").toString() : null);
        result.setCurrentQuantity(map.containsKey("currentQuantity")
            ? Long.parseLong(map.get("currentQuantity").toString())
            : null);
        return result;
    }
}
