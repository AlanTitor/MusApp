package org.AlanTitor.MusicApp.Service.Musics;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Getter
@Setter
@RequiredArgsConstructor
public class MusicFile {

    private final MultipartFile file;
    private final String pathWhereSaveFile;

    private String fileName;

    public boolean isValidAudioFile(){
        String contentType = this.file.getContentType();
        return contentType != null && contentType.startsWith("audio/");
    }

    public Path moveFile() throws IOException {
        Path projectRoot = Paths.get("").toAbsolutePath();
        Path uploadPath = projectRoot.resolve(pathWhereSaveFile);

        if(!Files.exists(uploadPath)){
            Files.createDirectory(uploadPath);
        }

        File pathToTransfer = new File(uploadPath.toString(), this.fileName + ".mp3");
        file.transferTo(pathToTransfer.toPath());

        return uploadPath;
    }

    public String getOriginalFilename() {
        return file.getOriginalFilename();
    }
}
