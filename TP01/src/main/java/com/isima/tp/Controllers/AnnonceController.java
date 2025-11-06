package com.isima.tp.Controllers;

import com.isima.tp.business.AnnonceService;
import com.isima.tp.models.Annonce;
import com.isima.tp.models.enums.EtatObjet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/annonces")
public class AnnonceController {

    private final AnnonceService annonceService;

    @Autowired
    public AnnonceController(AnnonceService annonceService) {
        this.annonceService = annonceService;
    }

    /**
     * Créer une nouvelle annonce
     */
    @PostMapping
    public ResponseEntity<Annonce> createAnnonce(@Valid @RequestBody Annonce annonce) {
        Annonce savedAnnonce = annonceService.saveAnnonce(annonce);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAnnonce);
    }

    /**
     * Récupérer toutes les annonces
     */
    @GetMapping
    public ResponseEntity<List<Annonce>> getAllAnnonces() {
        List<Annonce> annonces = annonceService.getAllAnnonces();
        return ResponseEntity.ok(annonces);
    }

    /**
     * Récupérer une annonce par son ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Annonce> getAnnonceById(@PathVariable Long id) {
        Optional<Annonce> annonce = annonceService.getAnnonceById(id);
        return annonce.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Rechercher des annonces par état
     */
    @GetMapping("/etat/{etat}")
    public ResponseEntity<List<Annonce>> getAnnoncesByEtat(@PathVariable EtatObjet etat) {
        List<Annonce> annonces = annonceService.getAnnoncesByEtat(etat);
        return ResponseEntity.ok(annonces);
    }

    /**
     * Rechercher des annonces par zone géographique
     */
    @GetMapping("/zone/{zone}")
    public ResponseEntity<List<Annonce>> getAnnoncesByZone(@PathVariable String zone) {
        List<Annonce> annonces = annonceService.getAnnoncesByZone(zone);
        return ResponseEntity.ok(annonces);
    }

    /**
     * Rechercher des annonces par donneur
     */
    @GetMapping("/donneur/{donneurId}")
    public ResponseEntity<List<Annonce>> getAnnoncesByDonneur(@PathVariable Long donneurId) {
        List<Annonce> annonces = annonceService.getAnnoncesByDonneur(donneurId);
        return ResponseEntity.ok(annonces);
    }

    /**
     * Rechercher des annonces par mot-clé
     */
    @GetMapping("/motcle/{motcle}")
    public ResponseEntity<List<Annonce>> getAnnoncesByMotCle(@PathVariable String motcle) {
        List<Annonce> annonces = annonceService.getAnnoncesByMotCle(motcle);
        return ResponseEntity.ok(annonces);
    }

    /**
     * Supprimer une annonce
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnnonce(@PathVariable Long id) {
        annonceService.deleteAnnonce(id);
        return ResponseEntity.noContent().build();
    }
}
