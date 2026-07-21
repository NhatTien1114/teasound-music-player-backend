package com.teasound.teasound_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teasound.teasound_api.domain.History;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {
    List<History> findByUserIdOrderByPlayedAtDesc(Long userId);

    History findByUserIdAndSongId(Long userId, Long songId);
}
