package com.jherrell.exchangerate.infrastructure.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class JwtService {


    public static String getToken(UserDetails user, String secret) {
        return getToken(new HashMap<>(), user, secret);
    }

    private static String getToken(Map<String, Object> extraClaims, UserDetails user, String secret) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // token exp: 10 min
                .signWith(getKey(secret), SignatureAlgorithm.HS256)
                .compact();
    }

    private static Key getKey(String secret) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public static String getUsernameFromToken(String token, String secret) {
        return getClaim(token, Claims::getSubject, secret);
    }

    public static boolean isTokenValid(String token, String  userDetailUsername, String secret) {
        final String username = getUsernameFromToken(token, secret);
        return (username.equals(userDetailUsername) && !isTokenExpired(token, secret));
    }

    private static Claims getAllClaims(String token, String secret) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getKey(secret))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static <T> T getClaim(String token, Function<Claims, T> claimsResolver, String secret) {
        final Claims claims = getAllClaims(token, secret);
        return claimsResolver.apply(claims);
    }

    private static Date getExpiration(String token, String secret) {
        return getClaim(token, Claims::getExpiration, secret);
    }

    private static boolean isTokenExpired(String token, String secret) {
        return getExpiration(token, secret).before(new Date());
    }

    public static String getTokenFromRequest(ServerWebExchange exchange) {
        final String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}