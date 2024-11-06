package com.distributionsys.backend.services.auth;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.distributionsys.backend.annotations.dev.Overload;
import com.distributionsys.backend.entities.sql.User;
import com.distributionsys.backend.enums.ErrorCodes;
import com.distributionsys.backend.exceptions.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.distributionsys.backend.enums.TokenTypes.*;

@Service
@RequiredArgsConstructor
public class JwtService {
    @Value("${services.security.access-token-expired-min}")
    private Long ACCESS_TOKEN_EXPIRED;
    @Value("${services.security.refresh-token-expired-days}")
    private Long REFRESH_TOKEN_EXPIRED;
    private final SecretKeySpec mySecretKeySpec;

    public HashMap<String, String> generateToken(User user, Long duration, String type) {
        try {
            var nowInstant = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).toInstant();
            var tokenId = UUID.randomUUID().toString();
            var jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("home-workout-with-ai")
                .issueTime(new Date(nowInstant.toEpochMilli()))
                .expirationTime(new Date(nowInstant.plus(duration, ChronoUnit.SECONDS).toEpochMilli()))
                .jwtID(tokenId)
                .claim("scope", user.buildScope())
                .claim("type", type)
                .build();
            var jwsHeader = new JWSHeader.Builder(JWSAlgorithm.HS512).type(JOSEObjectType.JWT).build();
            var jwsObject = new JWSObject(jwsHeader, jwtClaimsSet.toPayload());
            jwsObject.sign(new MACSigner(mySecretKeySpec));

            var result = new HashMap<String, String>();
            result.put("id", tokenId);
            result.put("token", jwsObject.serialize());
            return result;
        } catch (JOSEException e) {
            throw new ApplicationException(ErrorCodes.INVALID_TOKEN);
        }
    }

    @Overload
    public HashMap<String, String> generateAccessToken(User user) {
        return this.generateToken(user, ACCESS_TOKEN_EXPIRED*60, ACCESS_TOKEN.name());
    }

    public HashMap<String, String> generateRefreshToken(User user) {
        return this.generateToken(user, REFRESH_TOKEN_EXPIRED*24*60*60, REFRESH_TOKEN.name());
    }

    public JWTClaimsSet verifyTokenOrElseThrow(String token, boolean isIgnoreExpiry) throws ApplicationException {
        try {
            //--Handle token Bearer type or not.
            var plainToken = token.contains("Bearer ") ? token.split("Bearer ")[1] : token;
            //--Prepare components to parse and verify token.
            var signedJWT = SignedJWT.parse(plainToken);
            var macVerifier = new MACVerifier(mySecretKeySpec);
            //--Verify with built Secret Key Spec.
            if (!signedJWT.verify(macVerifier))
                throw new ApplicationException(ErrorCodes.INVALID_TOKEN);
            //--Parse token to proactively check expiry time.
            var jwtClaimsSet = signedJWT.getJWTClaimsSet();
            if (new Date().after(jwtClaimsSet.getExpirationTime())) {
                if (isIgnoreExpiry)    return jwtClaimsSet;    //--Return claimsSet to work with.
                throw new ApplicationException(ErrorCodes.EXPIRED_TOKEN);   //--Throw error to client to login again.
            }
            return jwtClaimsSet;    //--Return claimsSet to work with.
        } catch (JOSEException | ParseException e) {
            throw new ApplicationException(ErrorCodes.INVALID_TOKEN);
        }
    }

    public HashMap<String, String> readPayload(String token) {
        var payload = token.split("\\.")[1];
        int paddingLength = 4 - payload.length() % 4;
        if (paddingLength < 4)    //--Required format if (payload.length % 4) # 0
            payload += "=".repeat(paddingLength);
        var payLoadJson = new String(Base64.getUrlDecoder().decode(payload));
        var strItems = payLoadJson.replaceAll("[{}]", "").split(",");
        var result = new HashMap<String, String>();
        Arrays.stream(strItems).forEach(strItem -> {
            var items = strItem.replaceAll("[\"]", "").split(":");
            result.put(items[0], items[1]);
        });
        return result;
    }
}
