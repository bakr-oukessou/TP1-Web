package com.isima.tp.Controllers;

import com.isima.tp.business.AnnonceService;
import com.isima.tp.models.Annonce;
import com.isima.tp.models.enums.EtatObjet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/annonces")
public class AnnonceViewController {

    private final AnnonceService annonceService;

    @Autowired
    public AnnonceViewController(AnnonceService annonceService) {
        this.annonceService = annonceService;
    }

    /**
     * List all announcements with optional filters
     */
    @GetMapping
    public String listAnnonces(
            @RequestParam(required = false) String zone,
            @RequestParam(required = false) String etat,
            @RequestParam(required = false) String motcle,
            Model model) {
        
        List<Annonce> annonces;
        
        // Apply filters
        if (zone != null && !zone.isEmpty()) {
            annonces = annonceService.getAnnoncesByZone(zone);
        } else if (etat != null && !etat.isEmpty()) {
            annonces = annonceService.getAnnoncesByEtat(EtatObjet.valueOf(etat));
        } else if (motcle != null && !motcle.isEmpty()) {
            annonces = annonceService.getAnnoncesByMotCle(motcle);
        } else {
            annonces = annonceService.getAllAnnonces();
        }
        
        model.addAttribute("annonces", annonces);
        return "annonces/list";
    }

    /**
     * Show announcement details
     */
    @GetMapping("/{id}")
    public String showAnnonce(@PathVariable Long id, Model model) {
        return annonceService.getAnnonceById(id)
                .map(annonce -> {
                    model.addAttribute("annonce", annonce);
                    return "annonces/detail";
                })
                .orElse("redirect:/annonces");
    }

    /**
     * Show form to create new announcement
     */
    @GetMapping("/new")
    public String newAnnonceForm() {
        return "annonces/form";
    }
}
