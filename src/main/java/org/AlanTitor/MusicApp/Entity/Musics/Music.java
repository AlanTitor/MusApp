package org.AlanTitor.MusicApp.Entity.Musics;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.AlanTitor.MusicApp.Entity.Users.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@ToString
@Table(name = "musics")
public class Music {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "genre")
    private String genre;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "path")
    private String filePath;

    @Column(name = "extension")
    private String fileExtension;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "mime_type")
    private String mimeType;

    @Column(name = "date_created", updatable = false, insertable = false)
    private LocalDateTime dateCreated;

    @Column(name = "date_changed", columnDefinition = "TIMESTAMP DEFAULT NOW()")
    private LocalDateTime dateChanged;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User authorId;
}
