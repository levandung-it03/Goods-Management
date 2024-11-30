package com.distributionsys.backend.dtos.request;

import com.distributionsys.backend.enums.Gender;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClientInfoRequest {
    String firstName;
    String lastName;
    Gender gender;
    LocalDate dob;
    String phone;
}
