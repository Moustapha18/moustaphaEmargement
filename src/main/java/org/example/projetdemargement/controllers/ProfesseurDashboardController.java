package org.example.projetdemargement.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class ProfesseurDashboardController {

    @FXML
    public Button logoutButton;

    public void goToEmargement(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/emargement_page.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Émargement");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void goToHistorique(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/professeur_historique.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Historique des Émargements");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
