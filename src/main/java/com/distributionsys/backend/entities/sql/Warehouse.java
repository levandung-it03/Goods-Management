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
@Table(name = "warehouse")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Warehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "warehouse_id")
    Long warehouseId;

    @Column(name = "warehouse_name", nullable = false, unique = true, length = 200)
    String warehouseName;

    @Column(name = "address", nullable = false, length = 200)
    String address;

    public static Warehouse buildFormFilterHashMap(HashMap<String, Object> map)
        throws NullPointerException, ApplicationException, IllegalArgumentException, NoSuchFieldException {
        for (String key : map.keySet())
            if (Arrays.stream(Warehouse.class.getDeclaredFields())
                .noneMatch(f -> f.getName().equals(key)))
                throw new NoSuchFieldException();

        var result = new Warehouse();
        result.setWarehouseName(!map.containsKey("warehouseName") ? null : map.get("warehouseName").toString());
        result.setAddress(!map.containsKey("address") ? null : map.get("address").toString());
        return result;
    }
}
