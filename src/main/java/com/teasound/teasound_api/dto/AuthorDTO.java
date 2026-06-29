package com.teasound.teasound_api.dto;

import com.teasound.teasound_api.domain.Author;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthorDTO {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String avatar;
    private String bio;

    public AuthorDTO(Author author) {
        if (author != null) {
            this.id = author.getId();
            this.name = author.getName();
            this.avatar = author.getAvatar();
            this.bio = author.getBio();
        }
    }
}
