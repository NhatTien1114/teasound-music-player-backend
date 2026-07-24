package com.teasound.teasound_api.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.teasound.teasound_api.domain.Playlist;
import com.teasound.teasound_api.domain.Song;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistDTO {
    private Long id;
    private String name;
    private String coverImage;
    private boolean isPublic;
    private List<SongDTO> songs;
    private String addedAt;
    private Long userId;

    public PlaylistDTO(Playlist playlist) {
        if (playlist != null) {
            this.id = playlist.getId();
            this.name = playlist.getName();
            this.coverImage = playlist.getCoverImage();
            this.isPublic = playlist.isPublic();
            if (playlist.getUser() != null) {
                this.userId = playlist.getUser().getId();
            }
            try {
                if (playlist.getPlaylistSongs() != null) {
                    this.songs = playlist.getPlaylistSongs().stream()
                            .filter(ps -> ps != null && ps.getSong() != null)
                            .map(ps -> new SongDTO(ps.getSong()))
                            .collect(Collectors.toList());
                } else {
                    this.songs = java.util.Collections.emptyList();
                }
            } catch (Exception e) {
                this.songs = java.util.Collections.emptyList();
            }
        }
    }
}
