package com.isima.tp.Repositories;

import com.isima.tp.models.Annonce;
import com.isima.tp.models.enums.EtatObjet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AnnonceRepository extends JpaRepository<Annonce, Long>, JpaSpecificationExecutor<Annonce> {

    List<Annonce> findByEtat(EtatObjet etat);

    List<Annonce> findByZoneGeographiqueContainingIgnoreCase(String zone);

    List<Annonce> findByDonneurId(Long donneurId);

    List<Annonce> findByMotsCles_Nom(String nomMotCle);
}