package org.AlanTitor.MusicApp.Jwt;

import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "spring.jwt")
public class JwtConfig {

    private String key;
    private int accessTokenExpiration;
    private int refreshTokenExpiration;

    public SecretKey generateSecretKey(){
        return Keys.hmacShaKeyFor(key.getBytes());
    }
}
