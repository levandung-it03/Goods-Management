package com.distributionsys.backend.services;

import org.springframework.stereotype.Service;

import com.distributionsys.backend.entities.sql.User;
import com.distributionsys.backend.enums.ErrorCodes;
import com.distributionsys.backend.exceptions.ApplicationException;
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

    public User deactivateClient(long userId) {
        var targetUser = this.userRepository.findById(userId);
        if (targetUser.isEmpty()) {
            throw new ApplicationException(ErrorCodes.USER_NOT_FOUND);
        }
        var userAuthorities = targetUser.get().getAuthorities();
        for (var authority: userAuthorities) {
            if (authority.getAuthorityName().equals("ROLE_ADMIN")) {
                throw new ApplicationException(ErrorCodes.FORBIDDEN_USER);
            }
        }
        this.userRepository.updateStatusByUserId(userId, false);
        var udpatedUser = targetUser.get();
        udpatedUser.setActive(false);
        return udpatedUser;
    }
}
