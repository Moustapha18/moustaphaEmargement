package org.example.projetdemargement.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.example.projetdemargement.models.Presence;
import org.example.projetdemargement.models.StatutPresence;
import org.example.projetdemargement.models.User;

import java.time.LocalDate;
import java.util.List;

public class PresenceService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void addPresence(Presence presence) {
        entityManager.persist(presence);
    }

    @Transactional
    public void updatePresence(Presence presence) {
        entityManager.merge(presence);
    }

    public Presence getPresenceByProfAndDate(User prof, LocalDate date) {
        return entityManager.createQuery(
                        "SELECT p FROM Presence p WHERE p.professeur = :prof AND p.dateEmargement = :date", Presence.class)
                .setParameter("prof", prof)
                .setParameter("date", date)
                .getSingleResult();
    }

    public List<Presence> getAllPresences() {
        return entityManager.createQuery("SELECT p FROM Presence p", Presence.class).getResultList();
    }

    @Transactional
    public void validatePresence(Presence presence) {
        presence.setStatut(StatutPresence.PRESENT);
        entityManager.merge(presence);
    }

    @Transactional
    public void sendAlertForAbsence(User prof) {
        System.out.println("⚠️ ALERTE : Le professeur " + prof.getNom() + " ne s'est pas émargé à temps !");
        // Possibilité d'envoyer une notification dans le système
    }
}
