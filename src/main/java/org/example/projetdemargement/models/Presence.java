package org.example.projetdemargement.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Presence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cours_id", nullable = false)
    private Cours cours;

    @ManyToOne
    @JoinColumn(name = "professeur_id", nullable = false)
    private User professeur;

    private LocalDate dateEmargement;
    private LocalTime heureArrivee;

    @Enumerated(EnumType.STRING)
    private StatutPresence statut;
}