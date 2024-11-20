package com.distributionsys.backend.entities.sql;

import com.distributionsys.backend.exceptions.ApplicationException;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;
import java.util.HashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "supplier")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "supplier_id")
    Long supplierId;

    @Column(name = "supplier_name", nullable = false, unique = true, length = 200)
    String supplierName;

    public static Supplier buildFromFilterHashMap(HashMap<String, Object> map)
        throws NullPointerException, ApplicationException, IllegalArgumentException, NoSuchFieldException {
        for (String key: map.keySet())
            if (Arrays.stream(Supplier.class.getDeclaredFields())
                .noneMatch(f -> f.getName().equals(key)))
                throw new NoSuchFieldException();

        var result = new Supplier();
        result.setSupplierName(!map.containsKey("supplierName") ? null : map.get("supplierName").toString());
        return result;
    }
}
