package com.cardcase.server.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class JwtServiceImpl implements JwtService {
    private static final String SECRET_KEY = "swjkf#$545jt*sa";
    private static final long JWT_EXPIRATION_MINUTES = 7*24*60;

    @Override
    public String createToken(Map<String, String> map) {
        long expMillis = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(JWT_EXPIRATION_MINUTES);
        Date expDate = new Date(expMillis);
        JWTCreator.Builder builder = JWT.create();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            builder.withClaim(entry.getKey(), entry.getValue());
        }
        return builder.withExpiresAt(expDate).sign(Algorithm.HMAC256(SECRET_KEY));
    }

    @Override
    public boolean checkToken(String authHeader) {
        String token = authHeader;
        if (authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }
        try {
            JWT.require(Algorithm.HMAC256(SECRET_KEY)).build().verify(token);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
