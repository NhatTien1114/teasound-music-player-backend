package com.teasound.teasound_api.dto;

import com.teasound.teasound_api.domain.Song;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SongDTO {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String description;
    private Song.Type type;
    private Long authorId;
    private String videoUrl;
    private String audioUrl;
    private String thumbnailUrl;
    private String duration;

    public SongDTO(Song song) {
        if (song != null) {
            this.name = song.getName();
            this.description = song.getDescription();
            this.type = song.getType();
            this.authorId = song.getAuthor().getId();
            this.videoUrl = song.getVideoUrl();
            this.audioUrl = song.getAudioUrl();
            this.thumbnailUrl = song.getThumbnailUrl();
            this.duration = song.getDuration();
        }
    }
}
