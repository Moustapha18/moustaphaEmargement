package org.example.projetdemargement.services;

import org.example.projetdemargement.models.Salle;
import org.example.projetdemargement.utils.JPAUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SalleService {

    private static final Logger LOGGER = Logger.getLogger(SalleService.class.getName());

    public void addSalle(Salle salle) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(salle);
            em.getTransaction().commit();
            LOGGER.info("✅ Salle ajoutée avec succès !");
        } catch (Exception e) {
            em.getTransaction().rollback();
            LOGGER.log(Level.SEVERE, "Erreur lors de l'ajout de la salle", e);
        } finally {
            em.close();
        }
    }

    public Salle getSalleById(Long id) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.find(Salle.class, id);
        } finally {
            em.close();
        }
    }


    public List<Salle> getAllSalles() {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<Salle> query = em.createQuery("SELECT s FROM Salle s", Salle.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public void updateSalle(Salle salle) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(salle);
            em.getTransaction().commit();
            LOGGER.info("✅ Salle mise à jour avec succès !");
        } catch (Exception e) {
            em.getTransaction().rollback();
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour de la salle", e);
        } finally {
            em.close();
        }
    }

    public void deleteSalle(Long id) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            Salle salle = em.find(Salle.class, id);
            if (salle != null) {
                em.getTransaction().begin();
                em.remove(salle);
                em.getTransaction().commit();
                LOGGER.info("✅ Salle supprimée avec succès !");
            } else {
                LOGGER.warning("⚠️ Salle introuvable, suppression impossible !");
            }
        } catch (Exception e) {
            em.getTransaction().rollback();
            LOGGER.log(Level.SEVERE, "Erreur lors de la suppression de la salle", e);
        } finally {
            em.close();
        }
    }
}
