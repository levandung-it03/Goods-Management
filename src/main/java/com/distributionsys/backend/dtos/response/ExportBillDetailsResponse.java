package com.distributionsys.backend.dtos.response;

import com.distributionsys.backend.entities.sql.relationships.WarehouseGoods;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExportBillDetailsResponse {

    private Long id;
    private WarehouseGoods warehouseGoods;
    private Long goodsQuantity;
}