package com.gameapp.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.gameapp.core.dto.UserDto;
import com.gameapp.core.dto.UserRole;
import com.gameapp.corepersistence.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.gameapp.core.util.AppException;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

@Component
public class JWTUtils {
    public static final long JWT_TOKEN_VALIDITY = 24L * 60 * 60 * 60 * 1000;
    @Value("${jwt.secret}")
    private String secretKey;

    public String generateToken(User user) {
        return doGenerateToken(user.getId(), user.getRole());
    }

    public UserDto verifyToken(Map<String, Object> headers) {
        String token = (String) headers.get("api-token");
        if (Objects.isNull(token)) {
            throw new AppException("api-token is missing in request");
        }
        DecodedJWT decodedJWT = verifyAndDecodeToken(token);
        UserDto userDto = new UserDto();
        userDto.setLoggedInUserId(decodedJWT.getSubject());
        userDto.setUserRole(UserRole.valueOf(decodedJWT.getClaim("role").asString()));
        return userDto;
    }

    private String doGenerateToken(String userId, UserRole userRole) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey.getBytes());
        return JWT.create()
                .withSubject(userId)
                .withExpiresAt(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .withIssuer("login-api")
                .withClaim("role", userRole.name())
                .sign(algorithm);
    }

    private DecodedJWT verifyAndDecodeToken(String token) {
        if (Objects.isNull(token)) {
            throw new AppException("api token is missing in request");
        }

        Algorithm algorithm = Algorithm.HMAC256(secretKey.getBytes());
        JWTVerifier verifier = JWT.require(algorithm).build();
        return verifier.verify(token);
    }
}