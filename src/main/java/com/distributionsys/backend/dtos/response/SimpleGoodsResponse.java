package com.distributionsys.backend.dtos.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SimpleGoodsResponse {
    Long goodsId;
    String goodsName;
    String warehouseName;
    String supplierName;

    public static SimpleGoodsResponse buildFromRepoResponseObjArr(Object[] repoResponse) {
        return SimpleGoodsResponse.builder()
            .goodsId(Long.valueOf(repoResponse[0].toString()))
            .goodsName(repoResponse[1].toString())
            .warehouseName(repoResponse[2].toString())
            .supplierName(repoResponse[3].toString())
            .build();
    }
}
