package com.distributionsys.backend.config;

import com.distributionsys.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    @Value("${services.security.secret-key}")
    private String SECRET_KEY;

    private final UserRepository userRepository;

    @Bean
    public SecretKeySpec mySecretKeySpec() {
        return new SecretKeySpec(SECRET_KEY.getBytes(), "HS512");
    }

    @Bean
    public PasswordEncoder userPasswordEncoder() {
        return new BCryptPasswordEncoder(8, new SecureRandom());
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return (emailAsUsername) -> userRepository
            .findByEmail(emailAsUsername)
            .orElseThrow(() -> new UsernameNotFoundException("Invalid email"));
    }
}
