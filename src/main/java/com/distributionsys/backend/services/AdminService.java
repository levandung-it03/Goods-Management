package com.distributionsys.backend.services;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.distributionsys.backend.dtos.request.NewClientRequest;
import com.distributionsys.backend.dtos.request.PaginatedTableRequest;
import com.distributionsys.backend.dtos.response.ClientInfoResponse;
import com.distributionsys.backend.dtos.response.TablePagesResponse;
import com.distributionsys.backend.dtos.utils.UserFilterRequest;
import com.distributionsys.backend.entities.sql.User;
import com.distributionsys.backend.enums.ErrorCodes;
import com.distributionsys.backend.exceptions.ApplicationException;
import com.distributionsys.backend.mappers.PageMappers;
import com.distributionsys.backend.repositories.ClientInfoRepository;
import com.distributionsys.backend.repositories.UserRepository;
import com.distributionsys.backend.services.auth.JwtService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminService {
    ClientInfoRepository clientInfoRepository;
    UserRepository userRepository;
    PageMappers pageMappers;
    JwtService jwtService;
    private final PasswordEncoder userPasswordEncoder;

    public long getTotalClient() {
        return this.userRepository.count();
    }

    public long getTotalActiveClient() {
        return this.userRepository.countActiveUser();
    }

    public long getTotalInactiveClient() {
        return this.userRepository.countInactiveUser();
    }

    public ClientInfoResponse getAdminInformation(String accessToken) {
        String email = jwtService.readPayload(accessToken).get("sub");
        if (email.isBlank() || email.isEmpty()) {
            throw new ApplicationException(ErrorCodes.INVALID_TOKEN);
        }

        return clientInfoRepository.findByUserEmail(email)
            .map(client -> ClientInfoResponse.builder()
                .firstName(client.getFirstName())
                .lastName(client.getLastName())
                .gender(client.getGender())
                .dob(client.getDob())
                .phone(client.getPhone())
                .build())
            .orElseThrow(() -> new ApplicationException(ErrorCodes.INVALID_TOKEN));
    }

    public TablePagesResponse<User> getAllUsers(PaginatedTableRequest request) {
        Pageable pageableCf = pageMappers.tablePageRequestToPageable(request).toPageable(User.class);
        if (Objects.isNull(request.getFilterFields()) || request.getFilterFields().isEmpty()) {
            Page<User> repoRes = userRepository.findAll(pageableCf);
            return TablePagesResponse.<User>builder()
                .data(repoRes.stream().toList())
                .totalPages(repoRes.getTotalPages())
                .currentPage(request.getPage())
                .build();
        }
        try {
            var userFilters = UserFilterRequest.builderFromFilterHashMap(request.getFilterFields());
            Page<User> repoRes = userRepository.findAllByUserFilter(userFilters, pageableCf);
            return TablePagesResponse.<User>builder()
                .data(repoRes.stream().toList())
                .totalPages(repoRes.getTotalPages())
                .currentPage(request.getPage())
                .build();
        } catch (NoSuchFieldException | IllegalArgumentException | NullPointerException e) {
            throw new ApplicationException(ErrorCodes.INVALID_FILTERING_FIELD_OR_VALUE);
        }
    }

    public User createClient(NewClientRequest request) {
        userRepository
            .findByEmail(request.getEmail())
            .ifPresent(user -> {
                throw new ApplicationException(ErrorCodes.USER_EXISTING);
            });
        var newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.setPassword(userPasswordEncoder.encode(request.getPassword()));
        newUser.setCreatedTime(LocalDateTime.now());
        newUser.setActive(true);
        return userRepository.save(newUser);
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
