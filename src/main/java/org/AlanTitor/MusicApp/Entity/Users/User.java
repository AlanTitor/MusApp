package org.AlanTitor.MusicApp.Entity.Users;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.AlanTitor.MusicApp.Entity.Musics.Music;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "date_created", updatable = false, insertable = false)
    private LocalDate dateCreated;

    @OneToMany(mappedBy = "authorId", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private Set<Music> musics = new LinkedHashSet<>();
}
