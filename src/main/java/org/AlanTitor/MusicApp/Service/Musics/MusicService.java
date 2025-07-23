package org.AlanTitor.MusicApp.Service.Musics;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.AlanTitor.MusicApp.Dto.Musics.MusicUploadDto;
import org.AlanTitor.MusicApp.Dto.Musics.ResponseMusicDataDto;
import org.AlanTitor.MusicApp.Entity.Musics.Music;
import org.AlanTitor.MusicApp.Entity.Users.User;
import org.AlanTitor.MusicApp.Exception.IncorrectFileData;
import org.AlanTitor.MusicApp.Mapper.MusicMapper;
import org.AlanTitor.MusicApp.Repository.MusicRepository;
import org.AlanTitor.MusicApp.Service.Users.UserService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Validated
@RequiredArgsConstructor
@Service
public class MusicService {

    private final MusicRepository musicRepository;

    private final MusicMapper musicMapper;
    private final UserService userService;
    private final MusicConfig musicConfig;


    @CacheEvict(value = "allMusic")
    public Music uploadMusic(@Valid MusicUploadDto musicUploadDto, MultipartFile multFile) throws IOException {
        User user = userService.getCurrantUser();
        MusicFile musicFile = new MusicFile(multFile, musicConfig.getPath());

        if(!musicFile.isValidAudioFile()){
            throw new IncorrectFileData();
        }

        Music music = musicMapper.toEntity(musicUploadDto);
        musicMapper.setFileProperties(music, multFile);

        music.setName(musicConfig.generateUniqueFileName(musicUploadDto.getName(), musicFile.getOriginalFilename()));
        musicFile.setFileName(music.getName());
        music.setFilePath(musicFile.moveFile().toString());
        //set currant user is authored
        music.setAuthorId(user);

        return musicRepository.save(music);
    }

    @Cacheable(value = "allMusic")
    public List<ResponseMusicDataDto> getAllMusic(){
        List<Music> musics = musicRepository.getAllMusic();

        return musics.stream()
                .map(musicMapper::toDto)
                .toList();
    }

    @Cacheable(key = "#id", value = "singleMusic")
    public ResponseMusicDataDto getMusicById(Long id){
        return musicMapper.toDto(musicRepository.findById(id).orElse(null));
    }
}
