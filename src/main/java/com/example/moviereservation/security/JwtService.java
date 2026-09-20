package com.example.moviereservation.security;

import com.example.moviereservation.entity.User;
import com.example.moviereservation.enums.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String SECRET;

    public String generateToken
            (Map<String, Object> extraClaims,
             User user
            ){

        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(user.getId().toString())
                .claim("role", user.getRole().name())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsFunction){
        Claims claims = extractClaims(token);
        return claimsFunction.apply(claims);
    }
    public Integer extractUserId(String token) {
        return Integer.parseInt(extractClaim(token,Claims::getSubject));
    }
    public UserRole extractUserRole(String token){
        return UserRole.valueOf(
                extractClaim(token,claims -> claims.get("role",String.class)));
    }

    public Date extractExpiration(String token){
        return extractClaim(token,Claims::getExpiration);
    }
    public boolean isTokenValid(String token, User user){
        Integer userId = extractUserId(token);
        return (userId.equals(user.getId()) && !isTokenExpired(token));
    }
    public boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date(System.currentTimeMillis()));
    }
    public Claims extractClaims(String token){
        return Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);
        return key;
    }
}
