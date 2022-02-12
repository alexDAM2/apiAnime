package com.example.apiAnime.domain.model.projection;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Set;
import java.util.UUID;

public interface FollowedProjection {
    UUID getUserid();
    String getUsername();

    @JsonIgnoreProperties("followedby")
    Set<UserProjection> getFollowedby();
}
