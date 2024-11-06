package com.distributionsys.backend.services.redis;

import com.distributionsys.backend.entities.redis.RefreshToken;
import com.distributionsys.backend.repositories.RefreshTokenCrud;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenCrud refreshTokenCrud;

    public void saveRefreshToken(RefreshToken refreshToken) {
        refreshTokenCrud.save(refreshToken);
    }

    public boolean checkExistRefreshTokenByJwtId(String id) {
        return refreshTokenCrud.existsById(id);
    }

    public void removeRefreshTokenByJwtId(String id) {
        refreshTokenCrud.deleteById(id);
    }
}
