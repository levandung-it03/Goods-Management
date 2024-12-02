package com.distributionsys.backend.exceptions;

import com.distributionsys.backend.dtos.response.ApiResponseObject;
import com.distributionsys.backend.enums.ErrorCodes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler(value = AccessDeniedException.class)
    public ResponseEntity<ApiResponseObject<Void>> handleAuthenticationException(AccessDeniedException exception) {
        var response = ApiResponseObject.buildByErrorCodes(ErrorCodes.FORBIDDEN_USER);
        log.info("[HANDLER]_AccessDeniedException: {}", exception.getMessage());
        return response;
    }

    @ExceptionHandler(value = AuthenticationException.class)
    public ResponseEntity<ApiResponseObject<Void>> handleAuthenticationException(AuthenticationException exception) {
        var response = ApiResponseObject.buildByErrorCodes(ErrorCodes.INVALID_CREDENTIALS);
        log.info("[HANDLER]_AuthenticationException: {}", exception.getMessage());
        return response;
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponseObject<Void>> handleUnawareException(HttpMessageNotReadableException exception) {
        var extractedErr = exception.getCause().toString().split(": ");
        var err = new StringBuilder(extractedErr[extractedErr.length - 1]);
        var fieldName = err.substring(err.indexOf("[\"") + 2, err.indexOf("\"]"));  //--Remove quotes.
        var response = ApiResponseObject.buildByErrorCodes(
            ErrorCodes.PARSE_JSON_ERR,
            ErrorCodes.PARSE_JSON_ERR.getMessage().replace("${field}", fieldName)
        );

        log.info("[HANDLER]_HttpMessageNotReadableException: {}", exception.getMessage());
        return response;
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseObject<Void>> handleHibernateValidatorException(
        MethodArgumentNotValidException exception) {
        var plainErr = exception.getMessage().split(";")[0];
        var startInd = plainErr.indexOf("field '") + 7;
        var endInd = plainErr.indexOf("'", startInd);
        var response = ApiResponseObject.buildByErrorCodes(
            ErrorCodes.VALIDATOR_ERR_RESPONSE,
            ErrorCodes.VALIDATOR_ERR_RESPONSE.getMessage().replace("${field}",
                plainErr.substring(startInd, endInd))
        );

        log.info("[HANDLER]_ValidatorException: {}", plainErr);
        return response;
    }

    @ExceptionHandler(value = ApplicationException.class)
    public ResponseEntity<ApiResponseObject<Void>> handleCustomApplicationException(ApplicationException exception) {
        var response = ApiResponseObject.buildByErrorCodes(exception.getErrorCodes());
        log.info("[HANDLER]_ApplicationException: {}", exception.getMessage());
        return response;
    }

    @ExceptionHandler(value = DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponseObject<Void>> handleConstraintViolationException(
        DataIntegrityViolationException exception) {
        var response = ApiResponseObject.buildByErrorCodes(ErrorCodes.CONSTRAINT_VIOLATION);
        log.info("[HANDLER]_ConstraintViolationException: {}", exception.getMessage());
        return response;
    }

    @ExceptionHandler(value = IOException.class)
    public ResponseEntity<ApiResponseObject<Void>> handleIOException(IOException exception) {
        log.info("[HANDLER]_IOException: {}", exception.getMessage());
        final List<String> ignoredErrors = List.of(
            "ESTABLISHED CONNECTION WAS ABORTED"
        );
        if (ignoredErrors.stream().noneMatch(error -> exception.getMessage().toUpperCase().contains(error)))
            return ApiResponseObject.buildByErrorCodes(ErrorCodes.UNAWARE_ERR);
        else return ResponseEntity.of(Optional.empty());
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ApiResponseObject<Void>> handleUnawareException(Exception exception) {
        var response = ApiResponseObject.buildByErrorCodes(ErrorCodes.UNAWARE_ERR);
        log.info("[HANDLER]_Exception: {}", exception.getMessage());
        return response;
    }
}
