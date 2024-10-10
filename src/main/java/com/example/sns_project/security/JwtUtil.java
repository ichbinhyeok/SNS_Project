package com.example.sns_project.security;
import com.example.sns_project.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;

public class JwtUtil {

    private static final String SECRET_KEY = "secret_key"; // 비밀 키

    public String generateJwtToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60 * 60 * 1000)) // 1시간 유효
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY) // 비밀 키
                .compact();
    }
}
