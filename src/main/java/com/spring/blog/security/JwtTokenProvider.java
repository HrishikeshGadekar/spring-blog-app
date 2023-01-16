package com.spring.blog.security;

import com.spring.blog.exceptions.BlogApplicationExceptionHandler;
import com.spring.blog.exceptions.BlogException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationDate;

    public String generateJwtToken(Authentication authentication) {
        String username = authentication.getName();

        Date currentDate = new Date();

        Date expiryDate = new Date(currentDate.getTime() + jwtExpirationDate);

        String token = Jwts.builder()
                           .setSubject(username)
                           .setIssuedAt(new Date())
                           .setExpiration(expiryDate)
                           .signWith(key())
                           .compact();

        return token;
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String getUsername(String token) {
        Claims claims = Jwts.parserBuilder()
                            .setSigningKey(key())
                            .build()
                            .parseClaimsJws(token)
                            .getBody();

        String username = claims.getSubject();
        return username;
    }

    public boolean validateJwt(String token) {

        try {
            Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parse(token);

            return true;
        } catch (MalformedJwtException malformedJwtException) {
            throw new BlogException(HttpStatus.BAD_REQUEST, "Invalid JWT Token");
        } catch (ExpiredJwtException expiredJwtException) {
            throw new BlogException(HttpStatus.BAD_REQUEST, "Expired JWT Token");
        } catch (UnsupportedJwtException unsupportedJwtException) {
            throw new BlogException(HttpStatus.BAD_REQUEST, "Unsupported JWT Token");
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new BlogException(HttpStatus.BAD_REQUEST, "JWT Claims string is empty");
        }

    }
}
