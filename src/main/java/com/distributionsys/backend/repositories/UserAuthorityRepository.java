package com.distributionsys.backend.repositories;

import com.distributionsys.backend.entities.sql.UserAuthority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAuthorityRepository extends JpaRepository<UserAuthority, Long> {
}
