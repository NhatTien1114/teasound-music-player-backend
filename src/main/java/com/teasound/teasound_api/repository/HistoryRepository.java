package com.teasound.teasound_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teasound.teasound_api.domain.History;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {
    // Query (SELECT * from history WHERE user_id = ? ORDER BY played_at desc)
    List<History> findByUserIdOrderByPlayedAtDesc(Long userId);

    // Query (SELECT * FROM history WHERE user_id = ? AND song_id = ?)
    History findByUserIdAndSongId(Long userId, Long songId);
}
