package org.AlanTitor.MusicApp.Service;

import lombok.AllArgsConstructor;
import org.AlanTitor.MusicApp.Entity.Musics.Music;
import org.AlanTitor.MusicApp.Entity.Users.User;
import org.AlanTitor.MusicApp.Exception.CustomExceptions.MusicNotFoundException;
import org.AlanTitor.MusicApp.Repository.MusicRepository;
import org.AlanTitor.MusicApp.Service.Users.UserAuthorizationServiceImpl;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service("musicSecurity")
public class MethodSecurityService {
    private final MusicRepository musicRepository;
    private final UserAuthorizationServiceImpl userAuthorizationService;

    public boolean canModify(Long musicId){
        User user =  userAuthorizationService.getCurrantUser();
        Music music = musicRepository.getMusicById(musicId).orElseThrow(MusicNotFoundException::new);
        return music.getAuthorId().equals(user);
    }
}
