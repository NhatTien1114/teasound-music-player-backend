package com.teasound.teasound_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teasound.teasound_api.domain.Author;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

}
