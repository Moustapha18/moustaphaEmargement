package org.example.projetdemargement.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.projetdemargement.models.User;
import org.example.projetdemargement.utils.SessionManager;

import java.io.IOException;

public class ProfesseurController {
    @FXML private Label welcomeLabel;
    @FXML private Button manageCoursesButton;
    @FXML private Button viewAttendanceButton;
    @FXML private Button logoutButton;
    @FXML private Button historiqueButton;

    @FXML
    public void initialize() {
        historiqueButton.setOnAction(event -> openHistorique());
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser != null) {
            welcomeLabel.setText("Bienvenue Professeur " + currentUser.getNom() + " !");
        }

        manageCoursesButton.setOnAction(event -> openManageCourses());
        viewAttendanceButton.setOnAction(event -> openViewAttendance());
        logoutButton.setOnAction(event -> logout());
    }
    private void openHistorique() {
        loadPage("/views/historique_emargements.fxml");
    }

    private void openManageCourses() {
        loadPage("/views/manage_courses.fxml");
    }

    private void openViewAttendance() {
        loadPage("/views/view_attendance.fxml");
    }

    private void logout() {
        SessionManager.logout();
        loadPage("/views/login.fxml");
    }

    private void loadPage(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleEmargement(ActionEvent event) {
    }
}
