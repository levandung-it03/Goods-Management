package com.distributionsys.backend.dtos.utils;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;

import com.distributionsys.backend.enums.Gender;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserFilterRequest {
    Long userId;
    String email;
    Boolean status;
    String firstName;
    String lastName;
    Gender gender;
    String phone;
    LocalDate dob;

    public static UserFilterRequest builderFromFilterHashMap(HashMap<String, Object> map) 
        throws NullPointerException, NoSuchFieldException, IllegalArgumentException {
        for (String key: map.keySet()) {
            if (Arrays
                .stream(UserFilterRequest.class.getDeclaredFields())
                .noneMatch(f -> f.getName().equals(key))) {
                    throw new NoSuchFieldException();
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
        result.setFirstName(map.containsKey("firstName") 
            ? map.get("firstName").toString() 
            : null);
        result.setLastName(map.containsKey("lastName") 
            ? map.get("lastName").toString() 
            : null);
        result.setGender(map.containsKey("gender") 
            ? Gender.valueOf(map.get("gender").toString().toUpperCase()) 
            : null);
        result.setPhone(map.containsKey("phone") 
            ? map.get("phone").toString() 
            : null);
        result.setDob(map.containsKey("dob")
            ? LocalDate.parse(map.get("dob").toString())
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
