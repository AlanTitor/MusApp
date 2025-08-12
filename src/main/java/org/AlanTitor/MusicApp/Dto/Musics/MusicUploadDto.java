package org.AlanTitor.MusicApp.Dto.Musics;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class MusicUploadDto implements Serializable {

    public MusicUploadDto(String name, String genre) {
        this.name = name;
        this.genre = genre;
    }

    @NotEmpty(message = "Name must be completed!")
    @Size(max = 50)
    private String name;

    @NotEmpty(message = "Genre must be completed!")
    @Size(max = 50)
    private String genre;
}
