package com.distributionsys.backend.dtos.request;

import com.distributionsys.backend.exceptions.ApplicationException;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;
import java.util.HashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SupplierRequest {
    String supplierName;

    public static SupplierRequest buildFromFilterHashMap(HashMap<String, Object> map)
        throws NullPointerException, ApplicationException, IllegalArgumentException, NoSuchFieldException {
        for (String key: map.keySet())
            if (Arrays
                .stream(SupplierRequest.class.getDeclaredFields())
                .noneMatch(f -> f.getName().equals(key))
            )   throw new NoSuchFieldException();

        var result = new SupplierRequest();
        result.setSupplierName(!map.containsKey("supplierName") ? null : map.get("supplierName").toString());
        return result;
    }
}
