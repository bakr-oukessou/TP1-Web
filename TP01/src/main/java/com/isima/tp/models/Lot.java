package com.isima.tp.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@Table(name = "lots")
@EqualsAndHashCode(exclude = {"createur", "annonces"})
public class Lot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createur_id", nullable = false)
    private Utilisateur createur;

    // Les annonces incluses dans ce lot
    // 'mappedBy = "lot"' signifie que l'entité Annonce gère la clé étrangère
    @OneToMany(mappedBy = "lot", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Set<Annonce> annonces = new HashSet<>();
}