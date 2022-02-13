package com.example.apiAnime.controller;

import com.example.apiAnime.domain.dto.AnimeError;
import com.example.apiAnime.domain.dto.RequestAnimeByName;
import com.example.apiAnime.domain.model.Anime;
import com.example.apiAnime.domain.model.Comment;
import com.example.apiAnime.domain.model.projection.AnimeProjection;
import com.example.apiAnime.domain.model.projection.CommentsAnimeProjection;
import com.example.apiAnime.repository.AnimeRepository;
import com.example.apiAnime.repository.CommentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/animes")
public class AnimeController {

    private final AnimeRepository animeRepository;
    private final CommentRepository commentRepository;

    AnimeController(AnimeRepository animeRepository, CommentRepository commentRepository){
        this.animeRepository = animeRepository;
        this.commentRepository = commentRepository;
    }

    @GetMapping("/")
    /*public List<Anime> findAllAnimes(){
        return animeRepository.findAll();
    }*/
    public ResponseEntity<?> findAllAnimes(){
        return ResponseEntity.ok().body(animeRepository.findBy(AnimeProjection.class));
    }

    @GetMapping("/{id}")
    public Object getAnime(@PathVariable UUID id){
        return ResponseEntity.ok().body(animeRepository.findByAnimeid(id, AnimeProjection.class));
    }

    @PostMapping("/")
    public ResponseEntity<?> createAnime(@RequestBody Anime anime){
        if (animeRepository.findByName(anime.name) != null) {
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

    @GetMapping("/name")
    public Object getAnimeByName(@RequestBody RequestAnimeByName requestAnimeByName) {
        return ResponseEntity.ok().body(animeRepository.findByName(requestAnimeByName.name, AnimeProjection.class));
    }

    @GetMapping("/comments/{id}")
    public ResponseEntity<?> getComments(@PathVariable UUID id){
        List<CommentsAnimeProjection> anime = animeRepository.findByAnimeid(id, CommentsAnimeProjection.class);
        if (anime != null) {
            return ResponseEntity.ok().body(anime);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(AnimeError.message("No s'ha trobat l'anime amb id'" + id + "'"));
    }

    @PostMapping("/comment")
    public ResponseEntity doComment(@RequestBody Comment comment){
        Comment commentPost = new Comment();
        commentPost.userid = comment.userid;
        commentPost.animeid = comment.animeid;
        commentPost.comment = comment.comment;
        commentRepository.save(commentPost);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/comment/{animeid}/{userid}")
    public ResponseEntity<?> deleteComment(@PathVariable UUID animeid, @PathVariable UUID userid) {
        List<Comment> comments = commentRepository.findAll();
        for (Comment comment : comments) {
            if (comment.animeid.equals(animeid) && comment.userid.equals(userid)) {
                commentRepository.delete(comment);
                return ResponseEntity.ok().body("S'ha eliminat exitosament");
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(AnimeError.message("No s'ha pogut eliminar"));
    }
}
