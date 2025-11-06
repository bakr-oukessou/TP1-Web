package com.isima.tp.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@Table(name = "mots_cles")
@EqualsAndHashCode(exclude = {"annonces"})
public class MotCle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nom;

    // Inverse de la relation dans Annonce
    @ManyToMany(mappedBy = "motsCles", fetch = FetchType.LAZY)
    private Set<Annonce> annonces = new HashSet<>();

    // Constructeur utile pour créer un MotCle
    public MotCle(String nom) {
        this.nom = nom;
    }
}