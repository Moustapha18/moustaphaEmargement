package org.example.projetdemargement.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.example.projetdemargement.IOException;
import org.example.projetdemargement.models.Emargement;
import org.example.projetdemargement.services.EmargementService;

import java.util.List;

public class AdminValidationEmargementController {
    @FXML
    private Button backButton;
    @FXML private TableView<Emargement> emargementTable;
    @FXML private TableColumn<Emargement, String> professeurColumn;
    @FXML private TableColumn<Emargement, String> coursColumn;
    @FXML private TableColumn<Emargement, String> dateColumn;
    @FXML private TableColumn<Emargement, String> heureDebutColumn;
    @FXML private TableColumn<Emargement, String> heureFinColumn;
    @FXML private TableColumn<Emargement, String> statutColumn;
    @FXML private TableColumn<Emargement, Void> actionColumn;

    private final EmargementService emargementService = new EmargementService();

    @FXML
    public void initialize() {
        professeurColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getProfesseur().getNomComplet()));
        coursColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getCours().getNom()));
        dateColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getDateEmargement().toString()));
        heureDebutColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getHeureDebut().toString()));
        heureFinColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getHeureFin().toString()));
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));
        backButton.setOnAction(e -> goBack());
        addActionButtons();
        loadEmargements();
    }

    private void loadEmargements() {
        List<Emargement> emargements = emargementService.getEmargementsNonValides();
        emargementTable.setItems(FXCollections.observableArrayList(emargements));
    }

    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/admin_dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addActionButtons() {
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button validerBtn = new Button("Valider");
            private final Button rejeterBtn = new Button("Rejeter");

            {
                validerBtn.setOnAction(event -> {
                    Emargement e = getTableView().getItems().get(getIndex());
                    emargementService.validerEmargement(e.getId());
                    loadEmargements();
                });

                rejeterBtn.setOnAction(event -> {
                    Emargement e = getTableView().getItems().get(getIndex());
                    emargementService.rejeterEmargement(e.getId());
                    loadEmargements();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(10, validerBtn, rejeterBtn);
                    setGraphic(box);
                }
            }
        });
    }
}
