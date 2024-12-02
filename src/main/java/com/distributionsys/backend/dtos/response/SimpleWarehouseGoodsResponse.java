package com.distributionsys.backend.dtos.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SimpleWarehouseGoodsResponse {
    Long warehouseGoodsId;
    String goodsName;
    String warehouseName;
    String supplierName;

    public static SimpleWarehouseGoodsResponse buildFromRepoResponseObjArr(Object[] repoResponse) {
        return SimpleWarehouseGoodsResponse.builder()
            .warehouseGoodsId(Long.parseLong(repoResponse[0].toString()))
            .goodsName(repoResponse[1].toString())
            .warehouseName(repoResponse[2].toString())
            .supplierName(repoResponse[3].toString())
            .build();
    }
}
