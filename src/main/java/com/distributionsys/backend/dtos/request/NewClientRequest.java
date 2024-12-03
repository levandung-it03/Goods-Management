package com.distributionsys.backend.dtos.request;

import java.time.LocalDate;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewClientRequest {
    String email;
    String password;
    String firstName;
    String lastName;
    String gender;
    String phone;
    LocalDate dob;
}
