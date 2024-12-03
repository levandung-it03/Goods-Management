package com.distributionsys.backend.controllers;

import com.distributionsys.backend.dtos.request.AuthenticationRequest;
import com.distributionsys.backend.dtos.request.ChangePasswordRequest;
import com.distributionsys.backend.dtos.request.VerifyAuthOtpRequest;
import com.distributionsys.backend.dtos.response.ApiResponseObject;
import com.distributionsys.backend.entities.redis.ChangePasswordOtp;
import com.distributionsys.backend.enums.SucceedCodes;
import com.distributionsys.backend.services.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/api/private")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @ResponseBody
    @PostMapping("/user/v1/get-otp-to-change-password")
    public ResponseEntity<ApiResponseObject<HashMap<String, Object>>> getOtpToChangePassword(
        @Valid @RequestBody AuthenticationRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_OTP_TO_CHANGE_PASSWORD,
            userService.getOtpToChangePassword(request));
    }

    @ResponseBody
    @PostMapping("/user/v1/verify-change-password-otp")
    public ResponseEntity<ApiResponseObject<ChangePasswordOtp>> verifyOtpToChangePassword(
        @RequestHeader("Authorization") String accessToken,
        @Valid @RequestBody VerifyAuthOtpRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.VERIFY_OTP,
            userService.verifyOtpToChangePassword(request, accessToken));
    }

    @ResponseBody
    @PostMapping("/user/v1/change-password")
    public ResponseEntity<ApiResponseObject<Void>> changePassword(
        @RequestHeader("Authorization") String accessToken,
        @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(request, accessToken);
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.CHANGE_PASSWORD);
    }
}
