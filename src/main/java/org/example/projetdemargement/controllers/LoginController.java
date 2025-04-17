package org.example.projetdemargement.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.projetdemargement.models.User;
import org.example.projetdemargement.services.UserService;
import org.example.projetdemargement.utils.SessionManager;

import java.io.IOException;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;

    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
        loginButton.setOnMouseEntered(event -> {
            loginButton.setStyle("-fx-background-color: #ffa733; -fx-text-fill: white;");
        });

        loginButton.setOnMouseExited(event -> {
            loginButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
        });
    }

    @FXML
    private void handleLogin() { // 🔹 Vérifiez que cette méthode existe bien
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Erreur", "Veuillez entrer un email et un mot de passe.", Alert.AlertType.ERROR);
            return;
        }

        User user = userService.authenticate(email, password);
        if (user == null) {
            showAlert("Erreur", "Identifiants incorrects. Veuillez réessayer.", Alert.AlertType.ERROR);
            return;
        }

        // Stocker l'utilisateur connecté
        SessionManager.setCurrentUser(user);

        // Rediriger vers le bon tableau de bord
        redirectToDashboard(user);
    }
    private void redirectToDashboard(User user) {
        String fxmlPath;
        switch (user.getRole()) {
            case ADMIN:
                fxmlPath = "/views/admin_dashboard.fxml";
                break;
            case GESTIONNAIRE:
                fxmlPath = "/views/gestionnaire_dashboard.fxml";
                break;
            case PROFESSEUR:
                fxmlPath = "/views/professeur_dashboard.fxml";  // 🔹 Vérifie ce chemin
                break;
            default:
                showAlert("Erreur", "Rôle utilisateur inconnu.", Alert.AlertType.ERROR);
                return;
        }

        loadDashboard(fxmlPath);
    }
    private void loadDashboard(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Vérification du bon chargement du contrôleur
            Object controller = loader.getController();
            if (controller == null) {
                System.out.println("⚠️ Erreur : le contrôleur du FXML est null !");
            }

            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger l'interface : " + fxmlPath, Alert.AlertType.ERROR);
        }
    }



    /**
     * 📌 Méthode pour ouvrir le formulaire d'inscription
     */
    public void openRegisterForm(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/register.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Inscription");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir le formulaire d'inscription.", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
