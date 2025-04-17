package org.example.projetdemargement.services;

import org.example.projetdemargement.models.User;
import org.example.projetdemargement.utils.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

public class UserService {

    public void register(User user) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt())); // Hash du mot de passe
            em.persist(user);
            em.getTransaction().commit();
            System.out.println("✅ Utilisateur inscrit !");
        } finally {
            em.close();
        }
    }



    public User authenticate(String email, String password) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);
            query.setParameter("email", email);
            List<User> users = query.getResultList();

            if (users.isEmpty()) {
                System.out.println("❌ Aucun utilisateur trouvé avec cet email.");
                return null;
            }

            User user = users.get(0);

            // ✅ Vérifier le mot de passe avec BCrypt
            if (BCrypt.checkpw(password, user.getPassword())) {
                System.out.println("✅ Connexion réussie pour : " + user.getNom());
                return user;
            } else {
                System.out.println("❌ Mot de passe incorrect.");
                return null;
            }
        } finally {
            em.close();
        }
    }


    public List<User> getAllUsers() {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT u FROM User u", User.class).getResultList();
        } finally {
            em.close();
        }
    }
    public void addUser(User newUser) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();

            // 🛑 Vérifier si l'email existe déjà
            TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);
            query.setParameter("email", newUser.getEmail());
            List<User> existingUsers = query.getResultList();

            if (!existingUsers.isEmpty()) {
                System.out.println("❌ L'email existe déjà !");
                return;
            }

            // 🔥 Hachage du mot de passe avant l'ajout
            String hashedPassword = BCrypt.hashpw(newUser.getPassword(), BCrypt.gensalt());
            newUser.setPassword(hashedPassword);

            em.persist(newUser);
            em.getTransaction().commit();
            System.out.println("✅ Utilisateur ajouté : " + newUser);
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }


    public void updateUser(User user) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(user);
            em.getTransaction().commit();
            System.out.println("✅ Utilisateur mis à jour avec succès !");
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public void deleteUser(Long id) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            User user = em.find(User.class, id);
            if (user != null) {
                em.getTransaction().begin();
                em.remove(user);
                em.getTransaction().commit();
                System.out.println("✅ Utilisateur supprimé !");
            }
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

}
