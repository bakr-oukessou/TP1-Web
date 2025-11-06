package com.isima.tp.Repositories;

import com.isima.tp.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Méthode essentielle pour la sécurité et la connexion
    Optional<User> findByEmail(String email);

    // Trouver un utilisateur par son pseudo
    Optional<User> findByPseudo(String pseudo);
}