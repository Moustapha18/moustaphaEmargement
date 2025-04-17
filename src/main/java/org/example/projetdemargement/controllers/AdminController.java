package org.example.projetdemargement.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.example.projetdemargement.MainApp;
import org.example.projetdemargement.utils.SceneSwitcher;
import org.example.projetdemargement.utils.SessionManager;

import java.io.IOException;
import java.util.EventObject;

public class AdminController {

    public Button validationEmargementButton;
    @FXML private Button manageUsersButton;
    @FXML private Button manageRoomsButton;
    @FXML private Button validateEmargementButton; // 🔹 Ajout du bouton pour valider les émargements
    @FXML private Button logoutButton;
    @FXML private Button historiqueButton;
    private EventObject event;


    @FXML
    public void initialize() {
        if (historiqueButton != null) {
            historiqueButton.setOnAction(this::openHistoriqueEmargements); // 👈 Correction ici
        }
        if (manageUsersButton != null) {
            manageUsersButton.setOnAction(event -> openManageUsers());
        }
        if (manageRoomsButton != null) {
            manageRoomsButton.setOnAction(event -> openManageRooms());
        }
        if (validateEmargementButton != null) {
            validateEmargementButton.setOnAction(event -> openValidationEmargements());
        }
        if (logoutButton != null) {
            logoutButton.setOnAction(event -> logout());
        }
    }

    @FXML
    private void openHistoriqueEmargements(ActionEvent event) {
        SceneSwitcher.switchScene(MainApp.primaryStage,"/views/HistoriqueEmargement.fxml");
    }

    private void openManageUsers() {
        SceneSwitcher.switchScene(MainApp.primaryStage,"/views/manage_users.fxml");
    }

    private void openManageRooms() {
        SceneSwitcher.switchScene(MainApp.primaryStage,"/views/manage_rooms.fxml");
    }


    private void logout() {
        SessionManager.logout();
        loadPage("/views/login.fxml");
    }

    /**
     * 🔹 Méthode pour ouvrir la page de validation des émargements.
     */
    @FXML
    private void openValidationEmargements() {
        SceneSwitcher.switchScene(MainApp.primaryStage,"/views/admin_validation_emargement.fxml");
    }
//    @FXML
//    private void openHistoriqueEmargements(){
//        loadPage("/views/admin_Historique.fxml");
//    }

    /**
     * 📌 Méthode générique pour charger une nouvelle page.
     * @param fxmlPath Chemin du fichier FXML à charger.
     */
    private void loadPage(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) historiqueButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("❌ Erreur lors du chargement de la page : " + fxmlPath);
        }
    }

    @FXML
    private void goToStatistiques(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/statistiques.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("❌ Impossible de charger la page des statistiques.");
        }
    }




//    public void openHistoriqueEmargements(ActionEvent event) {
//
//    }




}
