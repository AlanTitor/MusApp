package org.AlanTitor.MusicApp.Mapper;

import org.AlanTitor.MusicApp.Dto.Musics.MusicUploadDto;
import org.AlanTitor.MusicApp.Dto.Musics.ResponseMusicDataDto;
import org.AlanTitor.MusicApp.Entity.Musics.Music;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.web.multipart.MultipartFile;

@Mapper(componentModel = "spring")
public interface MusicMapper {
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "fileExtension", ignore = true)
    @Mapping(target = "fileName", ignore = true)
    @Mapping(target = "fileSize", ignore = true)
    @Mapping(target = "mimeType", ignore = true)
    Music toEntity(MusicUploadDto music);

    @Mapping(target = "authorName", expression = "java(music.getAuthorId().getName())")
    @Mapping(target = "id", source = "id")
    ResponseMusicDataDto toDto(Music music);

    default void setFileProperties(Music music, MultipartFile file, MusicUploadDto musicUploadDto){
        if(file != null){
            music.setFileExtension(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")));
            music.setName(musicUploadDto.getName() + music.getFileExtension());
            music.setFileName(file.getOriginalFilename());
            music.setFileSize(file.getSize() / 1024 / 1024);
            music.setMimeType(file.getContentType());
        }
    }
}
