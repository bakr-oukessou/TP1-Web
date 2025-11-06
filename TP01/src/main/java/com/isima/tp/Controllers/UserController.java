package com.isima.tp.Controllers;

import com.isima.tp.business.UserService;
import com.isima.tp.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Créer un nouvel utilisateur
     */
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        User savedUser = userService.registerNewUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    /**
     * Récupérer un utilisateur par son ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.findById(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Récupérer un utilisateur par email
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        Optional<User> user = userService.findByEmail(email);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Ajouter une annonce aux favoris
     */
    @PostMapping("/{userId}/favoris/{annonceId}")
    public ResponseEntity<Void> addFavorite(@PathVariable Long userId, @PathVariable Long annonceId) {
        boolean added = userService.addFavorite(userId, annonceId);
        return added ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    /**
     * Retirer une annonce des favoris
     */
    @DeleteMapping("/{userId}/favoris/{annonceId}")
    public ResponseEntity<Void> removeFavorite(@PathVariable Long userId, @PathVariable Long annonceId) {
        boolean removed = userService.removeFavorite(userId, annonceId);
        return removed ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
