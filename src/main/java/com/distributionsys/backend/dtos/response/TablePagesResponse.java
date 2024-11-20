package com.distributionsys.backend.dtos.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TablePagesResponse<T> {
    List<T> data;
    Integer currentPage;
    Integer totalPages;
}
