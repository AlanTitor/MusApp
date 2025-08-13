package org.AlanTitor.MusicApp.Service.Musics.FileStorage;

import org.AlanTitor.MusicApp.Dto.Musics.MusicUploadDto;
import org.AlanTitor.MusicApp.Entity.Musics.Music;
import org.AlanTitor.MusicApp.Entity.Users.User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

@Service
public class FileMetadataService {

    public void setFileProperties(Music music, MultipartFile file, MusicUploadDto musicUploadDto, Path path, User user){
        if(file != null){
            music.setFileExtension(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")));
            music.setName(musicUploadDto.getName() + music.getFileExtension());
            music.setFileName(file.getOriginalFilename());
            music.setFileSize(file.getSize() / 1024 / 1024);
            music.setMimeType(file.getContentType());
            music.setFilePath(path.toString());
            music.setAuthorId(user);
        }
    }
}
