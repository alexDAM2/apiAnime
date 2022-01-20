package com.example.apiAnime.repository;

import com.example.apiAnime.domain.model.Author;
import com.example.apiAnime.domain.model.projection.AuthorProjection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AuthorRepository extends JpaRepository<Author, UUID> {
    Author findByName(String name);
    <T> List<T> findBy(Class<T> type);

}