package org.AlanTitor.MusicApp.Dto.Users;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.AlanTitor.MusicApp.Entity.Users.Role;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Response user with users list and their musics
@Getter
@Setter
public class ResponseUserDataDto implements Serializable {
    private Long id;
    private String name;
    private String email;
    private Role role;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateCreated;
    private List<ResponseListOfMusicUserDto> musics = new ArrayList<>();
}
