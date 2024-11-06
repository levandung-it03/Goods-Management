package com.distributionsys.backend.services.auth;

import com.distributionsys.backend.dtos.general.TokenDto;
import com.distributionsys.backend.dtos.request.AuthenticationRequest;
import com.distributionsys.backend.dtos.request.VerifyPublicOtpRequest;
import com.distributionsys.backend.dtos.response.AuthenticationResponse;
import com.distributionsys.backend.entities.redis.ForgotPasswordOtp;
import com.distributionsys.backend.entities.redis.RefreshToken;
import com.distributionsys.backend.entities.sql.User;
import com.distributionsys.backend.enums.ErrorCodes;
import com.distributionsys.backend.exceptions.ApplicationException;
import com.distributionsys.backend.repositories.UserRepository;
import com.distributionsys.backend.services.third_party.EmailService;
import com.distributionsys.backend.services.redis.ForgotPasswordOtpService;
import com.distributionsys.backend.services.redis.InvalidTokenService;
import com.distributionsys.backend.services.redis.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationService {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final InvalidTokenService invalidTokenService;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final ForgotPasswordOtpService forgotPasswordOtpService;
    private final PasswordEncoder userPasswordEncoder;

    @Value("${services.security.max-otp-age-min}")
    private int maxOtpAgeMin;

    public static String generateRandomOtp(int length) {
        StringBuilder otp = new StringBuilder(length);
        for (int i = 0; i < length; i++)
            otp.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        return otp.toString();
    }

    /**
     * Steps
     * <br> 1. AuthenticationManager authenticates Credentials (by configured AuthenticationProvider).
     * <br> &nbsp; Failed: throw {@link AuthenticationException}.
     * <br> 2. Check if User is not in-activated.
     * <br> &nbsp; Failed: throw {@link ApplicationException}.
     * <br> 3. Generate a pair of token: AccessToken and RefreshToken to respond to client.
     * <br> 4. Save RefreshToken into Redis (marking that User's login session is starting).
     *
     * @param authObject as Credentials(username, password).
     * @return Response(AccessToken, RefreshToken).
     * @throws AuthenticationException by AuthenticationManager.
     */
    public AuthenticationResponse authenticate(AuthenticationRequest authObject)
        throws AuthenticationException, ApplicationException {
        //--Authenticate User's credentials.
        var authToken = new UsernamePasswordAuthenticationToken(authObject.getEmail(), authObject.getPassword());
        var authUser = (User) authenticationManager.authenticate(authToken).getPrincipal();
        //--Check if this User is in blacklist or not.
        if (!authUser.isActive())
            throw new ApplicationException(ErrorCodes.FORBIDDEN_USER);
        //--Generate Token Pair: Access Token and Refresh Token for response.
        var accessTok = jwtService.generateAccessToken(authUser).get("token");
        var refreshTokMap = jwtService.generateRefreshToken(authUser);
        var refreshTokObj = RefreshToken.builder().id(refreshTokMap.get("id")).build();
        //--Save Refresh Token into Redis for security (logout or immediately denying accessibility).
        refreshTokenService.saveRefreshToken(refreshTokObj);
        return AuthenticationResponse.builder().refreshToken(refreshTokMap.get("token")).accessToken(accessTok).build();
    }

    /**
     * Steps
     * <br> 0. Verify RefreshToken by Oauth2ResourceServer (in JwtDecoder() Bean).
     * <br> 1. Check if AccessToken is expired or not.
     * <br> &nbsp;Failed: throw {@link ApplicationException} as INVALID_TOKEN
     * <br> 2. Retrieve User to check in-activated status.
     * <br> &nbsp;Failed: throw {@link ApplicationException} as FORBIDDEN_USER
     * <br> 3. Generate new AccessToken and pay it back to client.
     *
     * @param tokenObject as expired AccessToken.
     * @return Response(AccessToken).
     * @throws ApplicationException by AccessToken.
     */
    //--Refresh Token has already been verified by Oauth2RrcServer - JwtDecoder.
    public TokenDto refreshToken(TokenDto tokenObject) throws ApplicationException {
        //--Verify expired Access Token to symbolize for a valid refreshing token request.
        var jwtClaimsSet = jwtService.verifyTokenOrElseThrow(tokenObject.getToken(), true);
        //--Get User to check if he/she's in in-activated or not.
        var user = userRepository.findByEmail(jwtClaimsSet.getSubject()).orElseThrow(() ->
            new ApplicationException(ErrorCodes.INVALID_TOKEN));
        if (!user.isActive())
            throw new ApplicationException(ErrorCodes.FORBIDDEN_USER);
        //--Refresh new Access Token.
        var accessTok = jwtService.generateAccessToken(user).get("token");
        return TokenDto.builder().token(accessTok).build();
    }

    /**
     * Cases:
     * <br> 1. Both AccessToken & RefreshToken valid: save AccessToken into blacklist, remove RefreshToken from Redis.
     * <br> 2. AccessToken invalids: immediately respond to client successful result (because something is wrong).
     * <br> 3. AccessToken is expired:
     * <br> &nbsp; 3.1. RefreshToken invalids: respond successful logging-out result to client.
     * <br> &nbsp; 3.2. RefreshToken is expired: respond successful logging-out result to client
     * <br> &nbsp; 3.2. RefreshToken valid: remove RefreshToken and respond successful logging-out result to client at the end of service.
     * <br> 4. AccessToken valid, but RefreshToken doesn't: still save AccessToken into blacklist (because it valid and is still stolen).
     *
     * @param refreshToken from headers (Authorization Bearer).
     * @param accessToken  from body.
     * @throws ApplicationException by RefreshToken.
     */
    public void logout(String refreshToken, String accessToken) throws ApplicationException {
        //--Check if Access Token is valid or expired. If it's not, then save into blacklist.
        invalidTokenService.saveInvalidToken(accessToken);  //--May throw ApplicationExc(INVALID_TOKEN).
        //--If Access Token is invalid, the code can't be touched at here to remove Refresh Token.
        var refreshJwt = jwtService.verifyTokenOrElseThrow(refreshToken, false);
        refreshTokenService.removeRefreshTokenByJwtId(refreshJwt.getJWTID());
    }

    public HashMap<String, Object> getForgotPasswordOtp(String email) {
        var userQueryResult = userRepository.findByEmail(email);
        if (userQueryResult.isEmpty() || !userQueryResult.get().isActive())
            throw new ApplicationException(ErrorCodes.FORBIDDEN_USER);

        String otp = generateRandomOtp(4);
        //--Remove the previous OTP code in session if it's existing.
        if (forgotPasswordOtpService.findByEmail(email).isPresent())
            forgotPasswordOtpService.deleteByEmail(email);

        String otpMailMessage = String.format("""
            <div>
                <p style="font-size: 18px">Do not share this information to anyone. Please secure these characters!</p>
                <h2>User Email: <b>%s</b></h2>
                <h2>OTP: <b>%s</b></h2>
            </div>
        """, email, otp);
        emailService.sendSimpleEmail(email, "OTP Code for new password by Home Workout With AI",
            otpMailMessage);

        //--Save into session for the next actions.
        forgotPasswordOtpService.save(ForgotPasswordOtp.builder().id(email).otpCode(otp).build());

        // Schedule a task to remove the OTP after 5 minutes (or your preferred timeout).
        scheduler.schedule(() -> {
            forgotPasswordOtpService.deleteByEmail(email);
            log.info("Forgot Password OTP for {} has expired and been removed.", email);
        }, maxOtpAgeMin, TimeUnit.MINUTES);

        return new HashMap<>(Map.of("ageInSeconds", maxOtpAgeMin*60));
    }

    public void generateRandomPassword(VerifyPublicOtpRequest request) {
        var user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new ApplicationException(ErrorCodes.FORBIDDEN_USER));
        if (!user.isActive())   throw new ApplicationException(ErrorCodes.FORBIDDEN_USER);

        var removedOtp = forgotPasswordOtpService.findByEmail(request.getEmail())
            .orElseThrow(() -> new ApplicationException(ErrorCodes.HIDDEN_OTP_IS_KILLED));
        if (!removedOtp.getOtpCode().equals(request.getOtpCode()))
            throw new ApplicationException(ErrorCodes.HIDDEN_OTP_NOT_FOUND);
        forgotPasswordOtpService.deleteByEmail(removedOtp.getId());

        String newPassword = AuthenticationService.generateRandomOtp(6);
        String newPassMessage = String.format("""
            <div>
                <p style="font-size: 18px">Do not share this information to anyone. Please secure these characters!</p>
                <h2>User Email: <b>%s</b></h2>
                <h2>New Password: <b>%s</b></h2>
            </div>
        """, request.getEmail(), newPassword);
        emailService.sendSimpleEmail(request.getEmail(), "New password by Home Workout With AI", newPassMessage);

        user.setPassword(userPasswordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
