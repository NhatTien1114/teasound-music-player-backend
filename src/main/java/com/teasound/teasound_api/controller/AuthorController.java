package com.teasound.teasound_api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.teasound.teasound_api.dto.request.CreateAuthorRequest;
import com.teasound.teasound_api.dto.request.UpdateAuthorRequest;
import com.teasound.teasound_api.dto.response.ApiResponse;
import com.teasound.teasound_api.dto.response.AuthorResponse;
import com.teasound.teasound_api.service.AuthorService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
public class AuthorController {
    private final AuthorService authorService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<AuthorResponse>> createAuthor(@RequestBody CreateAuthorRequest request) {
        ApiResponse<AuthorResponse> response = authorService.createAuthor(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping()
    public ResponseEntity<List<AuthorResponse>> getAllAuthor() {
        List<AuthorResponse> authors = authorService.getAllAuthor();
        return ResponseEntity.ok(authors);
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<AuthorResponse>> getAllAuthor(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(defaultValue = "") String search) {
        Page<AuthorResponse> authors = authorService.getAllAuthor(page, limit, search);
        return ResponseEntity.ok(authors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponse> getAuthorById(@PathVariable Long id) {
        AuthorResponse author = authorService.getAuthorById(id);
        return ResponseEntity.ok(author);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AuthorResponse>> updateAuthor(@PathVariable Long id,
            @RequestBody UpdateAuthorRequest request) {
        ApiResponse<AuthorResponse> response = authorService.updateAuthor(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
