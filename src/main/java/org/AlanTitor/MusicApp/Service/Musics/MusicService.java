package org.AlanTitor.MusicApp.Service.Musics;

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
import org.AlanTitor.MusicApp.Service.Musics.Patch.PatchServiceImpl;
import org.AlanTitor.MusicApp.Service.Users.UserAuthorizationServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.core.io.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
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

    private final PatchServiceImpl patchService;

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

    public Resource getMusicFileById(Long id) throws IOException {
        Music music = musicRepository.findById(id).orElseThrow(MusicNotFoundException::new);
        return fileStorageService.loadAsResource(music.getFilePath(), music.getName());
    }

    @Cacheable(cacheNames = "music")
    public List<ResponseMusicDataDto> getAllMusic(){
        List<Music> musics = musicRepository.getAllMusic();
        return musics.stream()
                .map(musicMapper::toDto)
                .toList();
    }

    @PreAuthorize("@musicSecurity.canModify(#id)")
    @Caching(
            put = @CachePut(value = "music", key = "#id"),
            evict = @CacheEvict(value = {"music", "user"}, allEntries = true)
    )
    public void patchMusicMetaData(Long id, JsonPatch patch) throws JsonPatchException, IOException {
        Music music = musicRepository.getMusicById(id).orElseThrow(MusicNotFoundException::new);
        Music patchedMusic = patchService.patchMusic(music, patch);
        fileStorageService.rename(music.getFilePath(), music.getName(), patchedMusic.getName());
        musicRepository.save(patchedMusic);
    }

    @PreAuthorize("@musicSecurity.canModify(#id)")
    @CacheEvict(key = "#id", value = {"music", "user"}, allEntries = true)
    public void deleteMusic(Long id) throws IOException, MusicNotFoundException {
        Music music = musicRepository.getMusicById(id).orElseThrow(MusicNotFoundException::new);
        fileStorageService.delete(music);
        musicRepository.deleteById(id);
    }
}