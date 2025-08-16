package org.AlanTitor.MusicApp.Service.Musics.FileStorage;

import lombok.AllArgsConstructor;
import org.AlanTitor.MusicApp.Entity.Musics.Music;
import org.AlanTitor.MusicApp.Exception.CustomExceptions.IncorrectFileData;
import org.AlanTitor.MusicApp.Exception.CustomExceptions.MusicNotFoundException;
import org.AlanTitor.MusicApp.Service.Musics.MusicConfig;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;

@AllArgsConstructor
@Service
public class FileStorageServiceImpl implements FileStorageService{
    private MusicConfig musicConfig;

    @Override
    public boolean isValidAudioFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("audio/");
    }

    // create and move file
    @Override
    public Path store(MultipartFile file, String fileName) throws IOException {
        if (file == null || file.isEmpty() || !isValidAudioFile(file)) throw new IncorrectFileData();

        Path base = Path.of(musicConfig.getPath());
        if(!Files.exists(base)){
            Files.createDirectories(base);
        }

        String extensions = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));

        Path target = base.resolve(fileName + extensions);

        if (Files.exists(Path.of(musicConfig.getPath(), fileName + extensions))) throw new FileAlreadyExistsException("File with name " + fileName + " already exits!");

        file.transferTo(target);
        return base;
    }

    @Override
    public void delete(Music music) throws IOException, MusicNotFoundException {
        File musicFile = new File(music.getFilePath(), music.getName());
        Files.deleteIfExists(musicFile.toPath());
    }

    // Only move(); method works here to rename file
    @Override
    public void rename(String filePath, String oldName, String newName) throws IOException {
        Files.move(Path.of(filePath, oldName), Path.of(filePath, newName), ATOMIC_MOVE);
    }

    @Override
    public Resource loadAsResource(String filePath, String fileName) throws IOException {
        Path path = Path.of(filePath, fileName);
        Resource resource = new UrlResource(path.toUri());

        if(!resource.exists() || !resource.isReadable()){
            throw new IOException("Can't read file!");
        }
        return resource;
    }
}
