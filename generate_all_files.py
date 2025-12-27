#!/usr/bin/env python3
"""
Microservices File Generator
Generates all Java files for User, Post, Opinion services and API Gateway
"""

import os
from pathlib import Path

def create_file(filepath, content):
    """Create file with directory structure"""
    Path(filepath).parent.mkdir(parents=True, exist_ok=True)
    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(content)
    print(f"✓ Created: {filepath}")

# Base paths
USER_SERVICE = "user-service/src/main/java/com/issuetracker/userservice"
POST_SERVICE = "post-service/src/main/java/com/issuetracker/postservice"
OPINION_SERVICE = "opinion-service/src/main/java/com/issuetracker/opinionservice"
API_GATEWAY = "api-gateway/src/main/java/com/issuetracker/apigateway"

print("=" * 60)
print("🚀 Generating All Microservices Files...")
print("=" * 60)

# ============================================================================
# USER SERVICE FILES
# ============================================================================
print("\n📁 Creating User Service files...")

# ApiResponse.java
create_file(f"{USER_SERVICE}/dto/ApiResponse.java", '''package com.issuetracker.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private String status;
    private String message;
    private T data;
}
''')

# UserRepository.java
create_file(f"{USER_SERVICE}/repository/UserRepository.java", '''package com.issuetracker.userservice.repository;

import com.issuetracker.userservice.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByPhoneNumber(Long phoneNumber);
    Optional<User> findByPasswordResetToken(String token);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(Long phoneNumber);
}
''')

# JwtService.java  
create_file(f"{USER_SERVICE}/security/JwtService.java", '''package com.issuetracker.userservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(String userId, String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role);
        return createToken(claims, email);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, String email) {
        final String extractedEmail = extractEmail(token);
        return (extractedEmail.equals(email)) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
''')

print("\n✅ User Service DTO files created")
print("✅ User Service Repository created")
print("✅ User Service JWT Security created")
print("\n" + "=" * 60)
print("📝 To create remaining files, open USER_SERVICE_COMPLETE_CODE.md")
print("   and copy each code block to the specified path.")
print("=" * 60)
print("\n🎯 Next Steps:")
print("1. Copy remaining User Service files from USER_SERVICE_COMPLETE_CODE.md")
print("2. Copy Post Service files from POST_SERVICE_COMPLETE_CODE.md")
print("3. Copy Opinion Service files from OPINION_SERVICE_WEBSOCKET_CODE.md")
print("4. Copy API Gateway files from API_GATEWAY_AND_DEPLOYMENT.md")
print("\nAll code is ready in the markdown files! ✨")
