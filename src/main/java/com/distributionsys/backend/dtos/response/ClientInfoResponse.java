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
public class ClientInfoResponse {
    String firstName;
    String lastName;
    Gender gender;
    LocalDate dob;
    String phone;
}
