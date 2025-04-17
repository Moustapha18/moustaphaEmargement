package org.example.projetdemargement.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;

import org.example.projetdemargement.models.Emargement;
import org.example.projetdemargement.services.EmargementService;
import org.example.projetdemargement.utils.SessionManager;

import java.io.IOException;
import java.util.List;

public class ProfesseurHistoriqueController {

    @FXML private TableView<Emargement> historiqueTable;
    @FXML private TableColumn<Emargement, String> coursColumn;
    @FXML private TableColumn<Emargement, String> dateColumn;
    @FXML private TableColumn<Emargement, String> heureDebutColumn;
    @FXML private TableColumn<Emargement, String> heureFinColumn;
    @FXML private TableColumn<Emargement, String> statutColumn;

    private final EmargementService emargementService = new EmargementService();

    @FXML
    public void initialize() {
        // Liaisons entre les colonnes et les données d’émargement
        coursColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCours().getNom()));

        dateColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDateEmargement().toString()));

        heureDebutColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCours().getHeureDebut().toString()));

        heureFinColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCours().getHeureFin().toString()));

        statutColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getStatut()));

        // Chargement de l’historique
        loadHistorique();
    }

    private void loadHistorique() {
        Long professeurId = SessionManager.getCurrentUser().getId();
        List<Emargement> historique = EmargementService.getHistoriqueEmargements(professeurId);
        historiqueTable.setItems(FXCollections.observableArrayList(historique));
    }

    @FXML
    public void goBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/professeur_dashboard.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Tableau de bord Professeur");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
