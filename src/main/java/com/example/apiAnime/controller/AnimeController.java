package com.example.apiAnime.controller;

import com.example.apiAnime.domain.dto.AnimeError;
import com.example.apiAnime.domain.dto.ResponseList;
import com.example.apiAnime.domain.model.Anime;
import com.example.apiAnime.domain.model.projection.AnimeProjection;
import com.example.apiAnime.repository.AnimeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/animes")
public class AnimeController {

    private final AnimeRepository animeRepository;

    AnimeController(AnimeRepository animeRepository){
        this.animeRepository = animeRepository;
    }

    @GetMapping("/")
    /*public List<Anime> findAllAnimes(){
        return animeRepository.findAll();
    }*/
    public ResponseEntity<?> findAllAnimes(){
        return ResponseEntity.ok().body(animeRepository.findBy(AnimeProjection.class));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAnime(@PathVariable UUID id){
        Anime anime = animeRepository.findById(id).orElse(null);

        if(anime==null) return //ResponseEntity.notFound().build();
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(AnimeError.message("No s'ha trobat l'anime amb l'id '" + id + "'"));
        return ResponseEntity.ok().body(anime);
    }

    @PostMapping("/")
    public ResponseEntity<?> createAnime(@RequestBody Anime anime){
        if (animeRepository.findByName(anime.getName()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(AnimeError.message("Ja existeix un anime amb el nom '"+anime.name+"'"));
        }
        return ResponseEntity.ok().body(animeRepository.save(anime));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAnime(@PathVariable UUID id) {
        Anime anime = animeRepository.findById(id).orElse(null);
        if (anime != null) {
            animeRepository.delete(anime);
            return  ResponseEntity.ok().body(AnimeError.message("S'ha eliminat l'anime amd id " + "'" + id + "'"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(AnimeError.message("No s'ha trobat l'anime amb id '" + id + "'"));
    }

    @GetMapping("/{name}")
    public ResponseEntity<?> getAnimeByName(@PathVariable String name){
        List<Anime> anime = animeRepository.findByName(name);

        if(anime==null) return //ResponseEntity.notFound().build();
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(AnimeError.message("No s'ha trobat l'anime amb el nom '" + name + "'"));
        return ResponseEntity.ok().body(anime);
    }
}
