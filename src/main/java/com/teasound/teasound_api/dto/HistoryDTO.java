package com.teasound.teasound_api.dto;

import java.time.LocalDateTime;

import com.teasound.teasound_api.domain.History;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class HistoryDTO {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long songId;
    private String title;
    private String duration;
    private String thumbnailUrl;
    private String audioUrl;
    private String authorName;
    private Long userId;
    private LocalDateTime playedAt;

    public HistoryDTO(History history) {
        if (history != null) {
            this.id = history.getId();
            this.songId = history.getSong().getId();
            this.title = history.getSong().getName();
            this.duration = history.getSong().getDuration();
            this.thumbnailUrl = history.getSong().getThumbnailUrl();
            this.audioUrl = history.getSong().getAudioUrl();
            this.authorName = history.getSong().getAuthor().getName();
            this.userId = history.getUser().getId();
            this.playedAt = history.getPlayedAt();
        }
    }
}
