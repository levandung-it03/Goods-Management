package com.distributionsys.backend.dtos.utils;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;
import java.util.HashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserFilterRequest {
    Long userId;
    String email;
    Boolean status;

    public static UserFilterRequest builderFromFilterHashMap(HashMap<String, Object> map) 
        throws NullPointerException, NoSuchFieldException, IllegalArgumentException {
 
        System.out.println("Raw filter opbect: " + map);

        for (String key: map.keySet()) {
            System.out.println("key: " + key);
            if (Arrays
                .stream(UserFilterRequest.class.getDeclaredFields())
                .noneMatch(f -> f.getName().equals(key))) {
                    throw new NoSuchFieldException();
                }
            else {
                System.out.println("bug here");
            }
        }

        var result = new UserFilterRequest();
        result.setUserId(map.containsKey("userId")
            ? Long.valueOf(map.get("userId").toString())
            : null
        );
        result.setEmail(map.containsKey("email") 
            ? map.get("email").toString() 
            : null);
        result.setStatus(map.containsKey("status") 
            ? Boolean.valueOf(map
                .get("status")
                .toString()
                .equalsIgnoreCase("active")
                    ? true
                    : map
                        .get("status")
                        .toString()
                        .equalsIgnoreCase("inactive")
                            ? false 
                            : null)
            : null);

        return result;
    }
}
