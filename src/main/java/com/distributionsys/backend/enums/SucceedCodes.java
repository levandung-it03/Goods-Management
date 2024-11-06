package com.distributionsys.backend.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum SucceedCodes {
    //--Auth(21)
    AUTHENTICATION(21001, "Authenticate successfully"),
    REFRESHING_TOKEN(21002, "Refresh Token successfully"),
    LOGOUT(21003, "Logout successfully"),
    GET_OTP(21004, "Get OTP successfully"),
    VERIFY_OTP(21005, "Verify OTP successfully"),
    SEND_RANDOM_PASSWORD(21006, "Send random password into your email successfully"),
    GET_OTP_TO_CHANGE_PASSWORD(21007, "Authentication successfully and sent OTP"),
    CHANGE_PASSWORD(21008, "Change password successfully"),
    //--Enum(22)
    GET_ALL_GENDER_ENUMS(22001, "Get all Genders successfully"),
    ;

    int code;
    String message;
}
