package com.isima.tp.business;
import com.isima.tp.Repositories.AnnonceRepository;
import com.isima.tp.Repositories.MotCleRepository;
import com.isima.tp.models.Annonce;
import com.isima.tp.models.MotCle;
import com.isima.tp.models.User;
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
}