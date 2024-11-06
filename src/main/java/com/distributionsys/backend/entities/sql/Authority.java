package com.distributionsys.backend.entities.sql;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
    name = "authority",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"authority_name"})}
)
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Authority implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "authority_id", nullable = false)
    Long authorityId;

    @Column(name = "authority_name", nullable = false)
    String authorityName;

    @Override
    public String getAuthority() {
        return this.getAuthorityName();
    }
}
