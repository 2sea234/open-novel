package com.opennovel.common.utils;

import com.opennovel.common.jwt.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;


@RequiredArgsConstructor
@Component
public class JwtUtil {


    private final JwtProperties jwtProperties;

    public String createJwt(Long adminId, String username) {
        Date now = new Date();

        Date expire = new Date(now.getTime() + jwtProperties.getExpireTime());

        return Jwts.builder()
                .claim("adminId", adminId)
                .claim("username", username)
                .claim("loginType", "ADMIN")
                .issuedAt(now)
                .expiration(expire)
                .signWith(getKey())
                .compact();
    }

    private SecretKey getKey() {
        byte[] decode = Decoders.BASE64.decode(jwtProperties.getSecret());
        return Keys.hmacShaKeyFor(decode);
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
