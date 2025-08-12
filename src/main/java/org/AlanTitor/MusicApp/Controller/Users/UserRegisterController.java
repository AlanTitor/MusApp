package org.AlanTitor.MusicApp.Controller.Users;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.AlanTitor.MusicApp.Dto.Users.RegisterUserRequest;
import org.AlanTitor.MusicApp.Dto.Users.RegisterUserResponse;
import org.AlanTitor.MusicApp.Dto.Users.ResponseUserDataDto;
import org.AlanTitor.MusicApp.Service.Users.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserRegisterController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterUserRequest request, UriComponentsBuilder uriBuilder){
        RegisterUserResponse response = userService.registerUser(request);
        URI uri = uriBuilder.path("api/user/register/{id}").buildAndExpand(response.getId()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers(){
        List<ResponseUserDataDto> listOfUserWithMusic = userService.getAllUsers();
        return ResponseEntity.ok().body(listOfUserWithMusic);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable(name = "id") Long id){
        if (id <= 0){
            return ResponseEntity.badRequest().build();
        }

        ResponseUserDataDto user = userService.getUserById(id);
        if (user == null){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().body(user);
    }
}
