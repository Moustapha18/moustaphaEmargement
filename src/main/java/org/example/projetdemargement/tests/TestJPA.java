package org.example.projetdemargement.tests;

import org.example.projetdemargement.models.Role;
import org.example.projetdemargement.models.User;
import org.example.projetdemargement.utils.JPAUtil;
import jakarta.persistence.EntityManager;

public class TestJPA {
    public static void main(String[] args) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        em.getTransaction().begin();

        User user = new User();
        user.setNom("Dupont");
        user.setPrenom("Jean");
        user.setEmail("jean.dupont@example.com");
        user.setPassword("password12345");
        user.setRole(Role.ADMIN);

        em.persist(user);
        em.getTransaction().commit();

        em.close();
        JPAUtil.close();

        System.out.println("✅ Connexion réussie et utilisateur ajouté !");
    }
}
