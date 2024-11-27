package com.distributionsys.backend.services;

import org.springframework.stereotype.Service;

import com.distributionsys.backend.repositories.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminService {
    UserRepository userRepository;

    public long getTotalClient() {
        return this.userRepository.count();
    }

    public long getTotalActiveClient() {
        return this.userRepository.countActiveUser();
    }

    public long getTotalInactiveClient() {
        return this.userRepository.countInactiveUser();
    }
}
