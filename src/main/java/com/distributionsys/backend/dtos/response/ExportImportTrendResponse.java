package com.distributionsys.backend.dtos.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExportImportTrendResponse {
    private String date;
    private Long quantityExported;
    private Long quantityImported;
}