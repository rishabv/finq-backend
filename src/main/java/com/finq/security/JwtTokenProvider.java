package com.finq.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.springframework.beans.factory.annotation.Value;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationInMs;

    @Value("${jwt.refresh-expiration}")
    private long jwtRefreshExpirationInMs;

    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return generateTokenFromUserPrincipal(userPrincipal);
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(this.jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    public String generateTokenFromUserPrincipal(UserPrincipal principal) {
        Date exp = new Date(System.currentTimeMillis() + jwtExpirationInMs);
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", principal.getId().toString());
        claims.put("email", principal.getEmail());
        claims.put("roles", Collections.singletonList(principal.getUserType()));
        if (principal.getCustomerId() != null) {
            claims.put("customerId", principal.getCustomerId());
        }

        if (principal.getEmployeeId() != null) {
            claims.put("employeeId", principal.getEmployeeId());
        }

        return Jwts.builder()
                .claims(claims)
                .subject(principal.getId().toString())
                .issuedAt(new Date())
                .expiration(exp)
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken(UserPrincipal userPrincipal) {
        Date expiryDate = new Date(System.currentTimeMillis() + jwtRefreshExpirationInMs);

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userPrincipal.getId().toString());
        claims.put("tokenType", "refresh");
        return Jwts.builder()
                .claims(claims)
                .subject(userPrincipal.getId().toString())
                .issuedAt(new Date())
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    public UUID getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return UUID.fromString(claims.getSubject());
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("email", String.class);
    }

    public String getUserTypeFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        Object userTypeObj = claims.get("userType");
        if (userTypeObj instanceof String) {
            return (String) userTypeObj;
        } else if (userTypeObj instanceof List) {
            // Get the first role if it's a list
            List<?> userTypes = (List<?>) userTypeObj;
            return userTypes.isEmpty() ? null : userTypes.get(0).toString();
        }
        return null;
    }

    public Date getExpirationDateFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getExpiration();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(authToken);
            System.out.println("-----------------------" + getUserTypeFromToken(authToken));
            return true;
        } catch (SecurityException ex) {
            logger.error("Invalid JWT signature: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty: {}", ex.getMessage());
        }
        return false;
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Collection<? extends GrantedAuthority> extractAuthorities(Claims claims) {
        Collection<GrantedAuthority> authorities = new HashSet<>();
        if (claims.containsKey("roles")) {
            Object rolesClaim = claims.get("roles");
            @SuppressWarnings("unchecked")
            Collection<String> roles = (Collection<String>) rolesClaim;
            roles.forEach(role -> {
//                if (!role.startsWith("ROLE_")) {
//                    role = "ROLE_" + role;
//                }
                authorities.add(new SimpleGrantedAuthority(role));
            });
        }
        return authorities;
    }
}
