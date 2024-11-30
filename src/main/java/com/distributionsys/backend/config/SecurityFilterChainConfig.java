package com.distributionsys.backend.config;

import com.distributionsys.backend.services.redis.InvalidTokenService;
import com.distributionsys.backend.services.redis.RefreshTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.distributionsys.backend.dtos.response.ApiResponseObject;
import com.distributionsys.backend.enums.ErrorCodes;
import com.distributionsys.backend.enums.TokenTypes;
import com.distributionsys.backend.exceptions.ExpiredTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityFilterChainConfig {
    private final ObjectMapper objectMapper;
    private final SecretKeySpec mySecretKeySpec;
    private final PasswordEncoder userPasswordEncoder;
    private final UserDetailsService userDetailsService;

    private final InvalidTokenService invalidTokenService;
    private final RefreshTokenService refreshTokenService;

    @Bean
    public org.springframework.security.web.SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .cors(configurer -> configurer.configurationSource(configurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(request -> request
                .requestMatchers("/api/private/admin/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/api/private/user/**").hasAuthority("ROLE_USER")
                .requestMatchers("/api/private/auth/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/test/**").permitAll()
                .anyRequest().denyAll()
            ).exceptionHandling(exception -> exception.accessDeniedHandler(accessDeniedHandler()))  //--Role's denied.
            .oauth2ResourceServer(resourceServerConfigurer -> resourceServerConfigurer
                .jwt(jwtConfigurer -> jwtConfigurer
                    .decoder(jwtDecoderForOauth2ResourceServer())
                    .jwtAuthenticationConverter(customJwtAuthenticationConverter())
                )
                /*--Config exception-handler from oauth2 service .
                (1) Refresh accessToken.
                (2) Send Customized HttpResponse with invalid tokens.*/
                .authenticationEntryPoint(authenticationEntryPoint())
            )
            .authenticationProvider(authenticationProvider())
        ;
        return httpSecurity.build();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (httpServletRequest, httpServletResponse, accessDeniedException) -> {
            int httpStatus = ErrorCodes.FORBIDDEN_USER.getHttpStatus().value();
            var responseObject = ApiResponseObject.buildByErrorCodes(ErrorCodes.FORBIDDEN_USER);
            var jsonResponse = objectMapper.writeValueAsString(responseObject);
            httpServletResponse.getWriter().write(jsonResponse);
            httpServletResponse.setStatus(httpStatus);
            httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
            httpServletResponse.getWriter().flush();
        };
    }

    @Bean
    public JwtAuthenticationConverter customJwtAuthenticationConverter() {
        var converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt ->
            Arrays.stream(jwt.getClaimAsString("scope").split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList())
        );
        return converter;
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (httpServletRequest, httpServletResponse, authException) -> {
            int httpStatus = ErrorCodes.INVALID_TOKEN.getHttpStatus().value();
            var responseObject = ApiResponseObject.buildByErrorCodes(ErrorCodes.INVALID_TOKEN);

            if (authException instanceof ExpiredTokenException) {
                httpStatus = ErrorCodes.EXPIRED_TOKEN.getHttpStatus().value();
                responseObject = ApiResponseObject.buildByErrorCodes(ErrorCodes.EXPIRED_TOKEN);
            }

            var jsonResponse = objectMapper.writeValueAsString(responseObject);
            httpServletResponse.getWriter().write(jsonResponse);
            httpServletResponse.setStatus(httpStatus);
            httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
            httpServletResponse.getWriter().flush();
        };
    }

    @Bean
    public JwtDecoder jwtDecoderForOauth2ResourceServer() {
        return (token) -> {
            try {
                //--Decode token with Spring Security NimbusJwtDecoder.
                var parsedJwt = NimbusJwtDecoder.withSecretKey(mySecretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS512).build().decode(token);
                //--Check if there's a Refresh Token in HTTP Headers and verify.
                if (parsedJwt.getClaim("type").equals(TokenTypes.REFRESH_TOKEN.name())) {
                    if (!refreshTokenService.checkExistRefreshTokenByJwtId(parsedJwt.getId()))
                        throw new JwtException("Refresh Token is invalid because of ended up user login session");
                } else { //--Check if there's a valid Access Token but in blacklist.
                    if (invalidTokenService.existByJwtId(parsedJwt.getId()))
                        throw new JwtException("Access Token is invalid because of ended up user login session");
                }
                //--Return jwt (default requirement) to verify and access services or get into AuthenticationEntryPoint.
                return parsedJwt;
            } catch (JwtException e) {
                if (e.getMessage().toUpperCase().contains("JWT EXPIRED"))
                    throw new ExpiredTokenException();
                throw new JwtException("Token is invalid");
            }
        };
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        var authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(userPasswordEncoder);
        return authProvider;
    }

    /**
     * CORS configuration instead of WebMvcConfigurer.corsMapping()
     * @return [CorsConfigurationSource]
     */
    @Bean
    public CorsConfigurationSource configurationSource() {
        var config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedOrigins(List.of("*"));
        config.setAllowedMethods(List.of("*"));
        config.setAllowedHeaders(List.of("*"));
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}
