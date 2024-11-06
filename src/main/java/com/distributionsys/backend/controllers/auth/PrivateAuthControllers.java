package com.distributionsys.backend.controllers.auth;

import com.distributionsys.backend.dtos.general.TokenDto;
import com.distributionsys.backend.dtos.response.ApiResponseObject;
import com.distributionsys.backend.enums.SucceedCodes;
import com.distributionsys.backend.services.auth.AuthenticationService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/private/auth")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PrivateAuthControllers {
    AuthenticationService authenticationService;

    @ResponseBody
    @PostMapping("/v1/refresh-token")  //--RefreshToken as Authorization-Bearer needed.
    public ResponseEntity<ApiResponseObject<TokenDto>> refreshToken(@Valid @RequestBody TokenDto tokenObject) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.REFRESHING_TOKEN,
            authenticationService.refreshToken(tokenObject));
    }

    @ResponseBody
    @PostMapping("/v1/logout")
    public ResponseEntity<ApiResponseObject<Void>> logout(@RequestHeader("Authorization") String refreshToken,
                                                          @Valid @RequestBody TokenDto tokenObject) {
        authenticationService.logout(refreshToken, tokenObject.getToken());
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.LOGOUT);
    }
}
