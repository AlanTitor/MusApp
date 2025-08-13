package org.AlanTitor.MusicApp.Service.Musics.FileStorage;

import lombok.AllArgsConstructor;
import org.AlanTitor.MusicApp.Service.Musics.MusicConfig;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;

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
        Path base = Path.of(musicConfig.getPath());
        Files.createDirectory(base);

        String extensions = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));

        Path target = base.resolve(fileName + extensions);
        file.transferTo(target);
        return target;
    }

    @Override
    public void delete(MultipartFile file) {

    }

    @Override
    public void rename(String filePath, String oldName, String newName) throws IOException {
        Files.move(Path.of(filePath, oldName), Path.of(filePath, newName));
    }

    @Override
    public Resource loadAsResource(String filePath, String fileName) throws IOException {
        Path path = Path.of(filePath);
        Resource resource = new UrlResource(path.toUri());

        if(!resource.exists() || !resource.isReadable()){
            throw new IOException("Can't read file!");
        }
        return resource;
    }
}
