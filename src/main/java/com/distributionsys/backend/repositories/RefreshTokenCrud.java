package com.distributionsys.backend.repositories;

import com.distributionsys.backend.entities.redis.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenCrud extends CrudRepository<RefreshToken, String> {
}
