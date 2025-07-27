import org.AlanTitor.MusicApp.Entity.Users.Role;
import org.AlanTitor.MusicApp.Entity.Users.User;
import org.AlanTitor.MusicApp.Jwt.Jwt;
import org.AlanTitor.MusicApp.Jwt.JwtConfig;
import org.AlanTitor.MusicApp.Jwt.JwtService;
import org.AlanTitor.MusicApp.Repository.UserRepository;
import org.AlanTitor.MusicApp.Service.Users.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Test
    public void generateAccessJwtToken_ShouldContainSubRoleExp(){
        JwtConfig config = new JwtConfig();
        config.setKey("0123456789ABCDEF0123456789ABCDEF");
        config.setAccessTokenExpiration(3600);
        config.setRefreshTokenExpiration(5600);

        JwtService jwtService = new JwtService(config, userRepository);

        User user = new User();
        user.setId(2L);
        user.setEmail("alan@yandex.ru");
        user.setName("alan");
        user.setRole(Role.ADMIN);

        var jwtAccessToken = jwtService.generateAccessToken(user);

        Jwt jwt = jwtService.parseToken(String.valueOf(jwtAccessToken));

        assertEquals(jwt.getUserId(), 2L);
        assertEquals(jwt.getUserRole(), Role.ADMIN);
    }
}
