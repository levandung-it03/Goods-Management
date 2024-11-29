package com.distributionsys.backend.dtos.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatisticsResponse {
    Long totalImportBills;
    Long totalExportBills;
    Long totalGoods;
    Long totalSuppliers;
}

