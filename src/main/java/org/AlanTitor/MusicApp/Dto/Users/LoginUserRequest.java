package org.AlanTitor.MusicApp.Dto.Users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginUserRequest {
    @NotBlank(message = "Email can't be blank.")
    @Email(message = "Email must be valid.")
    private String email;

    @NotBlank(message = "Password can't be blank.")
    @Size(min = 5, max = 20, message = "Password must be less 20 characters and more than 5.")
    private String password;
}
