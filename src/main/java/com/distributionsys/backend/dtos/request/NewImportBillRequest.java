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
public class NewImportBillRequest {

    @NotNull
    @NotEmpty
    @ListTypeConstraint(listType = ImportedWarehouseGoodsDto.class)
    List<ImportedWarehouseGoodsDto> importedWarehouseGoods;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ImportedWarehouseGoodsDto {

        @NotNull
        Long goodsId;

        @NotNull
        Long warehouseId;

        @NotNull
        Long importedGoodsQuantity;
    }
}
