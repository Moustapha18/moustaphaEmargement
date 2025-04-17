package org.example.projetdemargement.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.example.projetdemargement.models.Emargement;
import org.example.projetdemargement.services.EmargementService;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminEmargementController implements Initializable {

    @FXML private TableView<Emargement> emargementTable;
    @FXML private TableColumn<Emargement, String> idColumn;
    @FXML private TableColumn<Emargement, String> professeurColumn;
    @FXML private TableColumn<Emargement, String> coursColumn;
    @FXML private TableColumn<Emargement, String> dateColumn;
    @FXML private TableColumn<Emargement, String> heureDebutColumn;
    @FXML private TableColumn<Emargement, String> heureFinColumn;
    @FXML private TableColumn<Emargement, String> statutColumn;
    @FXML private TableColumn<Emargement, Void> actionColumn;

    private final EmargementService emargementService = new EmargementService();
    private ObservableList<Emargement> emargementList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getId())));
        professeurColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProfesseur().getNomComplet()));
        coursColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCours().getNom()));
        dateColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDateEmargement().toString()));
        heureDebutColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getHeureDebut().toString()));
        heureFinColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getHeureFin().toString()));
        statutColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatut()));

        addActionButtonsToTable();
        loadEmargements();
    }

    private void loadEmargements() {
        emargementList = FXCollections.observableArrayList(emargementService.getAllEmargements());
        emargementTable.setItems(emargementList);
    }

    private void addActionButtonsToTable() {
        Callback<TableColumn<Emargement, Void>, TableCell<Emargement, Void>> cellFactory = param -> new TableCell<>() {
            private final Button btnValider = new Button("Valider");
            private final Button btnRejeter = new Button("Rejeter");
            private final HBox pane = new HBox(10, btnValider, btnRejeter);

            {
                btnValider.setOnAction((ActionEvent event) -> {
                    Emargement emargement = getTableView().getItems().get(getIndex());
                    handleValidation(emargement, true);
                });
                btnRejeter.setOnAction((ActionEvent event) -> {
                    Emargement emargement = getTableView().getItems().get(getIndex());
                    handleValidation(emargement, false);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        };

        actionColumn.setCellFactory(cellFactory);
    }

    private void handleValidation(Emargement emargement, boolean isValid) {
        emargement.setStatut(isValid ? "Validé" : "Rejeté");
        emargementService.updateEmargement(emargement);
        emargementTable.refresh();
    }
}
