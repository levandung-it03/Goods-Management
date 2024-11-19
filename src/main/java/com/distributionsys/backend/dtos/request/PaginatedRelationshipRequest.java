package com.distributionsys.backend.dtos.request;

import com.distributionsys.backend.annotations.constraint.SortedModeConstraint;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaginatedRelationshipRequest {

    @NotNull
    Long id;

    @NotNull
    @Min(1)
    Integer page;

    HashMap<String, Object> filterFields;

    String sortedField;

    @SortedModeConstraint
    Integer sortedMode;
}
