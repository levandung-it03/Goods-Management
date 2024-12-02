package com.distributionsys.backend.dtos.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SimpleWarehouseResponse {
    Long warehouseId;
    String warehouseName;

    public static SimpleWarehouseResponse buildFromRepoResponseObjArr(Object[] objects) {
        return SimpleWarehouseResponse.builder()
            .warehouseId(Long.valueOf(objects[0].toString()))
            .warehouseName(objects[1].toString())
            .build();
    }
}
