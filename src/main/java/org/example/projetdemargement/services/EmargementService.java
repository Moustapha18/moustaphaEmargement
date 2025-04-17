package org.example.projetdemargement.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.example.projetdemargement.models.Cours;
import org.example.projetdemargement.models.Emargement;
import org.example.projetdemargement.models.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class EmargementService {

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("EmargementPU");

    // 🔹 Récupère tous les émargements
    public static List<Emargement> getAllEmargements() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT e FROM Emargement e", Emargement.class).getResultList();
        } finally {
            em.close();
        }
    }

    // 🔹 Récupère l’historique des émargements d’un professeur
    public static List<Emargement> getHistoriqueEmargements(Long professeurId) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT e FROM Emargement e WHERE e.professeur.id = :pid ORDER BY e.dateEmargement DESC",
                            Emargement.class)
                    .setParameter("pid", professeurId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // 🔹 Enregistre un nouvel émargement
    public boolean enregistrerEmargement(Long professeurId, Long coursId) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            if (hasAlreadyEmarged(professeurId, coursId)) {
                return false;
            }

            User professeur = em.find(User.class, professeurId);
            Cours cours = em.find(Cours.class, coursId);

            if (professeur == null || cours == null) return false;

            Emargement emargement = new Emargement();
            emargement.setProfesseur(professeur);
            emargement.setCours(cours);
            emargement.setDateEmargement(LocalDate.now());
            emargement.setHeureEmargement(LocalTime.now());
            emargement.setHeureDebut(cours.getHeure_Debut());
            emargement.setHeureFin(cours.getHeure_Fin());
            emargement.setStatut("En attente");

            em.persist(emargement);
            em.getTransaction().commit();
            return true;

        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    // 🔹 Vérifie si le professeur a déjà émargé pour ce cours
    public boolean hasAlreadyEmarged(Long professeurId, Long coursId) {
        EntityManager em = emf.createEntityManager();
        try {
            Long count = em.createQuery(
                            "SELECT COUNT(e) FROM Emargement e WHERE e.professeur.id = :pid AND e.cours.id = :cid",
                            Long.class)
                    .setParameter("pid", professeurId)
                    .setParameter("cid", coursId)
                    .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }

    // 🔹 Liste des émargements à valider
    public List<Emargement> getEmargementsNonValides() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT e FROM Emargement e WHERE e.statut = 'En attente'", Emargement.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // 🔹 Valide un émargement
    public void validerEmargement(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Emargement e = em.find(Emargement.class, id);
            if (e != null) {
                e.setStatut("Validé");
                em.merge(e);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
            ex.printStackTrace();
        } finally {
            em.close();
        }
    }

    // 🔹 Rejette un émargement
    public void rejeterEmargement(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Emargement e = em.find(Emargement.class, id);
            if (e != null) {
                e.setStatut("Rejeté");
                em.merge(e);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
            ex.printStackTrace();
        } finally {
            em.close();
        }
    }

    // 🔹 Mise à jour d'un émargement
    public void updateEmargement(Emargement emargement) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(emargement);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public void marquerAbsent(Long professeurId, Long coursId) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            User professeur = em.find(User.class, professeurId);
            Cours cours = em.find(Cours.class, coursId);

            if (professeur == null || cours == null) {
                em.getTransaction().rollback();
                return;
            }

            Emargement emargement = new Emargement();
            emargement.setCours(cours);
            emargement.setProfesseur(professeur);
            emargement.setStatut("Absent");
            emargement.setDateEmargement(LocalDate.now());
            emargement.setHeureDebut(cours.getHeure_Debut());
            emargement.setHeureFin(cours.getHeure_Fin());
            emargement.setHeureEmargement(LocalTime.now());

            em.persist(emargement);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }


}
