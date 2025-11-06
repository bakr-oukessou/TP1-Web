package com.isima.tp.Controllers;

import com.isima.tp.business.RechercheService;
import com.isima.tp.models.Annonce;
import com.isima.tp.models.RechercheSauv;
import com.isima.tp.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recherches")
public class RechercheController {

    private final RechercheService rechercheService;

    @Autowired
    public RechercheController(RechercheService rechercheService) {
        this.rechercheService = rechercheService;
    }

    /**
     * Sauvegarder une nouvelle recherche
     */
    @PostMapping
    public ResponseEntity<RechercheSauv> saveSearch(@RequestBody Map<String, Object> request) {
        // Note: En production, récupérer l'utilisateur depuis le contexte de sécurité (Keycloak)
        User utilisateur = new User();
        utilisateur.setId(Long.parseLong(request.get("userId").toString()));
        
        String criteres = request.get("criteres").toString();
        RechercheSauv recherche = rechercheService.saveSearch(utilisateur, criteres);
        return ResponseEntity.status(HttpStatus.CREATED).body(recherche);
    }

    /**
     * Rechercher des annonces manuellement
     */
    @PostMapping("/search")
    public ResponseEntity<List<Annonce>> searchAnnonces(@RequestBody Map<String, String> criteres) {
        List<Annonce> annonces = rechercheService.searchAnnonces(criteres);
        return ResponseEntity.ok(annonces);
    }

    /**
     * Rechercher avec une chaîne de critères
     */
    @GetMapping("/search")
    public ResponseEntity<List<Annonce>> searchAnnoncesByString(@RequestParam String criteres) {
        List<Annonce> annonces = rechercheService.searchAnnonces(criteres);
        return ResponseEntity.ok(annonces);
    }

    /**
     * Activer/Désactiver les notifications pour une recherche
     */
    @PutMapping("/{rechercheId}/notifications")
    public ResponseEntity<Void> toggleNotifications(
            @PathVariable Long rechercheId,
            @RequestParam boolean actives) {
        rechercheService.toggleNotifications(rechercheId, actives);
        return ResponseEntity.ok().build();
    }

    /**
     * Supprimer une recherche sauvegardée
     */
    @DeleteMapping("/{rechercheId}")
    public ResponseEntity<Void> deleteSearch(@PathVariable Long rechercheId) {
        rechercheService.deleteSearch(rechercheId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Déclencher manuellement la vérification des nouvelles annonces
     */
    @PostMapping("/check-notifications")
    public ResponseEntity<String> checkNotifications() {
        rechercheService.checkForNewMatchesAndNotify();
        return ResponseEntity.ok("Vérification des notifications lancée");
    }
}
