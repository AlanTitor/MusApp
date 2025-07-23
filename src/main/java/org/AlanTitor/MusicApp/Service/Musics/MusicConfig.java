package org.AlanTitor.MusicApp.Service.Musics;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "spring.files")
public class MusicConfig {
    private String path;

    public String generateUniqueFileName(String musicName, String originalFileName) {
        String extension = "";
        if (originalFileName != null && originalFileName.lastIndexOf('.') > 0) {
            extension = originalFileName.substring(originalFileName.lastIndexOf('.'));
        } else {
            extension = ".mp3";
        }
        String cleanName = musicName
                .replaceAll("[<>:\"/\\\\|?*]", "") // Удаляем запрещенные в Windows символы
                .replaceAll("\\s+", " ")          // Множественные пробелы заменяем на один
                .trim();                          // Убираем пробелы в начале и конце

        if (cleanName.isEmpty()) {
            cleanName = "untitled";
        }
        long timestamp = System.currentTimeMillis();
        return cleanName + "_" + timestamp + extension;
    }
}
