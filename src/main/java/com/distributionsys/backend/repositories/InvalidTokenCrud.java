package com.distributionsys.backend.repositories;

import com.distributionsys.backend.entities.redis.InvalidToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvalidTokenCrud extends CrudRepository<InvalidToken, String> {
}
