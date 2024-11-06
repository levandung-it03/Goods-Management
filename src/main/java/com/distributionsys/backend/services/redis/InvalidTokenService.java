package com.distributionsys.backend.services.redis;

import com.distributionsys.backend.entities.redis.InvalidToken;
import com.distributionsys.backend.exceptions.ApplicationException;
import com.distributionsys.backend.repositories.InvalidTokenCrud;
import com.distributionsys.backend.services.auth.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class InvalidTokenService {
    private final InvalidTokenCrud invalidTokenCrud;
    private final JwtService jwtService;

    public boolean existByJwtId(String id) {
        return invalidTokenCrud.existsById(id);
    }

    public void saveInvalidToken(String token) throws ApplicationException {
        var parsedJwt = jwtService.verifyTokenOrElseThrow(token, true);
        //--Proactively check expiry time to save into blacklist or not, not throw exception.
        if (new Date().before(parsedJwt.getExpirationTime()))
            invalidTokenCrud.save(InvalidToken.builder()
                .id(parsedJwt.getJWTID())
                .expiryDate(parsedJwt.getExpirationTime().toInstant())
                .build());
    }
}
