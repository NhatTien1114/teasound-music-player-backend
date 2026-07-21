package com.teasound.teasound_api.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.teasound.teasound_api.domain.History;
import com.teasound.teasound_api.domain.Song;
import com.teasound.teasound_api.domain.User;
import com.teasound.teasound_api.dto.HistoryDTO;
import com.teasound.teasound_api.repository.HistoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final HistoryRepository historyRepository;

    public List<HistoryDTO> getHistory(Long userId) {
        return historyRepository.findByUserIdOrderByPlayedAtDesc(userId)
                .stream()
                .map(HistoryDTO::new)
                .toList();
    }

    public HistoryDTO createHistory(Song song, User user) {
        History history = new History();
        // if history of user & song already exists, update playedAt
        History existingHistory = historyRepository.findByUserIdAndSongId(user.getId(), song.getId());
        if (existingHistory != null) {
            existingHistory.setPlayedAt(LocalDateTime.now());
            return new HistoryDTO(historyRepository.save(existingHistory));
        }
        history.setSong(song);
        history.setUser(user);
        history.setPlayedAt(LocalDateTime.now());
        return new HistoryDTO(historyRepository.save(history));
    }
}
