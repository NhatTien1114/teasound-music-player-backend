package com.teasound.teasound_api.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.teasound.teasound_api.domain.Song;
import com.teasound.teasound_api.dto.SongDTO;
import com.teasound.teasound_api.repository.SongRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SongService {
    private final SongRepository songRepository;

    public SongDTO createSong(Song song) {
        Song savedSong = songRepository.save(song);
        return new SongDTO(savedSong);
    }

    public List<SongDTO> getAllSongs() {
        List<Song> songs = songRepository.findAll();
        return songs.stream().map(SongDTO::new).collect(Collectors.toList());
    }

    public Page<SongDTO> getAllSongs(int page, int limit, String search, String type) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "id"));

        Page<Song> songPage;
        if (type != null && !type.isEmpty()) {
            try {
                Song.Type songType = Song.Type.valueOf(type.toUpperCase());
                songPage = songRepository.findByNameContainingIgnoreCaseAndType(
                        search != null ? search : "", songType, pageable);
            } catch (IllegalArgumentException e) {
                songPage = songRepository.findByNameContainingIgnoreCase(
                        search != null ? search : "", pageable);
            }
        } else {
            songPage = songRepository.findByNameContainingIgnoreCase(
                    search != null ? search : "", pageable);
        }

        return songPage.map(SongDTO::new);
    }

    public SongDTO updateSong(Song updatedSong) {
        Song existSong = songRepository.findById(updatedSong.getId()).orElse(null);
        if (existSong == null) {
            return null;
        }
        existSong.setName(updatedSong.getName());
        existSong.setDescription(updatedSong.getDescription());
        existSong.setType(updatedSong.getType());
        existSong.setAuthor(updatedSong.getAuthor());
        existSong.setVideoUrl(updatedSong.getVideoUrl());
        existSong.setAudioUrl(updatedSong.getAudioUrl());
        existSong.setThumbnailUrl(updatedSong.getThumbnailUrl());
        existSong.setDuration(updatedSong.getDuration());
        existSong.setLyric(updatedSong.getLyric());
        Song savedSong = songRepository.save(existSong);
        return new SongDTO(savedSong);
    }

    public SongDTO findSongById(Long id) {
        Song song = songRepository.findById(id).orElse(null);
        if (song == null) {
            return null;
        }
        return new SongDTO(song);
    }

}
