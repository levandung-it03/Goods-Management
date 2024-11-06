package com.distributionsys.backend.repositories;

import com.distributionsys.backend.entities.sql.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.active = :newStatus WHERE u.userId = :id")
    void updateStatusByUserId(@Param("id") Long userId, @Param("newStatus") Boolean newStatus);

    boolean existsByEmail(String email);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.password = :#{#userInput.password} WHERE u.userId = :#{#userInput.userId}")
    void updateUserPassword(@Param("userInput") User user);
}
