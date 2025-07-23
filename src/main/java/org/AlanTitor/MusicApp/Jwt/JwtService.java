package org.AlanTitor.MusicApp.Jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.AlanTitor.MusicApp.Entity.Users.User;
import org.AlanTitor.MusicApp.Repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@AllArgsConstructor
public class JwtService {

    private final JwtConfig jwtConfig;
    private final UserRepository userRepository;

    public Jwt generateAccessToken(User user){
        return generateToken(user, jwtConfig.getAccessTokenExpiration());
    }
    public Jwt generateRefreshToken(User user){
        return generateToken(user, jwtConfig.getRefreshTokenExpiration());
    }

    private Jwt generateToken(User user, long tokenExpiration){
        Claims claims = Jwts.claims()
                .subject(user.getId().toString())
                .add("email", user.getEmail())
                .add("name", user.getName())
                .add("role", user.getRole())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * tokenExpiration))
                .build();

        return new Jwt(claims, jwtConfig.generateSecretKey());
    }

    public Jwt parseToken(String token){
        try{
            Claims claims = getClaims(token);
            return new Jwt(claims, jwtConfig.generateSecretKey());
        }catch (JwtException e){
            return null;
        }
    }

    private Claims getClaims(String token){
        return Jwts.parser()
                .verifyWith(jwtConfig.generateSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


}
