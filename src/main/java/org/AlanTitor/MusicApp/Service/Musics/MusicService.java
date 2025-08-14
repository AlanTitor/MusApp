package org.AlanTitor.MusicApp.Service.Musics;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.AlanTitor.MusicApp.Dto.Musics.MusicUploadDto;
import org.AlanTitor.MusicApp.Dto.Musics.ResponseMusicDataDto;
import org.AlanTitor.MusicApp.Entity.Musics.Music;
import org.AlanTitor.MusicApp.Entity.Users.User;
import org.AlanTitor.MusicApp.Exception.CustomExceptions.IncorrectFileData;
import org.AlanTitor.MusicApp.Exception.CustomExceptions.MusicNotFoundException;
import org.AlanTitor.MusicApp.Mapper.MusicMapper;
import org.AlanTitor.MusicApp.Repository.MusicRepository;
import org.AlanTitor.MusicApp.Service.Musics.FileStorage.FileMetadataService;
import org.AlanTitor.MusicApp.Service.Musics.FileStorage.FileStorageServiceImpl;
import org.AlanTitor.MusicApp.Service.Users.UserAuthorizationServiceImpl;
import org.AlanTitor.MusicApp.Service.Users.UserService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.core.io.Resource;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

@Validated
@RequiredArgsConstructor
@Service
public class MusicService {

    private final MusicRepository musicRepository;

    private final MusicMapper musicMapper;

    private final FileStorageServiceImpl fileStorageService;
    private final FileMetadataService fileMetadataService;

    private final UserAuthorizationServiceImpl userAuthorizationService;

    private final ObjectMapper objectMapper;

    @CacheEvict(value = {"music", "user"}, allEntries = true)
    public Music uploadMusic(@Valid MusicUploadDto musicUploadDto, MultipartFile file) throws IOException {
        if(!fileStorageService.isValidAudioFile(file)) throw new IncorrectFileData();
        User user = userAuthorizationService.getCurrantUser();
        Music music = musicMapper.toEntity(musicUploadDto);
        Path savedMusicPath = fileStorageService.store(file, musicUploadDto.getName());
        fileMetadataService.setFileProperties(music, file, musicUploadDto, savedMusicPath, user);
        return musicRepository.save(music);
    }

    @Cacheable(key = "#id", cacheNames = "music")
    public ResponseMusicDataDto getMusicById(Long id){
        return musicMapper.toDto(musicRepository.getMusicById(id).orElse(null));
    }

    @Cacheable(cacheNames = "music")
    public List<ResponseMusicDataDto> getAllMusic(){
        List<Music> musics = musicRepository.getAllMusic();

        return musics.stream()
                .map(musicMapper::toDto)
                .toList();
    }

    public Resource getMusicFileById(Long id) throws IOException {
        Music music = musicRepository.findById(id).orElseThrow(MusicNotFoundException::new);
        return fileStorageService.loadAsResource(music.getFilePath(), music.getName());
    }

    @Caching(
            put = @CachePut(value = "music", key = "#id"),
            evict = @CacheEvict(value = {"music", "user"}, allEntries = true)
    )
    public void patchMusicMetaData(Long id, JsonPatch patch) throws JsonPatchException, IOException {
        User user = userAuthorizationService.getCurrantUser();
        Music music = musicRepository.getMusicById(id).orElseThrow(MusicNotFoundException::new);

        if(!(user == music.getAuthorId())){
            throw new AuthorizationDeniedException("You can't change this song!");
        }

        Music patchedMusic = patchMusic(music, patch);

        if(patchedMusic.getName().equals(music.getName())){
            throw new FileAlreadyExistsException("File with the same name is exists!");
        }

        File oldFile = new File(music.getFilePath(), music.getName());

        if(!oldFile.exists()){
            throw new FileNotFoundException();
        }

        renameFile(music, patchedMusic);

        musicRepository.save(patchedMusic);
    }

    // relates to patchMusicMetaData()
    private Music patchMusic(Music music, JsonPatch patch) throws JsonPatchException, JsonProcessingException {
        JsonNode originNode = objectMapper.convertValue(music, JsonNode.class);
        JsonNode patchedNode = patch.apply(originNode);

        Music patchedMusic = objectMapper.treeToValue(patchedNode, Music.class);
        patchedMusic.setId(music.getId());
        patchedMusic.setAuthorId(music.getAuthorId());

        return patchedMusic;
    }

    // relates to patchMusicMetaData()
    private void renameFile(Music music, Music patchedMusic) throws IOException {
        Path oldPath = Path.of(music.getFilePath(), music.getName());
        String newFileName = patchedMusic.getName() + patchedMusic.getFileExtension();
        Path newPath = Path.of(music.getFilePath(), newFileName);

        Files.move(oldPath, newPath);

        patchedMusic.setName(newFileName);
        patchedMusic.setDateChanged(LocalDateTime.now());
    }

    @CacheEvict(key = "#id", value = {"music", "user"}, allEntries = true)
    public void deleteMusic(Long id) throws IOException {
        User user = userAuthorizationService.getCurrantUser();
        Music music = musicRepository.getMusicById(id).orElseThrow(MusicNotFoundException::new);
        fileStorageService.delete(music);
        musicRepository.deleteById(id);
    }
}
