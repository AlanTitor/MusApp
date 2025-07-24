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
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
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

    private final ResourceLoader resourceLoader;


    @CacheEvict(value = {"allMusic", "singleMusic", "singleMusicFile", "allUsers", "oneUser"}, allEntries = true)
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

    // сделать рефактор поиска пути
    public Resource getMusicFileById(Long id) throws IOException {
        Music music = musicRepository.findById(id).orElseThrow(FileNotFoundException::new);
        String path = "file:" + music.getFilePath() + "\\" + music.getName();
        Resource file = resourceLoader.getResource(path);

        if(!file.exists() || !file.isReadable()){
            throw new IOException("Can not send file!");
        }
        return file;
    }
}
