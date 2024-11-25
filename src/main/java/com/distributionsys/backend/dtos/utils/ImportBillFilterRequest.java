package com.distributionsys.backend.dtos.utils;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ImportBillFilterRequest {
    LocalDateTime fromCreatedTime;
    LocalDateTime toCreatedTime;

    public static ImportBillFilterRequest buildFromFilterHashMap(HashMap<String, Object> map)
        throws NullPointerException, IllegalArgumentException, NoSuchFieldException {
        for (String key: map.keySet())
            if (Arrays.stream(ImportBillFilterRequest.class.getDeclaredFields())
                .noneMatch(f -> f.getName().equals(key)))
                throw new NoSuchFieldException();

        var result = new ImportBillFilterRequest();
        result.setFromCreatedTime(!map.containsKey("fromCreatedTime") ? null
            : LocalDateTime.parse(map.get("fromCreatedTime").toString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        result.setToCreatedTime(!map.containsKey("toCreatedTime") ? null
            : LocalDateTime.parse(map.get("toCreatedTime").toString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return result;
    }
}
