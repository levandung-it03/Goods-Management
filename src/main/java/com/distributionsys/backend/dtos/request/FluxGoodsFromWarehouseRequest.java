package com.distributionsys.backend.dtos.request;

import com.distributionsys.backend.annotations.constraint.ListTypeConstraint;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FluxGoodsFromWarehouseRequest {

    @NotNull
    @NotEmpty
    @ListTypeConstraint(listType = Long.class)
    List<Long> goodsFromWarehouseIds;
}
