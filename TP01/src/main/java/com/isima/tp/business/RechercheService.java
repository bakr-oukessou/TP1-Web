package com.isima.tp.business;

import com.isima.tp.Repositories.AnnonceRepository;
import com.isima.tp.Repositories.RechercherSauvRepository;
import com.isima.tp.models.Annonce;
import com.isima.tp.models.RechercheSauv;
import com.isima.tp.models.User;
import com.isima.tp.models.enums.EtatObjet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RechercheService {

    private static final Logger logger = LoggerFactory.getLogger(RechercheService.class);
    
    private final RechercherSauvRepository rechercheRepository;
    private final AnnonceRepository annonceRepository;
    
    // Store the last check time for each search to avoid duplicate notifications
    private final Map<Long, LocalDateTime> lastCheckTimes = new HashMap<>();

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
     * Récupère toutes les recherches sauvegardées d'un utilisateur
     */
    public List<RechercheSauv> getUserSearches(User utilisateur) {
        return rechercheRepository.findAll().stream()
                .filter(r -> r.getUtilisateur().getId().equals(utilisateur.getId()))
                .collect(Collectors.toList());
    }
    
    /**
     * Active ou désactive les notifications pour une recherche
     */
    @Transactional
    public void toggleNotifications(Long rechercheId, boolean actives) {
        RechercheSauv recherche = rechercheRepository.findById(rechercheId)
                .orElseThrow(() -> new IllegalArgumentException("Recherche non trouvée"));
        recherche.setNotificationsActives(actives);
        rechercheRepository.save(recherche);
    }
    
    /**
     * Supprime une recherche sauvegardée
     */
    @Transactional
    public void deleteSearch(Long rechercheId) {
        rechercheRepository.deleteById(rechercheId);
        lastCheckTimes.remove(rechercheId);
    }

    /**
     * Logique de notification : Vérifie les nouvelles annonces par rapport aux recherches sauvegardées.
     * Cette méthode est appelée automatiquement toutes les heures par une tâche planifiée.
     * Format des critères attendu : "motcle=velo&zone=paris&etat=BON_ETAT"
     */
    @Scheduled(fixedRate = 3600000) // Exécute toutes les heures (3600000 ms)
    @Transactional(readOnly = true)
    public void checkForNewMatchesAndNotify() {
        logger.info("Démarrage de la vérification des nouvelles annonces correspondant aux recherches sauvegardées");
        
        List<RechercheSauv> recherchesActives = rechercheRepository.findByNotificationsActivesTrue();
        
        for (RechercheSauv recherche : recherchesActives) {
            try {
                // Récupère le dernier temps de vérification pour cette recherche
                LocalDateTime lastCheck = lastCheckTimes.getOrDefault(recherche.getId(), LocalDateTime.now().minusDays(7));
                
                // Parse les critères de recherche
                Map<String, String> criteres = parseCriteres(recherche.getCriteres());
                
                // Construit la spécification de recherche
                Specification<Annonce> spec = buildSearchSpecification(criteres, lastCheck);
                
                // Recherche les annonces correspondantes
                List<Annonce> nouvellesAnnonces = annonceRepository.findAll(spec);
                
                if (!nouvellesAnnonces.isEmpty()) {
                    // Envoie une notification à l'utilisateur
                    notifyUser(recherche.getUtilisateur(), nouvellesAnnonces, criteres);
                    
                    logger.info("Notification envoyée à l'utilisateur {} : {} nouvelle(s) annonce(s) trouvée(s)", 
                            recherche.getUtilisateur().getPseudo(), nouvellesAnnonces.size());
                }
                
                // Met à jour le dernier temps de vérification
                lastCheckTimes.put(recherche.getId(), LocalDateTime.now());
                
            } catch (Exception e) {
                logger.error("Erreur lors de la vérification de la recherche ID {}: {}", 
                        recherche.getId(), e.getMessage(), e);
            }
        }
        
        logger.info("Fin de la vérification des recherches sauvegardées");
    }
    
    /**
     * Parse la chaîne de critères en Map
     * Format attendu : "motcle=velo&zone=paris&etat=BON_ETAT"
     */
    private Map<String, String> parseCriteres(String criteresString) {
        Map<String, String> criteres = new HashMap<>();
        
        if (criteresString == null || criteresString.trim().isEmpty()) {
            return criteres;
        }
        
        String[] pairs = criteresString.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                criteres.put(keyValue[0].trim().toLowerCase(), keyValue[1].trim());
            }
        }
        
        return criteres;
    }
    
    /**
     * Construit une Specification JPA pour la recherche d'annonces
     */
    private Specification<Annonce> buildSearchSpecification(Map<String, String> criteres, LocalDateTime lastCheck) {
        return (root, query, criteriaBuilder) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();
            
            // Filtre par date de publication (seulement les nouvelles annonces)
            predicates.add(criteriaBuilder.greaterThan(root.get("datePublication"), lastCheck));
            
            // Filtre par mot-clé
            if (criteres.containsKey("motcle")) {
                String motcle = criteres.get("motcle");
                predicates.add(criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("titre")), "%" + motcle.toLowerCase() + "%"),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + motcle.toLowerCase() + "%")
                ));
            }
            
            // Filtre par zone géographique
            if (criteres.containsKey("zone")) {
                String zone = criteres.get("zone");
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("zoneGeographique")), 
                    "%" + zone.toLowerCase() + "%"
                ));
            }
            
            // Filtre par état
            if (criteres.containsKey("etat")) {
                try {
                    EtatObjet etat = EtatObjet.valueOf(criteres.get("etat").toUpperCase());
                    predicates.add(criteriaBuilder.equal(root.get("etat"), etat));
                } catch (IllegalArgumentException e) {
                    logger.warn("État d'objet invalide : {}", criteres.get("etat"));
                }
            }
            
            return criteriaBuilder.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }
    
    /**
     * Envoie une notification à l'utilisateur concernant les nouvelles annonces
     * Cette méthode peut être étendue pour envoyer des emails, notifications push, etc.
     */
    private void notifyUser(User utilisateur, List<Annonce> annonces, Map<String, String> criteres) {
        // Pour l'instant, utilisation du logger
        // TODO: Implémenter l'envoi d'emails ou de notifications push
        
        StringBuilder message = new StringBuilder();
        message.append("Bonjour ").append(utilisateur.getPseudo()).append(",\n\n");
        message.append("Nous avons trouvé ").append(annonces.size())
               .append(" nouvelle(s) annonce(s) correspondant à votre recherche sauvegardée :\n");
        message.append("Critères : ").append(formatCriteres(criteres)).append("\n\n");
        
        for (Annonce annonce : annonces) {
            message.append("- ").append(annonce.getTitre())
                   .append(" (").append(annonce.getZoneGeographique()).append(")\n");
        }
        
        message.append("\nConnectez-vous pour consulter les détails.\n");
        
        logger.info("Notification pour {} : {}", utilisateur.getEmail(), message.toString());
        
        // Ici, vous pourriez utiliser un service d'envoi d'email :
        // emailService.sendEmail(utilisateur.getEmail(), "Nouvelles annonces disponibles", message.toString());
    }
    
    /**
     * Formate les critères pour l'affichage
     */
    private String formatCriteres(Map<String, String> criteres) {
        if (criteres.isEmpty()) {
            return "Aucun critère spécifique";
        }
        
        return criteres.entrySet().stream()
                .map(entry -> entry.getKey() + " : " + entry.getValue())
                .collect(Collectors.joining(", "));
    }
    
    /**
     * Recherche manuelle d'annonces basée sur des critères
     * Utile pour tester une recherche avant de la sauvegarder
     */
    public List<Annonce> searchAnnonces(Map<String, String> criteres) {
        Specification<Annonce> spec = buildSearchSpecification(criteres, LocalDateTime.MIN);
        return annonceRepository.findAll(spec);
    }
    
    /**
     * Recherche manuelle avec chaîne de critères
     */
    public List<Annonce> searchAnnonces(String criteresString) {
        Map<String, String> criteres = parseCriteres(criteresString);
        return searchAnnonces(criteres);
    }
}