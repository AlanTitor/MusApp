package org.AlanTitor.MusicApp.Controller.Users;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.AlanTitor.MusicApp.Dto.Users.LoginUserRequest;
import org.AlanTitor.MusicApp.Dto.Users.LoginUserResponse;
import org.AlanTitor.MusicApp.Entity.Users.User;
import org.AlanTitor.MusicApp.Jwt.Jwt;
import org.AlanTitor.MusicApp.Jwt.JwtConfig;
import org.AlanTitor.MusicApp.Jwt.JwtResponse;
import org.AlanTitor.MusicApp.Service.Users.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserLoginController {

    private final UserService userService;
    private final JwtConfig jwtConfig;

    @PostMapping("/login")
    public JwtResponse loginUser(@RequestBody LoginUserRequest request, HttpServletResponse response){
        LoginUserResponse token = userService.loginUser(request);

        Cookie cookie = new Cookie("refreshToken", token.getRefreshToken().toString());
        cookie.setHttpOnly(true);
        cookie.setPath("/api/refresh");
        cookie.setSecure(true);
        cookie.setMaxAge(jwtConfig.getRefreshTokenExpiration());

        response.addCookie(cookie);

        return new JwtResponse(token.getAccessToken().toString());
    }

    @PostMapping("/refresh")
    public JwtResponse refreshToken(@CookieValue(value = "refreshToken") String refreshToken){
        Jwt accessToken = userService.refreshAccessToken(refreshToken);
        return new JwtResponse(accessToken.toString());
    }

    @GetMapping("/me")
    public boolean getMe(){
        User user = userService.getCurrantUser();

        return user != null;
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleUnauthorized(){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}
