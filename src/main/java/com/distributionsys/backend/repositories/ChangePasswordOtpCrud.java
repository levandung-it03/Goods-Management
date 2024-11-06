package com.distributionsys.backend.repositories;

import com.distributionsys.backend.entities.redis.ChangePasswordOtp;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChangePasswordOtpCrud extends CrudRepository<ChangePasswordOtp, String> {
}
