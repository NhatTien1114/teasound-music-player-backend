package com.teasound.teasound_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.teasound.teasound_api.domain.Author;
import com.teasound.teasound_api.dto.AuthorDTO;
import com.teasound.teasound_api.repository.AuthorRepository;

import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    public AuthorDTO createAuthor(Author author) {
        return new AuthorDTO(authorRepository.save(author));
    }

    public List<AuthorDTO> getAllAuthor() {
        return authorRepository.findAll().stream().map(AuthorDTO::new).collect(Collectors.toList());
    }

}
