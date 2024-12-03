package com.distributionsys.backend.controllers;

import com.distributionsys.backend.dtos.request.*;
import com.distributionsys.backend.dtos.response.ApiResponseObject;
import com.distributionsys.backend.dtos.response.ClientInfoResponse;
import com.distributionsys.backend.enums.SucceedCodes;
import com.distributionsys.backend.services.ClientInfoService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/private")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ClientInfoControllers {
    ClientInfoService clientInfoService;

    // Lấy thông tin client đã đăng nhập
    @GetMapping("/user/v1/info")
    public ResponseEntity<ApiResponseObject<ClientInfoResponse>> getClientInfo(
            @RequestHeader("Authorization") String accessToken) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_CLIENT_INFO,
            clientInfoService.getClientInfo(accessToken));
    }

    // Sửa thông tin client đã đăng nhập
    @PutMapping("/user/v1/info")
    public ResponseEntity<ApiResponseObject<Void>> updateClientInfo(
            @RequestHeader("Authorization") String accessToken,
            @RequestBody @Valid ClientInfoRequest updatedInfo) {
        clientInfoService.updateClientInfo(accessToken, updatedInfo);
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.UPDATE_CLIENT_INFO);
    }
}
