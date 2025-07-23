package org.AlanTitor.MusicApp.Service.Users;

import lombok.AllArgsConstructor;
import org.AlanTitor.MusicApp.Dto.Users.*;
import org.AlanTitor.MusicApp.Entity.Users.Role;
import org.AlanTitor.MusicApp.Entity.Users.User;
import org.AlanTitor.MusicApp.Exception.UserDuplicateException;
import org.AlanTitor.MusicApp.Jwt.Jwt;
import org.AlanTitor.MusicApp.Jwt.JwtService;
import org.AlanTitor.MusicApp.Mapper.UserMapper;
import org.AlanTitor.MusicApp.Repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


@AllArgsConstructor
@Service
public class UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public User getCurrantUser(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId = (Long) authentication.getPrincipal();
        return userRepository.findById(userId).orElse(null);
    }

    @CacheEvict(value = "allUsers")
    public RegisterUserResponse registerUser(RegisterUserRequest request){
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new UserDuplicateException();
        }

        request.setPassword(passwordEncoder.encode(request.getPassword()));

        User user =  userMapper.toUser(request);
        user.setRole(Role.ADMIN);
        userRepository.save(user);
        return userMapper.toResponse(user);
    }


    public LoginUserResponse loginUser(LoginUserRequest request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();

        Jwt accessToken = jwtService.generateAccessToken(user);
        Jwt refreshToken = jwtService.generateRefreshToken(user);

        return new LoginUserResponse(accessToken, refreshToken);
    }

    public Jwt refreshAccessToken(String token){
        Jwt jwt = jwtService.parseToken(token);

        if(jwt == null || jwt.isExpired()){
            throw new BadCredentialsException("Invalid refresh token");
        }

        User user = userRepository.findById(jwt.getUserId()).orElseThrow();
        return jwtService.generateAccessToken(user);
    }

    @Cacheable(value = "allUsers")
    public List<ResponseUserDataDto> getAllUsers(){
        List<User> users = userRepository.getAllUsersWithMusic();
        return users.stream().map(userMapper::toDtoWithMusic).toList();
    }
    @Cacheable(value = "oneUser")
    public ResponseUserDataDto getUserById(Long id){
        User user = userRepository.getOneUsersWithMusicById(id).orElse(null);
        return userMapper.toDtoWithMusic(user);
    }

}
