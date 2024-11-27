package com.distributionsys.backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.distributionsys.backend.dtos.response.ApiResponseObject;
import com.distributionsys.backend.entities.sql.User;
import com.distributionsys.backend.enums.SucceedCodes;
import com.distributionsys.backend.services.AdminService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api/private")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminController {
    AdminService adminService;

    @GetMapping("/admin/v1/count-total-clients")
    public ResponseEntity<ApiResponseObject<Long>> getTotalClient() {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_TOTAL_CLIENT, this.adminService.getTotalClient());
    }

    @GetMapping("/admin/v1/count-total-active-clients")
    public ResponseEntity<ApiResponseObject<Long>> getTotalActiveClient() {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_TOTAL_CLIENT, this.adminService.getTotalActiveClient());
    }

    @GetMapping("/admin/v1/count-total-inactive-clients")
    public ResponseEntity<ApiResponseObject<Long>> getTotalInactiveClient() {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_TOTAL_CLIENT, this.adminService.getTotalInactiveClient());
    }

    @PatchMapping("/admin/v1/deactivate-client/{userId}")
    public ResponseEntity<ApiResponseObject<User>> deactivateClient(@PathVariable long userId) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.DEACTIVATE_CLIENT, this.adminService.deactivateClient(userId));
    }
}
