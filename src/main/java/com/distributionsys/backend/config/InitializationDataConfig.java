package com.distributionsys.backend.config;

import com.distributionsys.backend.entities.sql.Authority;
import com.distributionsys.backend.entities.sql.ClientInfo;
import com.distributionsys.backend.entities.sql.User;
import com.distributionsys.backend.enums.Gender;
import com.distributionsys.backend.repositories.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InitializationDataConfig implements CommandLineRunner {
    AuthorityRepository authorityRepository;
    UserRepository userRepository;
    PasswordEncoder userPasswordEncoder;
    InvalidTokenCrud invalidTokenCrud;
    RefreshTokenCrud refreshTokenCrud;
    ClientInfoRepository clientInfoRepository;

    @Override
    public void run(String... args) {
        if (authorityRepository.count() == 0)
            authorityRepository.saveAll(List.of(
                Authority.builder().authorityName("ROLE_ADMIN").build(),
                Authority.builder().authorityName("ROLE_USER").build()
            ));
        if (userRepository.count() == 0) {
            List<Authority> authorities = authorityRepository.findAll();
            List<User> users = userRepository.saveAll(List.of(
                User.builder()
                    .email("root@gmail.com")
                    .password(userPasswordEncoder.encode("rootroot"))
                    .authorities(List.of(authorities.getFirst()))
                    .createdTime(LocalDateTime.now())
                    .active(true)
                    .build(),
                User.builder()
                    .email("user@gmail.com")
                    .password(userPasswordEncoder.encode("useruser"))
                    .authorities(List.of(authorities.getLast()))
                    .createdTime(LocalDateTime.now())
                    .active(true)
                    .build()
            ));
            clientInfoRepository.save(ClientInfo.builder()
                .firstName("Dung").lastName("Le Van").dob(LocalDate.of(2003, 12, 11))
                .gender(Gender.MALE).phone("0112333444").user(users.getLast()).build());
        }
        invalidTokenCrud.deleteAll();
        refreshTokenCrud.deleteAll();
    }
}
