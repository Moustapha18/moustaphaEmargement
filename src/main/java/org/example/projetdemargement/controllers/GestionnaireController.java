package org.example.projetdemargement.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.example.projetdemargement.MainApp;
import org.example.projetdemargement.utils.SceneSwitcher;

import java.io.IOException;

public class GestionnaireController {

    public Button assignCoursesButton;
    @FXML private Button manageRoomsButton;
    @FXML private Button viewCoursesButton;
    @FXML private Button manageCoursesButton; // ✅ Ajout du bouton gérer les cours
    @FXML private Button logoutButton;

    

    @FXML
    public void initialize() {
        assignCoursesButton.setOnAction(e -> openViewCourses());
        if (manageRoomsButton != null) {
            manageRoomsButton.setOnAction(event -> openManageRooms());
        } else {
            System.out.println("⚠️ manageRoomsButton est NULL !");
        }

        if (viewCoursesButton != null) {
            viewCoursesButton.setOnAction(event -> openViewCourses());
        } else {
            System.out.println("⚠️ viewCoursesButton est NULL !");
        }

        if (manageCoursesButton != null) {
            manageCoursesButton.setOnAction(event -> openManageCourses());
        } else {
            System.out.println("⚠️ manageCoursesButton est NULL !");
        }

        if (logoutButton != null) {
            logoutButton.setOnAction(event -> logout());
        } else {
            System.out.println("⚠️ logoutButton est NULL !");
        }
    }

    private void openManageRooms() {
        SceneSwitcher.switchScene(MainApp.primaryStage,"/views/manage_rooms.fxml");
    }

    private void openViewCourses() {
        SceneSwitcher.switchScene(MainApp.primaryStage,"/views/view_courses.fxml");
    }

    private void openManageCourses() {
        SceneSwitcher.switchScene(MainApp.primaryStage,"/views/gestionnaire_cours.fxml"); // ✅ Vérifie que ce fichier existe bien
    }

    private void logout() {
        SceneSwitcher.switchScene(MainApp.primaryStage,"/views/login.fxml");
    }

    private void loadPage(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) manageRoomsButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("❌ Erreur lors du chargement de la page : " + fxmlPath);
        }
    }

    public void handleBackButtonAction(ActionEvent event) {
    }
}
