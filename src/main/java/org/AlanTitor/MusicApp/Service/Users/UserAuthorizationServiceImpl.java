package org.AlanTitor.MusicApp.Service.Users;

import lombok.AllArgsConstructor;
import org.AlanTitor.MusicApp.Entity.Users.User;
import org.AlanTitor.MusicApp.Repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UserAuthorizationServiceImpl implements UserAuthorizationService{

    private final UserRepository userRepository;

    @Override
    public User getCurrantUser(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId = (Long) authentication.getPrincipal();
        return userRepository.findById(userId).orElse(null);
    }
}
