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
public class ExportBillWarehouseGoodsFilterRequest {
    Long exportBillWarehouseGoodsId;
    Long goodsId;
    String goodsName;
    Float unitPrice;
    String supplierName;
    String warehouseName;
    Long goodsQuantity;

    public static ExportBillWarehouseGoodsFilterRequest buildFromFilterHashMap(HashMap<String, Object> map)
        throws NullPointerException, IllegalArgumentException, NoSuchFieldException {
        for (String key: map.keySet())
            if (Arrays.stream(ExportBillWarehouseGoodsFilterRequest.class.getDeclaredFields())
                .noneMatch(f -> f.getName().equals(key)))
                throw new NoSuchFieldException();

        var result = new ExportBillWarehouseGoodsFilterRequest();
        result.setExportBillWarehouseGoodsId(!map.containsKey("exportBillWarehouseGoodsId") ? null
            : Long.parseLong(map.get("exportBillWarehouseGoodsId").toString()));
        result.setGoodsId(!map.containsKey("goodsId") ? null : Long.parseLong(map.get("goodsId").toString()));
        result.setGoodsName(!map.containsKey("goodsName") ? null : map.get("goodsName").toString());
        result.setUnitPrice(!map.containsKey("unitPrice") ? null : Float.parseFloat(map.get("unitPrice").toString()));
        result.setSupplierName(!map.containsKey("supplierName") ? null : map.get("supplierName").toString());
        result.setWarehouseName(!map.containsKey("warehouseName") ? null : map.get("warehouseName").toString());
        result.setGoodsQuantity(!map.containsKey("goodsQuantity") ? null
            : Long.parseLong(map.get("goodsQuantity").toString()));
        return result;
    }
}