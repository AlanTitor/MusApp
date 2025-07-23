package org.AlanTitor.MusicApp.Dto.Users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RegisterUserResponse {
    private Long id;
    private String name;
    private String email;

}
