package com.distributionsys.backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.distributionsys.backend.dtos.request.NewClientRequest;
import com.distributionsys.backend.dtos.request.PaginatedTableRequest;
import com.distributionsys.backend.dtos.response.ApiResponseObject;
import com.distributionsys.backend.dtos.response.ClientInfoResponse;
import com.distributionsys.backend.dtos.response.TablePagesResponse;
import com.distributionsys.backend.entities.sql.User;
import com.distributionsys.backend.enums.SucceedCodes;
import com.distributionsys.backend.services.AdminService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api/private")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminController {
    AdminService adminService;

    @GetMapping("/admin/v1/get-information")
    public ResponseEntity<ApiResponseObject<ClientInfoResponse>> getAdminInformation(
        @RequestHeader("Authorization") String accessToken) {
        return ApiResponseObject.buildSuccessResponse(
            SucceedCodes.GET_CLIENT_INFO,
            this.adminService.getAdminInformation(accessToken));
    }

    @GetMapping("/admin/v1/count-total-clients")
    public ResponseEntity<ApiResponseObject<Long>> getTotalClient() {
        return ApiResponseObject.buildSuccessResponse(
            SucceedCodes.GET_TOTAL_CLIENT,
            this.adminService.getTotalClient());
    }

    @GetMapping("/admin/v1/count-total-active-clients")
    public ResponseEntity<ApiResponseObject<Long>> getTotalActiveClient() {
        return ApiResponseObject.buildSuccessResponse(
            SucceedCodes.GET_TOTAL_CLIENT,
            this.adminService.getTotalActiveClient());
    }

    @GetMapping("/admin/v1/count-total-inactive-clients")
    public ResponseEntity<ApiResponseObject<Long>> getTotalInactiveClient() {
        return ApiResponseObject.buildSuccessResponse(
            SucceedCodes.GET_TOTAL_CLIENT,
            this.adminService.getTotalInactiveClient());
    }

    @GetMapping("/admin/v1/get-full-users-as-page")
    public ResponseEntity<ApiResponseObject<TablePagesResponse<User>>> getFullInfoGoodsPages(
        @Valid PaginatedTableRequest request) {
        return ApiResponseObject.buildSuccessResponse(
            SucceedCodes.GET_CLIENTS,
            adminService.getAllUsers(request));
    }

    @PostMapping("/admin/v1/create-new-client")
    public ResponseEntity<ApiResponseObject<User>> createNewClient(
        @RequestBody NewClientRequest request) {
        return ApiResponseObject.buildSuccessResponse(
            SucceedCodes.NEW_CLIENT_CREATED,
            this.adminService.createClient(request));
    }

    @PatchMapping("/admin/v1/deactivate-client/{userId}")
    public ResponseEntity<ApiResponseObject<User>> deactivateClient(
        @PathVariable long userId) {
        return ApiResponseObject.buildSuccessResponse(
            SucceedCodes.DEACTIVATE_CLIENT,
            this.adminService.deactivateClient(userId));
    }
}
