package org.example.projetdemargement.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.example.projetdemargement.models.Cours;
import org.example.projetdemargement.utils.JPAUtil;

import java.util.List;
import java.util.logging.Logger;

public class CoursService {

    private static final Logger LOGGER = Logger.getLogger(CoursService.class.getName());

    public void addCours(Cours cours) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(cours);
            em.getTransaction().commit();
            System.out.println("✅ Cours ajouté : " + cours);
        } catch (Exception e) {
            em.getTransaction().rollback();
            System.err.println("❌ ERREUR lors de l'ajout du cours : " + e.getMessage());
        } finally {
            em.close();
        }
    }

    public List<Cours> getAllCours() {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<Cours> query = em.createQuery("SELECT c FROM Cours c", Cours.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    public void updateCours(Cours cours) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(cours);
            em.getTransaction().commit();
            System.out.println("✅ Cours mis à jour avec succès !");
        } catch (Exception e) {
            em.getTransaction().rollback();
            System.err.println("❌ Erreur lors de la mise à jour du cours : " + e.getMessage());
        } finally {
            em.close();
        }
    }


    public void deleteCours(Long id) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            Cours cours = em.find(Cours.class, id);
            if (cours != null) {
                em.getTransaction().begin();
                em.remove(cours);
                em.getTransaction().commit();
                System.out.println("✅ Cours supprimé avec succès !");
            } else {
                System.err.println("❌ Cours introuvable, suppression impossible !");
            }
        } catch (Exception e) {
            em.getTransaction().rollback();
            System.err.println("❌ Erreur lors de la suppression du cours : " + e.getMessage());
        } finally {
            em.close();
        }
    }

    public boolean hasProfesseurConflict(Cours nouveauCours) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(c) FROM Cours c " +
                            "WHERE c.professeur.id = :profId " +
                            "AND c.dateCours = :date " +
                            "AND (:heureDebut < c.heure_Fin AND :heureFin > c.heure_Debut)",
                    Long.class
            );

            query.setParameter("profId", nouveauCours.getProfesseur().getId());
            query.setParameter("date", nouveauCours.getDate());
            query.setParameter("heureDebut", nouveauCours.getHeure_Debut());
            query.setParameter("heureFin", nouveauCours.getHeure_Fin());

            Long count = query.getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }


    public boolean hasScheduleConflict(Cours nouveauCours) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<Cours> query = em.createQuery(
                    "SELECT c FROM Cours c WHERE " +
                            "c.salle.id = :salleId AND " +
                            "c.dateCours = :dateCours AND " +
                            "(:heureDebut < c.heure_Fin AND :heureFin > c.heure_Debut)",
                    Cours.class
            );

            query.setParameter("salleId", nouveauCours.getSalle().getId());
            query.setParameter("dateCours", nouveauCours.getDate());
            query.setParameter("heureDebut", nouveauCours.getHeure_Debut());
            query.setParameter("heureFin", nouveauCours.getHeure_Fin());

            List<Cours> conflits = query.getResultList();

            if (!conflits.isEmpty()) {
                System.out.println("🚨 Conflit détecté avec " + conflits.size() + " cours !");
                for (Cours c : conflits) {
                    System.out.println("➡️ " + c.getNom() + " (" + c.getHeure_Debut() + " à " + c.getHeure_Fin() + ")");
                }
            }

            return !conflits.isEmpty();
        } finally {
            em.close();
        }
    }





    public List<Cours> getCoursByProfesseur(Long professeurId) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        List<Cours> coursList = null;

        try {
            TypedQuery<Cours> query = em.createQuery("SELECT c FROM Cours c WHERE c.professeur.id = :professeurId", Cours.class);
            query.setParameter("professeurId", professeurId);
            coursList = query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }

        return coursList;
    }
}
