package com.isima.tp.models;

import com.isima.tp.models.enums.EtatObjet;
import com.isima.tp.models.enums.ModeLivraison;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@Table(name = "annonces")
@EqualsAndHashCode(exclude = {"donneur", "motsCles", "utilisateursFavoris", "lot"})
public class Annonce {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @Column(nullable = false)
    private String titre;

    @Lob // Permet un texte long
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING) // Stocke "NEUF" plutôt que l'index 0
    private EtatObjet etat;

    @CreationTimestamp // Géré automatiquement par Hibernate
    @Column(updatable = false)
    private LocalDateTime datePublication;

    private String zoneGeographique;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ModeLivraison modeLivraison;

    // --- Relations ---

    // Le donneur (propriétaire) de l'annonce
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donneur_id", nullable = false)
    private Utilisateur donneur;

    // Mots-clés associés
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "annonce_motscles",
            joinColumns = @JoinColumn(name = "annonce_id"),
            inverseJoinColumns = @JoinColumn(name = "motcle_id")
    )
    private Set<MotCle> motsCles = new HashSet<>();

    // Inverse de la relation 'favoris' dans Utilisateur
    @ManyToMany(mappedBy = "favoris", fetch = FetchType.LAZY)
    private Set<Utilisateur> utilisateursFavoris = new HashSet<>();

    // Lot auquel cette annonce peut appartenir (optionnel)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot_id")
    private Lot lot;
}