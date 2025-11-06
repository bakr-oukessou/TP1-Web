package com.isima.tp.business;

import com.isima.tp.Repositories.AnnonceRepository;
import com.isima.tp.Repositories.RechercherSauvRepository;
import com.isima.tp.models.RechercheSauv;
import com.isima.tp.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RechercheService {

    private final RechercherSauvRepository rechercheRepository;
    private final AnnonceRepository annonceRepository;

    @Autowired
    public RechercheService(RechercherSauvRepository rechercheRepository, AnnonceRepository annonceRepository) {
        this.rechercheRepository = rechercheRepository;
        this.annonceRepository = annonceRepository;
    }
    @Transactional
    public RechercheSauv saveSearch(User utilisateur, String criteres) {
        RechercheSauv recherche = new RechercheSauv();
        recherche.setUtilisateur(utilisateur);
        recherche.setCriteres(criteres);
        recherche.setNotificationsActives(true);
        return rechercheRepository.save(recherche);
    }

    /**
     * Logique de notification : Vérifie les nouvelles annonces par rapport aux recherches sauvegardées.
     * Cette méthode sera probablement appelée par une tâche planifiée (@Scheduled)
     */
    // Méthode simple et illustrative (la logique réelle serait plus complexe)
    public void checkForNewMatchesAndNotify() {
        List<RechercheSauv> recherchesActives = rechercheRepository.findByNotificationsActivesTrue();

        for (RechercheSauv recherche : recherchesActives) {
            // Logique à implémenter :
            // 1. Extraire les critères de recherche (zone, mot-clé, etc.) du String 'criteres'.
            // 2. Chercher dans la BDD les Annonces correspondant à ces critères (ex: via AnnonceRepository customisée ou Specification).
            // 3. Si de nouvelles annonces sont trouvées :
            //    - Envoyer une notification à recherche.getUtilisateur().
            //    - Marquer l'annonce comme "notifiée" pour ne pas la renvoyer.
        }
    }
}