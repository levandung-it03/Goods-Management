package com.distributionsys.backend.exceptions;

import com.distributionsys.backend.enums.ErrorCodes;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;


@Getter
public class ExpiredTokenException extends AuthenticationException {
    private final ErrorCodes errorCodes;

    public ExpiredTokenException() {
        super(ErrorCodes.EXPIRED_TOKEN.getMessage());
        this.errorCodes = ErrorCodes.EXPIRED_TOKEN;
    }
}
