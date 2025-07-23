package org.AlanTitor.MusicApp.Repository;

import org.AlanTitor.MusicApp.Entity.Musics.Music;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MusicRepository extends CrudRepository<Music, Long> {
    @EntityGraph(attributePaths = "authorId")
    @Query("SELECT m FROM Music m")
    List<Music> getAllMusic();
}
