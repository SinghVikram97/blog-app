package com.vikram.blogapp.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTService {
    // TODO: Move to secrets
    private static final String SECRET_KEY = "zaUfp/QGfBN7YC6WKaHn2zyLtddrz/gwgN68ofiuwS1eGrfs20U+hRvEeeVaUaDgVwOD961lvmrXqbKW2YKPSZlAoSIj1VNUDZKaJd5vfWwd+6eZvkhOabWC+jvKDDXOW7TXkYCGFWBOXUcHgz7Ti6PCKDD1XcDfhGu2HVCT7ln0c2SmrK3zGMvgCTPhSJ+hyAThk0/rjcau6bucGsyG0jX0EtvlbJRdykw/9aYGh732ZxRQeRZ/V6VCm5AwlpFDmMkqZs/MvZK8Mcz/wPz+TLLPFSDB24Pz33a1mVB6EYXtZOxyh3klP/emD3THI4aW6FRgbcWxK+01dufPYd8ILpgF/5R6aP+1IRclW/31DU8=";

    public String extractUsername(String jwtToken) {
        return extractClaim(jwtToken, Claims::getSubject);
    }

    public <T> T extractClaim(String jwtToken, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(jwtToken);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() +1000*60*24)) // Valid for one day
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

    }

    // Check if its a valid token
    public boolean isValidToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String jwtToken) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
