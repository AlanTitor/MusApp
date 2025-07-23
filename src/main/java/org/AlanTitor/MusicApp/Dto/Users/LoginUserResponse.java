package org.AlanTitor.MusicApp.Dto.Users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.AlanTitor.MusicApp.Jwt.Jwt;

@AllArgsConstructor
@Getter
public class LoginUserResponse {
    private Jwt accessToken;
    private Jwt refreshToken;
}
