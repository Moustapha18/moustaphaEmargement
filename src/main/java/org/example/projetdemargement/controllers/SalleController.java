package org.example.projetdemargement.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.example.projetdemargement.utils.SessionManager;

import java.io.IOException;

public class SalleController {

    @FXML private Button backButton;
    @FXML private Button logoutButton;

    @FXML
    public void initialize() {
        if (backButton != null) {
            backButton.setOnAction(event -> goBack());
        }
        if (logoutButton != null) {
            logoutButton.setOnAction(event -> logout());
        }
    }

    /**
     * 🔙 Retourne au tableau de bord principal
     */
    private void goBack() {
        loadPage("/views/admin_dashboard.fxml"); // Change cette ligne si tu veux un autre écran
    }

    /**
     * 🚪 Déconnecte l'utilisateur et retourne à l'écran de connexion
     */
    private void logout() {
        SessionManager.logout();
        loadPage("/views/login.fxml");
    }

    /**
     * Charge une nouvelle interface dans la fenêtre actuelle
     */
    private void loadPage(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("❌ Erreur lors du chargement de la page : " + fxmlPath);
        }
    }
}
