package com.isima.tp.Repositories;

import com.isima.tp.models.MotCle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MotCleRepository extends JpaRepository<MotCle, Long> {

    // Permet d'éviter de créer des doublons de mots-clés
    Optional<MotCle> findByNom(String nom);
}
