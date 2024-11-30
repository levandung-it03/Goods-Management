package com.distributionsys.backend.entities.redis;

import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.RedisHash;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash("AsyncExecutingBillsFlag")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AsyncExecutingBillsFlag {
    @Id
    String userEmail;
    Boolean isExecuting;
}
