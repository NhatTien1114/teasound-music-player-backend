package com.teasound.teasound_api.service;

import java.net.http.HttpRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.teasound.teasound_api.domain.Author;
import com.teasound.teasound_api.dto.AuthorDTO;
import com.teasound.teasound_api.repository.AuthorRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    public AuthorDTO createAuthor(Author author) {
        return new AuthorDTO(authorRepository.save(author));
    }

}
