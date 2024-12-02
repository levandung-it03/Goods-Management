package com.distributionsys.backend.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewGoodsRequest {
    @NotBlank
    String goodsName;

    @NotNull
    Float unitPrice;

    @NotNull
    Long supplierId;
}
