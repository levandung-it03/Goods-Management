package com.distributionsys.backend.entities.redis;

import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.RedisHash;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "ChangePasswordOtp")
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangePasswordOtp {
    @Id
    String id;
    String otpCode;
}
