package com.distributionsys.backend.dtos.response;

import com.distributionsys.backend.annotations.dev.Overload;
import com.distributionsys.backend.enums.ErrorCodes;
import com.distributionsys.backend.enums.SucceedCodes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponseObject<T> {
    private int applicationCode;
    private String message;
    private int httpStatusCode;
    private T data;
    private LocalDateTime responseTime;

    public static ResponseEntity<ApiResponseObject<Void>> buildByErrorCodes(ErrorCodes errorCodes, String message) {
        var result = new ApiResponseObject<Void>();
        result.setApplicationCode(errorCodes.getCode());
        result.setMessage(message);
        result.setHttpStatusCode(errorCodes.getHttpStatus().value());
        result.setData(null);
        result.setResponseTime(LocalDateTime.now(ZoneId.from(ZonedDateTime.now())));
        return ResponseEntity.status(errorCodes.getHttpStatus()).body(result);
    }

    @Overload
    public static ResponseEntity<ApiResponseObject<Void>> buildByErrorCodes(ErrorCodes errorCodes) {
        return ApiResponseObject.buildByErrorCodes(errorCodes, errorCodes.getMessage());
    }

    public static <T> ResponseEntity<ApiResponseObject<T>> buildSuccessResponse(SucceedCodes succeedCodes, T data) {
        var result = new ApiResponseObject<T>();
        result.setApplicationCode(succeedCodes.getCode());
        result.setMessage(succeedCodes.getMessage());
        result.setHttpStatusCode(HttpStatus.OK.value());
        result.setData(data);
        result.setResponseTime(LocalDateTime.now(ZoneId.from(ZonedDateTime.now())));
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @Overload
    public static ResponseEntity<ApiResponseObject<Void>> buildSuccessResponse(SucceedCodes succeedCodes) {
        return ApiResponseObject.buildSuccessResponse(succeedCodes, null);
    }
}
