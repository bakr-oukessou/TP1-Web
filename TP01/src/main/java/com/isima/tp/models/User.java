package com.isima.tp.models;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Set;
import java.time.LocalDateTime;

@Entity
@Data // Génère getters, setters, toString, equals/hashCode
@NoArgsConstructor
@Table(name = "utilisateurs")
// Exclure les collections des méthodes equals/hashCode pour éviter les problèmes
@EqualsAndHashCode(exclude = {"annoncesPostees", "favoris", "recherches", "lots"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @Email
    @Column(unique = true, nullable = false)
    private String email;

    @NotEmpty
    @Column(nullable = false)
    private String pseudo;

    private String zoneGeographique;

    @OneToMany(mappedBy = "donneur", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Annonce> annoncesPostees = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "utilisateur_favoris",
            joinColumns = @JoinColumn(name = "utilisateur_id"),
            inverseJoinColumns = @JoinColumn(name = "annonce_id")
    )
    private Set<Annonce> favoris = new HashSet<>();

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<RechercheSauv> recherches = new HashSet<>();

    @OneToMany(mappedBy = "createur", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Lot> lots = new HashSet<>();

}