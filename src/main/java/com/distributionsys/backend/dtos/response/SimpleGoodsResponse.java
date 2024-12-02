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
    String supplierName;

    public static SimpleGoodsResponse buildFromRepoResponseObjArr(Object[] objects) {
        return SimpleGoodsResponse.builder()
            .goodsId(Long.valueOf(objects[0].toString()))
            .goodsName(objects[1].toString())
            .supplierName(objects[2].toString())
            .build();
    }
}
