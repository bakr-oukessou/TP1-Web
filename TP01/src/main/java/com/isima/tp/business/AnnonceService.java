package com.isima.tp.business;
import com.isima.tp.Repositories.AnnonceRepository;
import com.isima.tp.Repositories.MotCleRepository;
import com.isima.tp.models.Annonce;
import com.isima.tp.models.MotCle;
import com.isima.tp.models.User;
import com.isima.tp.models.enums.EtatObjet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AnnonceService {

    private final AnnonceRepository annonceRepository;
    private final MotCleRepository motCleRepository;

    @Autowired
    public AnnonceService(AnnonceRepository annonceRepository, MotCleRepository motCleRepository) {
        this.annonceRepository = annonceRepository;
        this.motCleRepository = motCleRepository;
    }

    @Transactional
    public Annonce save(Annonce annonce, Set<String> motsClesNouveaux) {
        Set<MotCle> motsCles = new HashSet<>();

        if (motsClesNouveaux != null) {
            for (String nomMotCle : motsClesNouveaux) {
                MotCle motCle = motCleRepository.findByNom(nomMotCle.toLowerCase())
                        .orElseGet(() -> motCleRepository.save(new MotCle(nomMotCle.toLowerCase())));
                motsCles.add(motCle);
            }
        }

        annonce.setMotsCles(motsCles);

        // 2. Sauvegarder l'annonce
        return annonceRepository.save(annonce);
    }

    public Page<Annonce> findAll(Pageable pageable) {
        return annonceRepository.findAll(pageable);
    }

    public Optional<Annonce> findById(Long id) {
        return annonceRepository.findById(id);
    }


    @Transactional
    public boolean deleteById(Long annonceId, User utilisateur) {
        Optional<Annonce> annonceOpt = annonceRepository.findById(annonceId);

        if (annonceOpt.isPresent() && annonceOpt.get().getDonneur().getId().equals(utilisateur.getId())) {
            annonceRepository.delete(annonceOpt.get());
            return true;
        }
        return false;
    }

    public List<Annonce> findByKeyword(String keyword) {
        return annonceRepository.findByMotsCles_Nom(keyword.toLowerCase());
    }
    
    // ===== Methods for AnnonceController =====
    
    /**
     * Save an annonce (simplified version without keywords parameter)
     */
    @Transactional
    public Annonce saveAnnonce(Annonce annonce) {
        // Extract keywords from the annonce if they exist
        Set<String> motsClesNouveaux = annonce.getMotsCles() != null 
            ? annonce.getMotsCles().stream()
                .map(MotCle::getNom)
                .collect(Collectors.toSet())
            : new HashSet<>();
        
        return save(annonce, motsClesNouveaux);
    }
    
    /**
     * Get all annonces
     */
    public List<Annonce> getAllAnnonces() {
        return annonceRepository.findAll();
    }
    
    /**
     * Get annonce by ID
     */
    public Optional<Annonce> getAnnonceById(Long id) {
        return findById(id);
    }
    
    /**
     * Get annonces by state
     */
    public List<Annonce> getAnnoncesByEtat(EtatObjet etat) {
        return annonceRepository.findByEtat(etat);
    }
    
    /**
     * Get annonces by geographic zone
     */
    public List<Annonce> getAnnoncesByZone(String zone) {
        return annonceRepository.findByZoneGeographiqueContainingIgnoreCase(zone);
    }
    
    /**
     * Get annonces by donor (donneur)
     */
    public List<Annonce> getAnnoncesByDonneur(Long donneurId) {
        return annonceRepository.findByDonneurId(donneurId);
    }
    
    /**
     * Get annonces by keyword
     */
    public List<Annonce> getAnnoncesByMotCle(String motcle) {
        return findByKeyword(motcle);
    }
    
    /**
     * Delete an annonce (simplified without user verification)
     * In production, you should verify the user has permission to delete
     */
    @Transactional
    public void deleteAnnonce(Long id) {
        annonceRepository.deleteById(id);
    }
}