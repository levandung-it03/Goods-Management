package com.distributionsys.backend.services;

import com.distributionsys.backend.dtos.request.AuthenticationRequest;
import com.distributionsys.backend.dtos.request.ChangePasswordRequest;
import com.distributionsys.backend.dtos.request.VerifyAuthOtpRequest;
import com.distributionsys.backend.entities.redis.ChangePasswordOtp;
import com.distributionsys.backend.entities.sql.User;
import com.distributionsys.backend.enums.ErrorCodes;
import com.distributionsys.backend.exceptions.ApplicationException;
import com.distributionsys.backend.repositories.UserRepository;
import com.distributionsys.backend.services.auth.JwtService;
import com.distributionsys.backend.services.redis.ChangePasswordOtpService;
import com.distributionsys.backend.services.third_party.EmailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.distributionsys.backend.services.auth.AuthenticationService.generateRandomOtp;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final ChangePasswordOtpService changePasswordOtpService;
    private final PasswordEncoder userPasswordEncoder;
    private final JwtService jwtService;

    @Value("${services.security.max-hidden-otp-age-min}")
    private int maxHiddenOtpAgeMin;
    @Value("${services.security.max-otp-age-min}")
    private int maxOtpAgeMin;

    public HashMap<String, Object> getOtpToChangePassword(AuthenticationRequest request) {
        var authToken = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        var authUser = (User) authenticationManager.authenticate(authToken).getPrincipal();
        //--Check if this User is in blacklist or not.
        if (!authUser.isActive())
            throw new ApplicationException(ErrorCodes.FORBIDDEN_USER);

        String otp = generateRandomOtp(4);
        //--Remove the previous OTP code in session if it's existing.
        if (changePasswordOtpService.findByEmail(request.getEmail()).isPresent())
            changePasswordOtpService.deleteByEmail(request.getEmail());

        String otpMailMessage = String.format("""
            <div>
                <p style="font-size: 18px">Do not share this information to anyone. Please secure these characters!</p>
                <h2>User Email: <b>%s</b></h2>
                <h2>OTP: <b>%s</b></h2>
            </div>
        """, request.getEmail(), otp);
        emailService.sendSimpleEmail(request.getEmail(), "OTP Code for changing password by Home Workout With AI",
            otpMailMessage);

        //--Save into session for the next actions.
        changePasswordOtpService.save(ChangePasswordOtp.builder().id(request.getEmail()).otpCode(otp).build());

        // Schedule a task to remove the OTP after 5 minutes (or your preferred timeout).
        scheduler.schedule(() -> {
            changePasswordOtpService.deleteByEmail(request.getEmail());
            log.info("Change Password OTP for {} has expired and been removed.", request.getEmail());
        }, maxOtpAgeMin, TimeUnit.MINUTES);

        return new HashMap<>(Map.of("ageInSeconds", maxOtpAgeMin*60));
    }

    public ChangePasswordOtp verifyOtpToChangePassword(VerifyAuthOtpRequest request, String accessToken) {
        //--Remove the previous OTP code in session if it's existing.
        String email = jwtService.readPayload(accessToken).get("sub");
        Optional<ChangePasswordOtp> existsOtp = changePasswordOtpService.findByEmail(email);
        if (existsOtp.isPresent() && existsOtp.get().getOtpCode().equals(request.getOtpCode())) {
            var result = ChangePasswordOtp.builder().id(email).otpCode(generateRandomOtp(4)).build();
            changePasswordOtpService.deleteByEmail(email);
            changePasswordOtpService.save(result);

            // Schedule a task to remove the OTP after 15 minutes (similar to accessToken lifetime).
            scheduler.schedule(() -> {
                changePasswordOtpService.deleteByEmail(email);
                log.info("Hidden Change Password OTP for {} has expired and been removed.", email);
            }, maxHiddenOtpAgeMin, TimeUnit.MINUTES);

            return result;
        } else throw new ApplicationException(ErrorCodes.OTP_NOT_FOUND);
    }

    @Transactional(rollbackOn = {RuntimeException.class})
    public void changePassword(ChangePasswordRequest request, String accessToken) {
        var user = userRepository.findByEmail(jwtService.readPayload(accessToken).get("sub"))
            .orElseThrow(() -> new ApplicationException(ErrorCodes.FORBIDDEN_USER));
        if (!user.isActive())   throw new ApplicationException(ErrorCodes.FORBIDDEN_USER);

        var existsOtp = changePasswordOtpService.findByEmail(user.getEmail())
            .orElseThrow(() -> new ApplicationException(ErrorCodes.HIDDEN_OTP_IS_KILLED));
        if (!existsOtp.getOtpCode().equals(request.getOtpCode()))
            throw new ApplicationException(ErrorCodes.HIDDEN_OTP_NOT_FOUND);

        changePasswordOtpService.deleteByEmail(user.getEmail());
        user.setPassword(userPasswordEncoder.encode(request.getPassword()));
        userRepository.updateUserPassword(user);
    }
}
