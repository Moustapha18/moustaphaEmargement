package org.example.projetdemargement.models;

import jakarta.persistence.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class Emargement {

    public static Object Statut;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User professeur;

    @ManyToOne
    private Cours cours;

    private LocalDate dateEmargement;
    private LocalTime heureEmargement;

    private LocalTime heureDebut;
    private LocalTime heureFin;

    private String statut; // "En attente", "Validé", "Rejeté"

    // 🔹 Getters et Setters

    public Long getId() {
        return id;
    }

    public User getProfesseur() {
        return professeur;
    }

    public void setProfesseur(User professeur) {
        this.professeur = professeur;
    }

    public Cours getCours() {
        return cours;
    }

    public void setCours(Cours cours) {
        this.cours = cours;
    }

    public LocalDate getDateEmargement() {
        return dateEmargement;
    }

    public void setDateEmargement(LocalDate dateEmargement) {
        this.dateEmargement = dateEmargement;
    }

    public LocalTime getHeureEmargement() {
        return heureEmargement;
    }

    public void setHeureEmargement(LocalTime heureEmargement) {
        this.heureEmargement = heureEmargement;
    }

    public LocalTime getHeureDebut() {
        return heureDebut;
    }

    public void setHeureDebut(LocalTime heureDebut) {
        this.heureDebut = heureDebut;
    }

    public LocalTime getHeureFin() {
        return heureFin;
    }

    public void setHeureFin(LocalTime heureFin) {
        this.heureFin = heureFin;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    // 🔹 Pour liaison avec TableView JavaFX
    public ObservableValue<String> dateEmargementProperty() {
        return new SimpleStringProperty(dateEmargement != null ? dateEmargement.toString() : "");
    }

    public ObservableValue<String> heureEmargementProperty() {
        return new SimpleStringProperty(heureEmargement != null ? heureEmargement.toString() : "");
    }
}
