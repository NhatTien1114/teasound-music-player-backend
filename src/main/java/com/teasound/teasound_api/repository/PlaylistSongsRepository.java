package com.teasound.teasound_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.teasound.teasound_api.domain.PlaylistSongs;
import com.teasound.teasound_api.domain.PlaylistSongId;

@Repository
public interface PlaylistSongsRepository extends JpaRepository<PlaylistSongs, PlaylistSongId> {
    Optional<PlaylistSongs> findByPlaylistIdAndSongId(Long playlistId, Long songId);

    List<PlaylistSongs> findByPlaylistIdOrderByPositionAsc(Long playlistId);

    @Query("SELECT MAX(ps.position) FROM PlaylistSongs ps WHERE ps.playlist.id = :playlistId")
    Optional<Integer> findMaxPositionByPlaylistId(Long playlistId);
}
