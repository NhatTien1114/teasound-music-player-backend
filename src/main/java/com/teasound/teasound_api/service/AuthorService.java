package com.teasound.teasound_api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.teasound.teasound_api.domain.Author;
import com.teasound.teasound_api.dto.request.CreateAuthorRequest;
import com.teasound.teasound_api.dto.request.UpdateAuthorRequest;
import com.teasound.teasound_api.dto.response.ApiResponse;
import com.teasound.teasound_api.dto.response.AuthorResponse;
import com.teasound.teasound_api.mapper.AuthorMapper;
import com.teasound.teasound_api.repository.AuthorRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthorService {

    AuthorRepository authorRepository;
    AuthorMapper authorMapper;

    public ApiResponse<AuthorResponse> createAuthor(CreateAuthorRequest request) {
        Author author = authorMapper.toAuthor(request);
        Author saveAuthor = authorRepository.save(author);
        return ApiResponse.<AuthorResponse>builder()
                .code(201)
                .message("Create author successfully")
                .result(authorMapper.toAuthorResponse(saveAuthor))
                .build();
    }

    public List<AuthorResponse> getAllAuthor() {
        return authorRepository.findAll().stream()
                .map(authorMapper::toAuthorResponse)
                .collect(Collectors.toList());
    }

    public Page<AuthorResponse> getAllAuthor(int page, int limit, String search) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "id"));
        Page<Author> authorPage = authorRepository.findByNameContainingIgnoreCase(search, pageable);
        return authorPage.map(authorMapper::toAuthorResponse);
    }

    public AuthorResponse getAuthorById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found"));
        return authorMapper.toAuthorResponse(author);
    }

    public ApiResponse<AuthorResponse> updateAuthor(Long id, UpdateAuthorRequest request) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found"));
        authorMapper.updateAuthor(request, author);
        return ApiResponse.<AuthorResponse>builder()
                .code(200)
                .message("Update author successfully")
                .result(authorMapper.toAuthorResponse(authorRepository.save(author)))
                .build();
    }

}
