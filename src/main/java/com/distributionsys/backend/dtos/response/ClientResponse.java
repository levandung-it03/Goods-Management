package com.distributionsys.backend.dtos.response;

import com.distributionsys.backend.entities.sql.Authority;
import com.distributionsys.backend.entities.sql.ClientInfo;
import com.distributionsys.backend.entities.sql.User;
import com.distributionsys.backend.enums.Gender;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClientResponse {
    Long userId;
    String email;
    Boolean active;
    LocalDateTime createdTime;
    String firstName;
    String lastName;
    Gender gender;
    LocalDate dob;
    String phone;
    Collection<Authority> authorities;

    public static ClientResponse buildFromRepoResponseObjArr(Object[] arrObj) {
        return ClientResponse.builder()
            .userId(Long.parseLong(arrObj[0].toString()))
            .email(arrObj[1].toString())
            .active(Boolean.valueOf(arrObj[2].toString()))
            .createdTime(LocalDateTime.parse(arrObj[3].toString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .firstName(Objects.isNull(arrObj[4]) ? null : arrObj[4].toString())
            .lastName(Objects.isNull(arrObj[5]) ? null : arrObj[5].toString())
            .gender(Objects.isNull(arrObj[6]) ? null : Gender.valueOf(arrObj[6].toString()))
            .phone(Objects.isNull(arrObj[7]) ? null : arrObj[7].toString())
            .dob(Objects.isNull(arrObj[8]) ? null : LocalDate.parse(arrObj[8].toString()))
            .build();
    }

    public static ClientResponse buildFromEntities(User user, ClientInfo clientInfo) {
        return ClientResponse.builder()
            .userId(user.getUserId())
            .email(user.getEmail())
            .active(user.isActive())
            .createdTime(user.getCreatedTime())
            .firstName(clientInfo.getFirstName())
            .lastName(clientInfo.getLastName())
            .gender(clientInfo.getGender())
            .phone(clientInfo.getPhone())
            .dob(clientInfo.getDob())
            .build();
    }
}
