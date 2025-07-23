package org.AlanTitor.MusicApp.Dto.Users;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

// list of musics to ResponseUserDataDto class
@Getter
@Setter
public class ResponseListOfMusicUserDto implements Serializable{
    private Long id;
    private String name;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateCreated;
}
