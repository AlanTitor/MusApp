import org.AlanTitor.MusicApp.Controller.Users.UserRegisterController;
import org.AlanTitor.MusicApp.Dto.Users.RegisterUserRequest;
import org.AlanTitor.MusicApp.Dto.Users.RegisterUserResponse;
import org.AlanTitor.MusicApp.Exception.UserDuplicateException;
import org.AlanTitor.MusicApp.Service.Users.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserRegisterControllerTest {

    private RegisterUserRequest request;
    private RegisterUserResponse response;
    private UriComponentsBuilder uri;

    @BeforeEach
    public void init(){
        request = new RegisterUserRequest("Alan", "alan@yandex.ru", "123456");
        response = new RegisterUserResponse(2L, "alan@yandex.ru", "123456");
        uri = UriComponentsBuilder.fromUriString("http://localhost/users");
    }

    @Mock
    private UserService userService;

    @InjectMocks
    private UserRegisterController controller;

    @Test
    public void createUserThanReturnCreated(){
        when(userService.registerUser(request)).thenReturn(response);

        var finalResponse = controller.registerUser(request, uri);

        assertEquals(HttpStatus.CREATED, finalResponse.getStatusCode());
        assertTrue(finalResponse.getHeaders().getLocation().toString().contains("http://localhost/usersapi/user/register/2"));
    }

//    @Test
//    public void createUserWithErrorThanReturnConflict(){
//        doThrow(new UserDuplicateException()).when(userService).registerUser(request);
//
//        var response = controller.registerUser(request, uri);
//
//        assertEquals(HttpStatus.CONFLICT.value(), response.getStatusCode().value());
//        assertEquals(Map.of("Error", "Incorrect data!"), response.getBody());
//
//        assertThrows(UserDuplicateException.class, () -> userService.registerUser(request));
//    }
}
