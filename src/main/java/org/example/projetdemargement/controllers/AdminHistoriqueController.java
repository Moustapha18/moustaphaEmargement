package org.example.projetdemargement.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.example.projetdemargement.models.Emargement;
import org.example.projetdemargement.models.User;
import org.example.projetdemargement.services.EmargementService;
import org.example.projetdemargement.utils.SessionManager;

import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.util.ResourceBundle;

public class AdminHistoriqueController implements Initializable {

    @FXML private TableView<Emargement> historiqueTable;
    @FXML private TableColumn<Emargement, String> idColumn;
    @FXML private TableColumn<Emargement, String> professeurColumn;
    @FXML private TableColumn<Emargement, String> coursColumn;
    @FXML private TableColumn<Emargement, String> dateColumn;
    @FXML private TableColumn<Emargement, String> heureDebutColumn;
    @FXML private TableColumn<Emargement, String> heureFinColumn;
    @FXML private TableColumn<Emargement, String> statutColumn;
    @FXML private Button backButton;

    private final EmargementService service = new EmargementService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idColumn.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getId())));

        professeurColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getProfesseur() != null ? data.getValue().getProfesseur().getNomComplet() : "—"
                ));

        coursColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getCours() != null ? data.getValue().getCours().getNom() : "—"
                ));

        dateColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getDateEmargement() != null ? data.getValue().getDateEmargement().toString() : "—"
                ));

        heureDebutColumn.setCellValueFactory(data -> {
            LocalTime h = data.getValue().getHeureDebut();
            return new SimpleStringProperty(h != null ? h.toString() : "—");
        });

        heureFinColumn.setCellValueFactory(data -> {
            LocalTime h = data.getValue().getHeureFin();
            return new SimpleStringProperty(h != null ? h.toString() : "—");
        });

        statutColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getStatut() != null ? data.getValue().getStatut() : "—"));

        historiqueTable.setItems(FXCollections.observableArrayList(service.getAllEmargements()));
    }


    @FXML
    private void goBack() {
        String dashboardPath = SessionManager.getCurrentUser().getRole() == User.Role.ADMIN
                ? "/views/HistoriqueEmargement.fxml"
                : "/views/admin_dashboard.fxml"; // Fallback au cas où

        loadPage(dashboardPath);
    }

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
        } catch (NullPointerException e) {
            System.out.println("❌ Le fichier FXML n'a pas été trouvé : " + fxmlPath);
        }
    }

}
