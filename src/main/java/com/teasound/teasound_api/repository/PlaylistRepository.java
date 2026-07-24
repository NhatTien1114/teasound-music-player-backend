package com.teasound.teasound_api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.teasound.teasound_api.domain.Playlist;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    Page<Playlist> findByUserId(Long userId, Pageable page);

    @Query("SELECT DISTINCT p FROM Playlist p LEFT JOIN FETCH p.playlistSongs ps LEFT JOIN FETCH ps.song WHERE p.user.id = :userId")
    List<Playlist> findByUserIdWithSongs(Long userId);

    @Query("SELECT DISTINCT p FROM Playlist p LEFT JOIN FETCH p.playlistSongs ps LEFT JOIN FETCH ps.song WHERE p.id = :playlistId")
    Optional<Playlist> findByIdWithSongs(Long playlistId);
}
