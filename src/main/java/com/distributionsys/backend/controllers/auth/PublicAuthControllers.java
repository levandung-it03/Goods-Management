package com.distributionsys.backend.controllers.auth;

import com.distributionsys.backend.dtos.request.*;
import com.distributionsys.backend.dtos.response.ApiResponseObject;
import com.distributionsys.backend.dtos.response.AuthenticationResponse;
import com.distributionsys.backend.enums.SucceedCodes;
import com.distributionsys.backend.services.auth.AuthenticationService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public/auth")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PublicAuthControllers {
    AuthenticationService authenticationService;

    @ResponseBody
    @PostMapping("/v1/authenticate")
    public ResponseEntity<ApiResponseObject<AuthenticationResponse>> authenticate(
        @Valid @RequestBody AuthenticationRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.AUTHENTICATION,
            authenticationService.authenticate(request));
    }

    @ResponseBody
    @PostMapping("/v1/get-forgot-password-otp")
    public ResponseEntity<ApiResponseObject<HashMap<String, Object>>> getForgotPasswordOtp(
        @Valid @RequestBody GetOtpRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_OTP,
            authenticationService.getForgotPasswordOtp(request.getEmail()));
    }

    @ResponseBody
    @PostMapping("/v1/generate-random-password")
    public ResponseEntity<ApiResponseObject<Void>> generateRandomPassword(
        @Valid @RequestBody VerifyPublicOtpRequest req) {
        authenticationService.generateRandomPassword(req);
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.SEND_RANDOM_PASSWORD);
    }
}
