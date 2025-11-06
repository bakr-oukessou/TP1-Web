package com.isima.tp.Repositories;

import com.isima.tp.models.RechercheSauv;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RechercherSauvRepository extends JpaRepository<RechercheSauv, Long> {

    List<RechercheSauv> findByUtilisateurId(Long utilisateurId);

    List<RechercheSauv> findByNotificationsActivesTrue();
}