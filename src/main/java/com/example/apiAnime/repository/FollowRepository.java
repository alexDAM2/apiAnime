package com.example.apiAnime.repository;

import com.example.apiAnime.domain.model.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FollowRepository extends JpaRepository<Follow, UUID> {

    <T> List<T> findByUsername(String username, Class<T> type);
}
