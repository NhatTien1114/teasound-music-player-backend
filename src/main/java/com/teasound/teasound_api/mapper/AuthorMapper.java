package com.teasound.teasound_api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.teasound.teasound_api.domain.Author;
import com.teasound.teasound_api.dto.request.CreateAuthorRequest;
import com.teasound.teasound_api.dto.request.UpdateAuthorRequest;
import com.teasound.teasound_api.dto.response.AuthorResponse;

@Mapper(componentModel = "spring")
public interface AuthorMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "followerCount", ignore = true)
    Author toAuthor(CreateAuthorRequest request);

    void updateAuthor(UpdateAuthorRequest request, @MappingTarget Author author);

    AuthorResponse toAuthorResponse(Author author);

}
