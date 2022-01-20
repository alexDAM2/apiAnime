package com.example.apiAnime.controller;

import com.example.apiAnime.domain.dto.AnimeError;
import com.example.apiAnime.domain.dto.RequestFavourite;
import com.example.apiAnime.domain.dto.UserRegisterRequest;
import com.example.apiAnime.domain.model.Anime;
import com.example.apiAnime.domain.model.Favourite;
import com.example.apiAnime.domain.model.User;
import com.example.apiAnime.domain.model.projection.UserFavouritesProjection;
import com.example.apiAnime.domain.model.projection.UserProjection;
import com.example.apiAnime.repository.AnimeRepository;
import com.example.apiAnime.repository.FavouriteRepository;
import com.example.apiAnime.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired private UserRepository userRepository;
    @Autowired private AnimeRepository animeRepository;
    @Autowired private FavouriteRepository favouriteRepository;

    @Autowired private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/")
    public List<UserProjection> findAllUsers(){
        return userRepository.findBy();
    }



    @PostMapping("/")
    public ResponseEntity<?> register(@RequestBody UserRegisterRequest userRegisterRequest) {

        if (userRepository.findByUsername(userRegisterRequest.username) == null) {
            User user = new User();
            user.username = userRegisterRequest.username;
            user.password = passwordEncoder.encode(userRegisterRequest.password);
            user.enabled = true;
            userRepository.save(user);
            return ResponseEntity.ok().body(userRepository.findByUsername(user.username, UserProjection.class)); //M'ha semblat la millor forma d'evitar filtrar dades
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(AnimeError.message("Ja existeix un usuari amb el nom '" + userRegisterRequest.username + "'"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id){
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            userRepository.delete(user);
            return  ResponseEntity.ok().body(AnimeError.message("S'ha eliminat l'usuari amd id " + "'" + id + "'"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(AnimeError.message("No s'ha trobat l'usuari amb id '" + id + "'"));
    }

    @DeleteMapping("/")
    public ResponseEntity<?> deleteAllUsers(){
        userRepository.deleteAll();
        return ResponseEntity.ok().body(AnimeError.message("S'han esborrat tots els usuaris"));
    }

    // WEB REGISTER FORM (for testing)
    @GetMapping("/register/web")
    public String hack(){
        return "<div style='display:flex;flex-direction:column;width:20em;gap:0.5em'>" +
                "<input name='username' id='username' placeholder='Username'>" +
                "<input id='password' type='password' placeholder='Password'>" +
                "<input type='button' value='Register' onclick='fetch(\"/users/register/\",{method:\"POST\",headers:{\"Content-Type\":\"application/json\"},body:`{\"username\":\"${username.value}\",\"password\":\"${password.value}\"}`})'></div>";
    }

    @GetMapping("/favourites")
    public ResponseEntity<?> getFavourites(Authentication authentication) {


        if (authentication != null) {
            User authenticatedUser = userRepository.findByUsername(authentication.getName());

            if (authenticatedUser != null) {
                return ResponseEntity.ok().body(userRepository.findByUsername(authentication.getName(), UserFavouritesProjection.class));
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(AnimeError.message("No autorizado"));
    }

    @PostMapping("/favorites")
    public ResponseEntity<?> addFavorite(@RequestBody RequestFavourite requestFavorite, Authentication authentication) {
        if (authentication != null) {
            User authenticatedUser = userRepository.findByUsername(authentication.getName());

            if (authenticatedUser != null) {
                Favourite favorite = new Favourite();
                favorite.animeid = requestFavorite.animeid;
                favorite.userid = authenticatedUser.userid;
                favouriteRepository.save(favorite);
                return ResponseEntity.ok().build();
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(AnimeError.message("No autorizado"));
    }



}
