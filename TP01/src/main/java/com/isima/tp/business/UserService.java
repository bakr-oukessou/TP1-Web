package com.isima.tp.business;

import com.isima.tp.Repositories.AnnonceRepository;
import com.isima.tp.Repositories.UserRepository;
import com.isima.tp.models.Annonce;
import com.isima.tp.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository utilisateurRepository;
    private final AnnonceRepository annonceRepository;

    @Autowired
    public UserService(UserRepository utilisateurRepository, AnnonceRepository annonceRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.annonceRepository = annonceRepository;
    }

    @Transactional
    public User registerNewUser(User utilisateur) {
        // Authentication is handled by Keycloak
        return utilisateurRepository.save(utilisateur);
    }

    public Optional<User> findById(Long id) {
        return utilisateurRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return utilisateurRepository.findByEmail(email);
    }

    /**
     * Ajoute une annonce aux favoris de l'utilisateur.
     */
    @Transactional
    public boolean addFavorite(Long userId, Long annonceId) {
        User user = utilisateurRepository.findById(userId).orElse(null);
        Annonce annonce = annonceRepository.findById(annonceId).orElse(null);

        if (user != null && annonce != null) {
            user.getFavoris().add(annonce);
            utilisateurRepository.save(user); // Sauvegarde la relation ManyToMany
            return true;
        }
        return false;
    }

    /**
     * Supprime une annonce des favoris de l'utilisateur.
     */
    @Transactional
    public boolean removeFavorite(Long userId, Long annonceId) {
        User user = utilisateurRepository.findById(userId).orElse(null);
        Annonce annonce = annonceRepository.findById(annonceId).orElse(null);

        if (user != null && annonce != null) {
            boolean removed = user.getFavoris().remove(annonce);
            utilisateurRepository.save(user);
            return removed;
        }
        return false;
    }
}