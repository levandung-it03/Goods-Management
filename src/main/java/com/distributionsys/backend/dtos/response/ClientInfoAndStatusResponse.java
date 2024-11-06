package com.distributionsys.backend.dtos.response;

import com.distributionsys.backend.enums.Gender;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClientInfoAndStatusResponse {
    Long userInfoId;
    String firstName;
    String lastName;
    Gender gender;
    String email;
    LocalDate dob;
    Long userId;
    boolean isActive;
    LocalDateTime createdTime;
}
