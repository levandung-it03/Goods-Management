package com.distributionsys.backend.dtos.response;

import com.distributionsys.backend.entities.sql.relationships.WarehouseGoods;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ImportBillDetailsResponse {
    private Long id;
    private WarehouseGoods warehouseGoods;
    private Long goodsQuantity;
}
