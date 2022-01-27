package com.example.apiAnime.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name="animeuser")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public UUID userid;

    public String username;
    //@JsonIgnore
    public String password;
    public String role;
    public boolean enabled;

    @ManyToMany(mappedBy = "favoritedby")
    public Set<Anime> favorites;

    @ManyToMany
    @JoinTable(name = "usertouser", joinColumns = @JoinColumn(name ="followerid"), inverseJoinColumns = @JoinColumn(name = "followedid"))
    public Set<User> followedby;

    @ManyToMany(mappedBy = "followedby")
    public Set<User> followers;

}
