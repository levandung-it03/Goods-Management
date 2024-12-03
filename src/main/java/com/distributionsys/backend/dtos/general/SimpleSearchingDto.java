package com.distributionsys.backend.dtos.general;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SimpleSearchingDto {
    String name;

    @NotNull
    @Min(1)
    Integer page;
}
