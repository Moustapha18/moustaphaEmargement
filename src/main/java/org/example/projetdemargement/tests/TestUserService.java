package org.example.projetdemargement.tests;
import org.example.projetdemargement.models.Role;
import org.example.projetdemargement.models.User;
import org.example.projetdemargement.services.UserService;

public class TestUserService {
    public static void main(String[] args) {
        UserService userService = new UserService();

        // Ajout d'un utilisateur test
        User admin = new User(null, "Admin", "Superdev", "admin@example.com", "admin123", Role.ADMIN);
        userService.addUser(admin);

        System.out.println("Utilisateur ajouté avec succès !");
    }
}
