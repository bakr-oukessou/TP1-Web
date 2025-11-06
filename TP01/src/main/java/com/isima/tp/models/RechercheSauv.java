package com.isima.tp.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Data
@NoArgsConstructor
@Table(name = "recherches_sauvegardees")
public class RechercheSauv {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private User utilisateur;

    @Lob
    private String criteres; // Ex: "motcle=velo&zone=paris&etat=BON_ETAT"

    private boolean notificationsActives = true;
}