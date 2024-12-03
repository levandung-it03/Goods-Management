package com.distributionsys.backend.entities.sql;

import com.distributionsys.backend.enums.Gender;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "client_info")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClientInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_info_id")
    Long clientInfoId;

    @OneToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "user_id")
    @JsonIgnore
    User user;

    @Column(name = "first_name", nullable = false)
    String firstName;

    @Column(name = "last_name", nullable = false)
    String lastName;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "gender_enum", nullable = false)
    Gender gender;

    @Column(name = "dob", nullable = false, columnDefinition = "DATE")
    LocalDate dob;

    @Column(name = "phone", nullable = false)
    String phone;
}
