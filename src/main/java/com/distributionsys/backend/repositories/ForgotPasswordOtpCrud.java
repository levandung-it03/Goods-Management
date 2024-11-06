package com.distributionsys.backend.repositories;

import com.distributionsys.backend.entities.redis.ForgotPasswordOtp;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForgotPasswordOtpCrud extends CrudRepository<ForgotPasswordOtp, String> {
}
