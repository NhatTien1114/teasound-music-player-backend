package com.teasound.teasound_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.teasound.teasound_api.domain.Song;
import com.teasound.teasound_api.dto.SongDTO;
import com.teasound.teasound_api.repository.SongRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SongService {
    @Autowired
    private SongRepository songRepository;

    public SongDTO createSong(Song song) {
        Song savedSong = songRepository.save(song);
        return new SongDTO(savedSong);
    }

}
