package Service.Music;

import org.AlanTitor.MusicApp.Dto.Users.LoginUserResponse;
import org.AlanTitor.MusicApp.Entity.Users.Role;
import org.AlanTitor.MusicApp.Entity.Users.User;
import org.AlanTitor.MusicApp.Jwt.Jwt;
import org.AlanTitor.MusicApp.Jwt.JwtService;
import org.AlanTitor.MusicApp.Main;
import org.AlanTitor.MusicApp.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

import java.io.IOException;
import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
public class ConcurrencyUploadFileTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    UserRepository userRepository;
    @Autowired
    JwtService jwtService;

    User testUser;

    @BeforeEach
    public void init() throws IOException {
        testUser = new User(1L, "AlanTest", "alanTest@yandex.ru", "112233", Role.ADMIN, LocalDate.now(), null);
        userRepository.save(testUser);
    }

    @Test
    public void shouldUpdateFileSuccessfully() throws Exception {
        Jwt jwtAccess = jwtService.generateAccessToken(testUser);
        Jwt jwtRefresh = jwtService.generateRefreshToken(testUser);
        LoginUserResponse responseToken = new LoginUserResponse(jwtAccess, jwtRefresh);


        int threads = 200;
        ExecutorService exec = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);

        for(int i = 0; i < threads; i++){
            exec.submit(() ->{
                try{
                    MockMultipartFile file = new MockMultipartFile("file", "linkinPark.mp3", "audio/mpeg", "bytes".getBytes());
                    MockMultipartHttpServletRequestBuilder builder = multipart("/api/musics");
                    builder.file(file)
                            .param("name", "burn-" + Thread.currentThread().getName())
                            .param("genre", "rock")
                            .header("Authorization", "Bearer " + responseToken.getAccessToken());

                    MvcResult response = mvc.perform(builder)
                            .andExpect(status().isCreated())
                            .andReturn();
                }catch (Exception ignored){

                }finally {
                    latch.countDown();
                }
            });
        }

        boolean completed = latch.await(30, TimeUnit.SECONDS);
        if(!completed) {
            System.out.println("ERROR LATCH!");
        }

        if(!exec.awaitTermination(10, TimeUnit.SECONDS)){
            exec.shutdown();
        }
    }
}