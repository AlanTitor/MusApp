package org.AlanTitor.MusicApp.Repository;

import org.AlanTitor.MusicApp.Entity.Users.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);

    @EntityGraph(attributePaths = "musics")
    @Query("SELECT u FROM User u")
    List<User> getAllUsersWithMusic();

    @EntityGraph(attributePaths = "musics")
    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> getOneUsersWithMusicById(@Param("id") Long id);
}
