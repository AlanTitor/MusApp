package org.AlanTitor.MusicApp.Service.Musics.FileStorage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;

public interface FileStorageService {
    boolean isValidAudioFile(MultipartFile file);
    Path store(MultipartFile file, String fileName) throws IOException;
    void delete(MultipartFile file);
    void rename(String filePath, String oldName, String newName) throws IOException;
    Resource loadAsResource(String filePath, String fileName) throws IOException;
}
