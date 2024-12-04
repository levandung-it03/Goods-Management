package com.distributionsys.backend.services;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.distributionsys.backend.dtos.request.NewClientRequest;
import com.distributionsys.backend.dtos.request.PaginatedTableRequest;
import com.distributionsys.backend.dtos.response.ClientResponse;
import com.distributionsys.backend.dtos.response.TablePagesResponse;
import com.distributionsys.backend.dtos.utils.UserFilterRequest;
import com.distributionsys.backend.entities.sql.ClientInfo;
import com.distributionsys.backend.entities.sql.User;
import com.distributionsys.backend.entities.sql.UserAuthority;
import com.distributionsys.backend.enums.ErrorCodes;
import com.distributionsys.backend.enums.Gender;
import com.distributionsys.backend.enums.PageEnum;
import com.distributionsys.backend.exceptions.ApplicationException;
import com.distributionsys.backend.mappers.PageMappers;
import com.distributionsys.backend.repositories.AuthorityRepository;
import com.distributionsys.backend.repositories.ClientInfoRepository;
import com.distributionsys.backend.repositories.UserAuthorityRepository;
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
    AuthorityRepository authorityRepository;
    UserAuthorityRepository userAuthorityRepository;
    PageMappers pageMappers;
    JwtService jwtService;
    private final PasswordEncoder userPasswordEncoder;
    private final int pageSize = PageEnum.SIZE.getSize();

    private final HashMap<Integer, Direction> SORTING_DIRECTION = new HashMap<>(Map.ofEntries(
        Map.entry(1, Direction.ASC),
        Map.entry(-1, Direction.DESC)
    ));

    public long getTotalClient() {
        return this.userRepository.count();
    }

    public long getTotalActiveClient() {
        return this.userRepository.countActiveUser();
    }

    public long getTotalInactiveClient() {
        return this.userRepository.countInactiveUser();
    }

    public ClientResponse getAdminInformation(String accessToken) {
        String email = jwtService.readPayload(accessToken).get("sub");
        if (email.isBlank() || email.isEmpty()) {
            throw new ApplicationException(ErrorCodes.INVALID_TOKEN);
        }

        var user = this.userRepository.findByEmail(email);
        if (!user.isPresent()) {
            throw new ApplicationException(ErrorCodes.USER_NOT_FOUND);
        }

        var clientInfo = this.clientInfoRepository.findByUserEmail(email);
        if (!clientInfo.isPresent()) {
            throw new ApplicationException(ErrorCodes.USER_NOT_FOUND);
        }

        return ClientResponse.builder()
            .authorities(user.get().getAuthorities())
            .email(email)
            .firstName(clientInfo.get().getFirstName())
            .lastName(clientInfo.get().getLastName())
            .gender(clientInfo.get().getGender())
            .dob(clientInfo.get().getDob())
            .phone(clientInfo.get().getPhone())
            .build();
    }

    public TablePagesResponse<ClientResponse> getAllUsers(PaginatedTableRequest request) {
        var userSortingFields = List.of("email", "userId");
        var isMatchUserSortingFields = userSortingFields
            .stream()
            .anyMatch(sortingField -> Objects.isNull(request.getSortedField()) ? "userId".equals(sortingField): request.getSortedField().equals(sortingField));
        StringBuilder sortingFieldBuilder = new StringBuilder();
        sortingFieldBuilder
            .append("ci.")
            .append(request.getSortedField());
        Pageable pageableCf = isMatchUserSortingFields 
            ? pageMappers.tablePageRequestToPageable(request).toPageable(User.class)
            : PageRequest.of(request.getPage() - 1, pageSize, SORTING_DIRECTION.get(request.getSortedMode()), sortingFieldBuilder.toString());
        if (Objects.isNull(request.getFilterFields()) || request.getFilterFields().isEmpty()) {
            var repoRes = userRepository.findAllNoFilters(pageableCf);
            return TablePagesResponse.<ClientResponse>builder()
                .data(repoRes.stream().map(ClientResponse::buildFromRepoResponseObjArr).toList())
                .totalPages(repoRes.getTotalPages())
                .currentPage(request.getPage())
                .build();
        }
        try {
            var userFilters = UserFilterRequest.builderFromFilterHashMap(request.getFilterFields());
            var repoRes = userRepository.findAllByUserFilter(userFilters, pageableCf);
            return TablePagesResponse.<ClientResponse>builder()
                .data(repoRes.stream().map(ClientResponse::buildFromRepoResponseObjArr).toList())
                .totalPages(repoRes.getTotalPages())
                .currentPage(request.getPage())
                .build();
        } catch (NoSuchFieldException | IllegalArgumentException | NullPointerException e) {
            throw new ApplicationException(ErrorCodes.INVALID_FILTERING_FIELD_OR_VALUE);
        }
    }

    public ClientResponse createClient(NewClientRequest request) {
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
        var createdUser = userRepository.save(newUser);
        if (Objects.isNull(createdUser)) {
            throw new ApplicationException(ErrorCodes.CANNOT_CREATE_CLIENT);
        }

        var userRole = authorityRepository.findByAuthorityName("ROLE_USER");
        if (!userRole.isPresent()) {
            throw new ApplicationException(ErrorCodes.AUTHORITY_NOT_FOUND);
        }
        var newUserAuthorities = new UserAuthority();
        newUserAuthorities.setAuthority(userRole.get());
        newUserAuthorities.setUser(createdUser);
        var createdUserAuthorities = userAuthorityRepository.save(newUserAuthorities);
        if (Objects.isNull(createdUserAuthorities)) {
            throw new ApplicationException(ErrorCodes.CANNOT_CREATE_CLIENT);
        }

        var newClient = new ClientInfo();
        newClient.setFirstName(request.getFirstName());
        newClient.setLastName(request.getLastName());
        newClient.setGender(Gender.valueOf(request.getGender().toUpperCase()));
        newClient.setPhone(request.getPhone());
        newClient.setUser(createdUser);
        newClient.setDob(request.getDob());
        var createdClient = clientInfoRepository.save(newClient);
        if (Objects.isNull(createdClient)) {
            throw new ApplicationException(ErrorCodes.CANNOT_CREATE_CLIENT);
        }

        return ClientResponse.buildFromEntities(createdUser, createdClient);
    }

    public User updateClientStatus(long userId, String status) {
        var validStatusValues = List.of("active", "inactive");
        boolean isInvalidStatusParam = validStatusValues.stream().noneMatch(validStatus -> validStatus.equalsIgnoreCase(status));
        if (isInvalidStatusParam) {
            throw new ApplicationException(ErrorCodes.INVALID_STATUS_VALUE);
        }

        var targetUser = this.userRepository.findById(userId);
        if (targetUser.isEmpty()) {
            throw new ApplicationException(ErrorCodes.USER_NOT_FOUND);
        }

        this.userRepository.updateStatusByUserId(userId, status.equalsIgnoreCase("active") ? true : false);
        var updatedUser = this.userRepository.findById(userId);
        return updatedUser.get();
    }
}
