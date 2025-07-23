package org.AlanTitor.MusicApp.Controller.Musics;

import jakarta.validation.ConstraintViolationException;
import lombok.AllArgsConstructor;
import org.AlanTitor.MusicApp.Dto.Musics.MusicUploadDto;
import org.AlanTitor.MusicApp.Dto.Musics.ResponseMusicDataDto;
import org.AlanTitor.MusicApp.Entity.Musics.Music;
import org.AlanTitor.MusicApp.Service.Musics.MusicService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@AllArgsConstructor
@RestController
@RequestMapping("/api/musics")
public class MusicUploadController {

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
            return ResponseEntity.notFound().build();
        }

        ResponseMusicDataDto music = musicService.getMusicById(id);
        if (music == null){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().body(music);
    }

    // if file isn't presented in request param
    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<Map<String, String>> handleNotFoundException(){
        return ResponseEntity.badRequest().body(Map.of("Error", "File is not present!"));
    }
    // if name or genre isn't presented in request param
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(ConstraintViolationException exception){
        Map<String, String> errors = new HashMap<>();
        exception.getConstraintViolations().forEach(error -> errors.put("Message:", error.getMessage()));
        return ResponseEntity.badRequest().body(errors);
    }
}
