package org.AlanTitor.MusicApp.Mapper;

import org.AlanTitor.MusicApp.Dto.Musics.MusicUploadDto;
import org.AlanTitor.MusicApp.Dto.Musics.ResponseMusicDataDto;
import org.AlanTitor.MusicApp.Entity.Musics.Music;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

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
}
