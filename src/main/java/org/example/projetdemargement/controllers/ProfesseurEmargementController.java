package org.example.projetdemargement.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.example.projetdemargement.models.Cours;
import org.example.projetdemargement.models.User;
import org.example.projetdemargement.services.CoursService;
import org.example.projetdemargement.services.EmargementService;
import org.example.projetdemargement.utils.SessionManager;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class ProfesseurEmargementController implements Initializable {

    @FXML private TableView<Cours> coursTable;
    @FXML private TableColumn<Cours, String> idColumn;
    @FXML private TableColumn<Cours, String> nomColumn;
    @FXML private TableColumn<Cours, String> heureDebutColumn;
    @FXML private TableColumn<Cours, String> heureFinColumn;
    @FXML private TableColumn<Cours, String> salleColumn;
    @FXML private TableColumn<Cours, String> statutColumn;
    @FXML private javafx.scene.control.Button backButton;


    @FXML private Label statusLabel;

    private final CoursService coursService = new CoursService();
    private final EmargementService emargementService = new EmargementService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getId())));
        nomColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNom()));
        heureDebutColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getHeure_Debut().toString()));
        heureFinColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getHeure_Fin().toString()));
        salleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSalle().getLibelle()));
        statutColumn.setCellValueFactory(data -> new SimpleStringProperty("En attente"));

        loadCours();

        // ✅ Planification du marquage d'absence automatique
        Long professeurId = SessionManager.getCurrentUser().getId();
        List<Cours> coursProfesseur = coursService.getCoursByProfesseur(professeurId);

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        for (Cours cours : coursProfesseur) {
            LocalDateTime coursStart = LocalDateTime.of(cours.getDate(), cours.getHeure_Debut());
            LocalDateTime deadline = coursStart.plusMinutes(30);
            long delay = Duration.between(LocalDateTime.now(), deadline).toMillis();

            if (delay > 0) {
                scheduler.schedule(() -> {
                    boolean alreadyEmarged = emargementService.hasAlreadyEmarged(professeurId, cours.getId());
                    if (!alreadyEmarged) {
                        emargementService.marquerAbsent(professeurId, cours.getId());
                        System.out.println("⏰ Professeur marqué absent automatiquement pour : " + cours.getNom());
                    }
                }, delay, TimeUnit.MILLISECONDS);
            } else {
                // Si le cours est déjà passé de +30 min mais pas encore émargé
                boolean alreadyEmarged = emargementService.hasAlreadyEmarged(professeurId, cours.getId());
                if (!alreadyEmarged) {
                    emargementService.marquerAbsent(professeurId, cours.getId());
                    System.out.println("⏰ Marquage immédiat d'absence pour le cours déjà passé : " + cours.getNom());
                }
            }
        }
    }

    private void loadCours() {
        Long professeurId = SessionManager.getCurrentUser().getId();
        List<Cours> coursProfesseur = coursService.getCoursByProfesseur(professeurId);

        if (coursProfesseur.isEmpty()) {
            statusLabel.setText("Aucun cours assigné.");
        } else {
            coursTable.setItems(FXCollections.observableArrayList(coursProfesseur));
        }
    }

    @FXML
    private void handleEmargement() {
        Cours coursSelectionne = coursTable.getSelectionModel().getSelectedItem();

        if (coursSelectionne == null) {
            showAlert("Erreur", "Veuillez sélectionner un cours à émarger.", Alert.AlertType.ERROR);
            return;
        }

        Long professeurId = SessionManager.getCurrentUser().getId();

        if (emargementService.hasAlreadyEmarged(professeurId, coursSelectionne.getId())) {
            showAlert("Info", "Vous avez déjà émargé ce cours.", Alert.AlertType.INFORMATION);
            return;
        }

        boolean success = emargementService.enregistrerEmargement(professeurId, coursSelectionne.getId());

        if (success) {
            showAlert("Succès", "Émargement enregistré avec succès !", Alert.AlertType.INFORMATION);
            statusLabel.setText("Émargement effectué !");
        } else {
            showAlert("Erreur", "Échec de l'émargement.", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void goBack(ActionEvent event) {
        String dashboardPath = SessionManager.getCurrentUser().getRole() == User.Role.PROFESSEUR
                ? "/views/professeur_dashboard.fxml"
                : "/views/admin_dashboard.fxml";
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
        }
    }
}
