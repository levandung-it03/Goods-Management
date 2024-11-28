package com.distributionsys.backend.dtos.request;

import com.distributionsys.backend.annotations.constraint.ListTypeConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewExportBillRequest {

    @NotNull
    @NotEmpty
    @ListTypeConstraint(listType = NewExportBillRequest.ExportedWarehouseGoodsDto.class)
    List<NewExportBillRequest.ExportedWarehouseGoodsDto> exportedWarehouseGoods;

    @NotBlank
    @Length(max = 200)
    String receiverName;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ExportedWarehouseGoodsDto {

        @NotNull
        Long warehouseGoodsId;

        @NotNull
        Long exportedGoodsQuantity;
    }
}
