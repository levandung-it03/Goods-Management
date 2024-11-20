package com.distributionsys.backend.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewWarehouseRequest {

    @NotBlank
    String warehouseName;

    @NotBlank
    String address;
}
