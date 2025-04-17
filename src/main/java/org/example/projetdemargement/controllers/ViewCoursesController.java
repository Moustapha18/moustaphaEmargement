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
import org.example.projetdemargement.models.Cours;
import org.example.projetdemargement.services.CoursService;

import java.io.IOException;

public class ViewCoursesController {

    @FXML private TableView<Cours> coursesTable;
    @FXML private TableColumn<Cours, String> idColumn;
    @FXML private TableColumn<Cours, String> nameColumn;
    @FXML private TableColumn<Cours, String> dateColumn;
    @FXML private TableColumn<Cours, String> startTimeColumn;
    @FXML private TableColumn<Cours, String> endTimeColumn;
    @FXML private TableColumn<Cours, String> roomColumn;
    @FXML private TableColumn<Cours, String> professorColumn;

    private final CoursService coursService = new CoursService();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getId())));
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNom()));
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate().toString()));
        startTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getHeure_Debut().toString()));
        endTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getHeure_Fin().toString()));
        roomColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSalle().getLibelle()));
        professorColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProfesseur().getNom()));

        coursesTable.setItems(FXCollections.observableArrayList(coursService.getAllCours()));
    }

    @FXML
    private void handleBackButtonAction() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/views/gestionnaire_dashboard.fxml"));
        Stage stage = (Stage) coursesTable.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}
