package com.teasound.teasound_api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.teasound.teasound_api.domain.Song;
import com.teasound.teasound_api.dto.SongDTO;
import com.teasound.teasound_api.service.SongService;

@RestController
@RequestMapping("/api/songs")
class SongController {

    @Autowired
    private SongService songService;

    public SongController(SongService songService) {
        this.songService = songService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<SongDTO> createSong(@RequestBody Song song) {
        SongDTO savedSong = songService.createSong(song);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSong);
    }
}
