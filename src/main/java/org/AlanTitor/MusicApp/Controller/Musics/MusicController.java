package org.AlanTitor.MusicApp.Controller.Musics;

import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import lombok.AllArgsConstructor;
import org.AlanTitor.MusicApp.Dto.Musics.MusicUploadDto;
import org.AlanTitor.MusicApp.Dto.Musics.ResponseMusicDataDto;
import org.AlanTitor.MusicApp.Entity.Musics.Music;
import org.AlanTitor.MusicApp.Service.Musics.MusicService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.List;


@AllArgsConstructor
@RestController
@RequestMapping("/api/musics")
public class MusicController {

    private final MusicService musicService;

    @PostMapping
    public ResponseEntity<?> uploadMusic(@RequestParam("name") String name, @RequestParam("genre") String genre, @RequestParam("file") MultipartFile file) throws IOException {
        Music music = musicService.uploadMusic(new MusicUploadDto(name, genre), file);
        URI locationUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(music.getId())
                .toUri();

        return ResponseEntity.created(locationUri).build();
    }

    @GetMapping
    public ResponseEntity<?> getAllMusic(){
        List<ResponseMusicDataDto> listOfMusic = musicService.getAllMusic();
        return ResponseEntity.ok().body(listOfMusic);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMusicById(@PathVariable(name = "id") Long id){
        if (id <= 0){
            return ResponseEntity.badRequest().build();
        }

        ResponseMusicDataDto music = musicService.getMusicById(id);
        if (music == null){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().body(music);
    }

    @GetMapping(value = "/file/{id}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> getMusicFileById(@PathVariable(name = "id") Long id) throws IOException {
        if (id <= 0){
            return ResponseEntity.badRequest().build();
        }
        try{
            Resource file = musicService.getMusicFileById(id);
            MediaType mediaType = MediaTypeFactory
                    .getMediaType(file)
                    .orElse(MediaType.APPLICATION_OCTET_STREAM);
            return ResponseEntity.ok().contentType(mediaType).contentLength(file.contentLength()).body(file);
        }catch (IOException exception){
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> patchMusicMetaData(@PathVariable(name = "id") Long id, @RequestBody JsonPatch request) throws JsonPatchException, IOException {
        if (id <= 0){
            return ResponseEntity.badRequest().build();
        }
        musicService.patchMusicMetaData(id, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMusic(@PathVariable(name = "id") Long id) throws IOException {
        if (id <= 0){
            return ResponseEntity.badRequest().build();
        }
        musicService.deleteMusic(id);
        return ResponseEntity.ok().build();
    }
}
