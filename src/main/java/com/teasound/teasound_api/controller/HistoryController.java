package com.teasound.teasound_api.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import com.teasound.teasound_api.domain.Song;
import com.teasound.teasound_api.domain.User;
import com.teasound.teasound_api.dto.HistoryDTO;
import com.teasound.teasound_api.repository.SongRepository;
import com.teasound.teasound_api.repository.UserRepository;
import com.teasound.teasound_api.service.HistoryService;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;
    private final UserRepository userRepository;
    private final SongRepository songRepository;

    private String getEmailFromPrincipal(OAuth2User oAuth2Principal, UserDetails userDetails) {
        if (oAuth2Principal != null) {
            return oAuth2Principal.getAttribute("email");
        } else if (userDetails != null) {
            return userDetails.getUsername();
        }
        return null;
    }

    @GetMapping
    public ResponseEntity<List<HistoryDTO>> getHistory(
            @RequestParam(required = false) Long userId,
            @AuthenticationPrincipal OAuth2User oAuth2Principal,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long targetUserId = userId;
        if (targetUserId == null) {
            String email = getEmailFromPrincipal(oAuth2Principal, userDetails);
            if (email != null) {
                targetUserId = userRepository.findByEmail(email).map(User::getId).orElse(null);
            }
        }

        if (targetUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<HistoryDTO> historyList = historyService.getHistory(targetUserId);
        return ResponseEntity.ok(historyList);
    }

    @PostMapping()
    public ResponseEntity<?> createHistory(
            @RequestBody HistoryRequest request,
            @AuthenticationPrincipal OAuth2User oAuth2Principal,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = request.getUserId();
        if (userId == null) {
            String email = getEmailFromPrincipal(oAuth2Principal, userDetails);
            if (email != null) {
                userId = userRepository.findByEmail(email).map(User::getId).orElse(null);
            }
        }

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User authentication required");
        }

        if (request.getSongId() == null) {
            return ResponseEntity.badRequest().body("Song ID is required");
        }

        Optional<User> userOptional = userRepository.findById(userId);
        Optional<Song> songOptional = songRepository.findById(request.getSongId());

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        if (songOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Song not found");
        }

        HistoryDTO historyDTO = historyService.createHistory(songOptional.get(), userOptional.get());
        return ResponseEntity.status(HttpStatus.CREATED).body(historyDTO);
    }

    @Getter
    @Setter
    public static class HistoryRequest {
        private Long songId;
        private Long userId;
    }
}
