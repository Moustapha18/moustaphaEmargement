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
public class Cours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    private LocalDate dateCours;

    private LocalTime heure_Debut;

    private LocalTime heure_Fin;

    private String description;

    private LocalDate dateEmargement;

    @ManyToOne
    @JoinColumn(name = "salle_id", nullable = false)
    private Salle salle;

    @ManyToOne
    @JoinColumn(name = "professeur_id", nullable = false)
    private User professeur;

    // ✅ Constructeur sans description ni date d’émargement
    public Cours(String nom, LocalDate dateCours, LocalTime heureDebut, LocalTime heureFin, Salle salle, User professeur) {
        this.nom = nom;
        this.dateCours = dateCours;
        this.heure_Debut = heureDebut;
        this.heure_Fin = heureFin;
        this.salle = salle;
        this.professeur = professeur;
    }

    // ✅ Constructeur avec description
    public Cours(String nom, LocalDate dateCours, LocalTime heureDebut, LocalTime heureFin, String description, Salle salle, User professeur) {
        this.nom = nom;
        this.dateCours = dateCours;
        this.heure_Debut = heureDebut;
        this.heure_Fin = heureFin;
        this.description = description;
        this.salle = salle;
        this.professeur = professeur;
    }

    // ✅ Getters "custom" si besoin
    public LocalDate getDate() {
        return this.dateCours;
    }

    public void setDate(LocalDate date) {
        this.dateCours = date;
    }

    public LocalTime getHeureDebut() {
        return this.heure_Debut;
    }

    public void setHeureDebut(LocalTime heure) {
        this.heure_Debut = heure;
    }

    public LocalTime getHeureFin() {
        return this.heure_Fin;
    }

    public void setHeureFin(LocalTime heure) {
        this.heure_Fin = heure;
    }

    public String getNom() {
        return this.nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Salle getSalle() {
        return this.salle;
    }

    public void setSalle(Salle salle) {
        this.salle = salle;
    }

    public User getProfesseur() {
        return this.professeur;
    }

    public void setProfesseur(User professeur) {
        this.professeur = professeur;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String desc) {
        this.description = desc;
    }
}
