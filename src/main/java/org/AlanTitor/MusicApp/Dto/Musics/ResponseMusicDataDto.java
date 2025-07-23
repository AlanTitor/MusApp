package org.AlanTitor.MusicApp.Dto.Musics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class ResponseMusicDataDto implements Serializable {
    private Long id;
    private String name;
    private String genre;
    private LocalDate dateCreated;
    private String authorName;
}
