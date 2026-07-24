package com.teasound.teasound_api.controller;

import java.util.List;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.teasound.teasound_api.dto.PlaylistDTO;
import com.teasound.teasound_api.service.PlaylistService;

@RestController
@RequestMapping("/api/playlists")
public class PlaylistController {
    private final PlaylistService playlistService;

    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    @PostMapping("/create")
    public ResponseEntity<PlaylistDTO> createPlaylist(@RequestBody PlaylistDTO playlistDTO) {
        return ResponseEntity.ok(playlistService.createPlaylist(playlistDTO));
    }

    @PutMapping("/{playlistId}")
    public ResponseEntity<PlaylistDTO> updatePlaylist(@PathVariable Long playlistId, @RequestBody PlaylistDTO playlistDTO) {
        return ResponseEntity.ok(playlistService.updatePlaylist(playlistId, playlistDTO));
    }

    @GetMapping("/detail/{playlistId}")
    public ResponseEntity<PlaylistDTO> getPlaylistById(@PathVariable Long playlistId) {
        return ResponseEntity.ok(playlistService.getPlaylistById(playlistId));
    }

    @PostMapping("/{playlistId}/songs/{songId}")
    public ResponseEntity<Void> addSong(@PathVariable Long playlistId, @PathVariable Long songId) {
        playlistService.addSongToPlaylist(playlistId, songId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{playlistId}/songs/{songId}")
    public ResponseEntity<Void> removeSong(@PathVariable Long playlistId, @PathVariable Long songId) {
        playlistService.removeSongFromPlaylist(playlistId, songId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{playlistId}/reorder")
    public ResponseEntity<Void> reorder(@PathVariable Long playlistId, @RequestBody List<Long> songIds) {
        playlistService.reorderSongs(playlistId, songIds);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<PlaylistDTO>> getPlaylistsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(playlistService.getPlaylistsByUserId(userId));
    }
}
