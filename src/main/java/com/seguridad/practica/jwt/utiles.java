package com.seguridad.practica.jwt;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import java.security.Key;
import java.time.LocalDate;

import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.util.Date;

import javax.crypto.SecretKey;

@Service 
public class utiles {
    
    @Value("${jwt.secret-key}")
    String secretKey;

    @Value("${jwt.expiration}")
    Long expiration;

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generarToken( Authentication autenticador) {
        return Jwts.builder()
        .subject(autenticador.getName())
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(getKey())
        .compact();
    }

     public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }
    public boolean isTokenValid(String token, UserDetails user) {
        Claims claims = extractClaims(token);
        return claims.getSubject().equals(user.getUsername())
                && claims.getExpiration().after(new Date());
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
