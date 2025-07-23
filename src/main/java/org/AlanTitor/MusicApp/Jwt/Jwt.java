package org.AlanTitor.MusicApp.Jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.AlanTitor.MusicApp.Entity.Users.Role;

import javax.crypto.SecretKey;
import java.util.Date;

public class Jwt {

    private final Claims claims;
    private final SecretKey key;

    public Jwt(Claims claims, SecretKey key) {
        this.claims = claims;
        this.key = key;
    }

    public boolean isExpired(){
        return claims.getExpiration().before(new Date());
    }

    public Long getUserId(){
        return Long.valueOf(claims.getSubject());
    }

    public Role getUserRole(){
        return Role.valueOf(claims.get("role", String.class));
    }

    public String toString(){
        return Jwts.builder().claims(claims).signWith(key).compact();
    }
}
