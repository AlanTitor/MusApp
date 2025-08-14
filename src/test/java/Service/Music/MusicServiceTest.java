package Service.Music;

import org.AlanTitor.MusicApp.Dto.Musics.MusicUploadDto;
import org.AlanTitor.MusicApp.Entity.Musics.Music;
import org.AlanTitor.MusicApp.Entity.Users.Role;
import org.AlanTitor.MusicApp.Entity.Users.User;
import org.AlanTitor.MusicApp.Exception.CustomExceptions.IncorrectFileData;
import org.AlanTitor.MusicApp.Mapper.MusicMapper;
import org.AlanTitor.MusicApp.Repository.MusicRepository;
import org.AlanTitor.MusicApp.Service.Musics.FileStorage.FileMetadataService;
import org.AlanTitor.MusicApp.Service.Musics.FileStorage.FileStorageServiceImpl;
import org.AlanTitor.MusicApp.Service.Musics.MusicService;
import org.AlanTitor.MusicApp.Service.Users.UserAuthorizationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class MusicServiceTest {

    @Mock
    private MusicRepository musicRepository;

    @Mock
    private UserAuthorizationServiceImpl userAuthorizationService;

    @Mock
    private FileStorageServiceImpl fileStorageService;

    @Mock
    private FileMetadataService fileMetadataService;

    @Mock
    private MusicMapper musicMapper;

    @InjectMocks
    private MusicService musicService;

    User user;
    Music music;

    @BeforeEach
    public void init(){
        user = new User(1L, "Alan", "alan@yandex.ru", "112233", Role.ADMIN, LocalDate.now(), null);
        music = new Music(1L, "burn", "rock", "linkinPark.mp3", "tracks/burn.mp3", ".mp3", 8L, "audio/mpeg", LocalDateTime.now(), LocalDateTime.now(), user);
    }

    @Test
    public void shouldUploadFileAndSaveInDb() throws IOException {
        MusicUploadDto dto = new MusicUploadDto("burn", "rock");
        MockMultipartFile file = new MockMultipartFile("burn", "linkinPark.mp3", "audio/mpeg", "bytes".getBytes());

        Mockito.when(userAuthorizationService.getCurrantUser()).thenReturn(user);
        Mockito.when(musicMapper.toEntity(dto)).thenReturn(music);
        Mockito.when(fileStorageService.isValidAudioFile(file)).thenReturn(true);
        Mockito.when(fileStorageService.store(file, "burn")).thenReturn(Path.of("tracks/burn.mp3"));

        Mockito.doAnswer(
                inv -> {
                    Music m = inv.getArgument(0);
                    Path p = inv.getArgument(3);
                    User u = inv.getArgument(4);
                    m.setFilePath(p.toString());
                    m.setAuthorId(u);
                    return null;
                }
        ).when(fileMetadataService).setFileProperties(Mockito.any(Music.class), Mockito.eq(file), Mockito.eq(dto), Mockito.any(Path.class), Mockito.eq(user));

        Mockito.when(musicRepository.save(music)).thenReturn(music);

        Music savedMusic = musicService.uploadMusic(dto, file);

        assertSame(music, savedMusic);
        InOrder inOrder = Mockito.inOrder(fileStorageService, fileMetadataService, musicRepository);
        inOrder.verify(fileStorageService).isValidAudioFile(file);
        inOrder.verify(fileStorageService).store(file, "burn");
        inOrder.verify(fileMetadataService).setFileProperties(Mockito.eq(music), Mockito.eq(file), Mockito.eq(dto), Mockito.eq(Path.of("tracks/burn.mp3")), Mockito.eq(user));
        inOrder.verify(musicRepository).save(music);
        verifyNoMoreInteractions(fileStorageService, fileMetadataService, musicRepository);
    }

    @Test
    public void shouldReturnIoException() throws IOException {
        MusicUploadDto dto = new MusicUploadDto("burn", "rock");
        MockMultipartFile file = new MockMultipartFile("burn", "linkinPark.mp3", "audio/mpeg", "bytes".getBytes());

        Mockito.when(fileStorageService.isValidAudioFile(file)).thenReturn(true);
        Mockito.when(userAuthorizationService.getCurrantUser()).thenReturn(user);
        Mockito.when(musicMapper.toEntity(dto)).thenReturn(music);
        Mockito.when(fileStorageService.store(file, "burn")).thenThrow(new IOException("Full disk"));

        IOException ex = assertThrows(IOException.class, () -> musicService.uploadMusic(dto, file));

        assertEquals("Full disk", ex.getMessage());

        Mockito.verify(fileMetadataService, Mockito.never()).setFileProperties(Mockito.any(),Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(musicRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    public void shouldReturnIncorrectFileData() throws IOException {
        MusicUploadDto dto = new MusicUploadDto("burn", "rock");
        MockMultipartFile file = new MockMultipartFile("burn", "linkinPark.mp3", "text/mpeg", "bytes".getBytes());

        Mockito.when(fileStorageService.isValidAudioFile(file)).thenReturn(false);

        assertThrows(IncorrectFileData.class, () -> musicService.uploadMusic(dto, file));

        Mockito.verify(fileStorageService, Mockito.never()).store(Mockito.any(), anyString());
        Mockito.verify(musicRepository, Mockito.never()).save(Mockito.any());
    }
}