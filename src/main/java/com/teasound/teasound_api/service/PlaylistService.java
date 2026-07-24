package com.teasound.teasound_api.service;

import com.teasound.teasound_api.repository.PlaylistSongsRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

import com.teasound.teasound_api.domain.Playlist;
import com.teasound.teasound_api.domain.PlaylistSongs;
import com.teasound.teasound_api.domain.Song;
import com.teasound.teasound_api.dto.PlaylistDTO;
import com.teasound.teasound_api.repository.PlaylistRepository;
import com.teasound.teasound_api.repository.SongRepository;
import com.teasound.teasound_api.repository.UserRepository;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PlaylistService {

    private final PlaylistSongsRepository playlistSongsRepository;
    private final PlaylistRepository playlistRepository;
    private final SongRepository songRepository;
    private final UserRepository userRepository;

    public PlaylistService(PlaylistSongsRepository playlistSongsRepository, PlaylistRepository playlistRepository,
            SongRepository songRepository, UserRepository userRepository) {
        this.playlistSongsRepository = playlistSongsRepository;
        this.playlistRepository = playlistRepository;
        this.songRepository = songRepository;
        this.userRepository = userRepository;
    }

    public PlaylistDTO createPlaylist(PlaylistDTO playlistDTO) {
        Playlist playlist = new Playlist();
        playlist.setName(playlistDTO.getName() != null ? playlistDTO.getName() : "My Playlist");
        playlist.setCoverImage(playlistDTO.getCoverImage());
        playlist.setPublic(playlistDTO.isPublic());
        playlist.setUser(userRepository.findById(playlistDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found")));
        Playlist saved = playlistRepository.save(playlist);
        return new PlaylistDTO(saved);
    }

    public PlaylistDTO updatePlaylist(Long playlistId, PlaylistDTO playlistDTO) {
        Playlist playlist = playlistRepository.findByIdWithSongs(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));
        if (playlistDTO.getName() != null) {
            playlist.setName(playlistDTO.getName());
        }
        if (playlistDTO.getCoverImage() != null) {
            playlist.setCoverImage(playlistDTO.getCoverImage());
        }
        playlist.setPublic(playlistDTO.isPublic());
        Playlist saved = playlistRepository.save(playlist);
        return new PlaylistDTO(saved);
    }

    public PlaylistDTO getPlaylistById(Long playlistId) {
        Playlist playlist = playlistRepository.findByIdWithSongs(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));
        return new PlaylistDTO(playlist);
    }

    public void addSongToPlaylist(Long playlistId, Long songId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found"));

        boolean exists = playlistSongsRepository.findByPlaylistIdAndSongId(playlistId, songId).isPresent();
        if (exists)
            return;

        int nextPosition = playlistSongsRepository.findMaxPositionByPlaylistId(playlistId)
                .map(pos -> pos + 1)
                .orElse(0);

        PlaylistSongs playlistSongs = new PlaylistSongs();
        playlistSongs.setPlaylist(playlist);
        playlistSongs.setSong(song);
        playlistSongs.setPosition(nextPosition);
        playlistSongsRepository.save(playlistSongs);
    }

    public List<PlaylistDTO> getPlaylistsByUserId(Long userId) {
        return playlistRepository.findByUserIdWithSongs(userId).stream().map(PlaylistDTO::new)
                .collect(Collectors.toList());
    }

    public void removeSongFromPlaylist(Long playlistId, Long songId) {
        playlistSongsRepository.findByPlaylistIdAndSongId(playlistId, songId)
                .ifPresent(playlistSongsRepository::delete);
    }

    public void reorderSongs(Long playlistId, List<Long> orderedSongIds) {
        List<PlaylistSongs> items = playlistSongsRepository.findByPlaylistIdOrderByPositionAsc(playlistId);
        Map<Long, PlaylistSongs> bySongId = items.stream()
                .collect(Collectors.toMap(ps -> ps.getSong().getId(), ps -> ps));

        for (int i = 0; i < orderedSongIds.size(); i++) {
            PlaylistSongs ps = bySongId.get(orderedSongIds.get(i));
            if (ps != null) {
                ps.setPosition(i);
            }
        }
        playlistSongsRepository.saveAll(items);
    }
}
