package com.distributionsys.backend.dtos.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewGoodsRequest {
    String goodsName;
    Float unitPrice;
    Long supplierId;
}
