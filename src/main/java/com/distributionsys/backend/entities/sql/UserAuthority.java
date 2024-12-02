package com.distributionsys.backend.entities.sql;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_authorities")
@Entity
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "user_id")
    @JsonIgnore
    User user;

    @ManyToOne(targetEntity = Authority.class)
    @JoinColumn(name = "authority_id", nullable = false, referencedColumnName = "authority_id")
    @JsonIgnore
    Authority authority;
}
