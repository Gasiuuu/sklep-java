package com.sklep.sklep_backend.service;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashMap;
import java.util.function.Function;

public interface JWTUtils {
    String generateToken(UserDetails userDetails);
    String generateRefreshToken(HashMap<String, Object> claims, UserDetails userDetails);
    String extractUsername(String token);
    <T> T extractClaims(String token, Function<Claims, T> claimsTFunction);
    boolean isTokenValid(String token, UserDetails userDetails);
    boolean isTokenExpired(String token);


}
