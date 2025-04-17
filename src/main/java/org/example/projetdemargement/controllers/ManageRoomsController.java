package org.example.projetdemargement.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.projetdemargement.models.Salle;
import org.example.projetdemargement.services.SalleService;

import java.util.List;

public class ManageRoomsController {

    @FXML private TableView<Salle> roomTable;
    @FXML private TableColumn<Salle, Long> idColumn;
    @FXML private TableColumn<Salle, String> libelleColumn;

    @FXML private TextField libelleField;

    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML
    private Button backButton;
    private final SalleService salleService = new SalleService();
    private final ObservableList<Salle> roomList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Configuration des colonnes
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        libelleColumn.setCellValueFactory(new PropertyValueFactory<>("libelle"));

        // Charger les salles
        loadRooms();

        // Événements des boutons
        addButton.setOnAction(event -> addRoom());
        updateButton.setOnAction(event -> updateRoom());
        deleteButton.setOnAction(event -> deleteRoom());

        // Sélection d'une salle en double-cliquant pour modification
        roomTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Salle selectedRoom = roomTable.getSelectionModel().getSelectedItem();
                if (selectedRoom != null) {
                    loadRoomToForm(selectedRoom);
                }
            }
        });
    }




    @FXML
    private void handleBackButtonAction(ActionEvent event) {
        try {
            // Charger le fichier FXML de l'admin_Dashboard
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/admin_Dashboard.fxml"));
            Parent root = loader.load();

            // Obtenir la scène actuelle et la remplacer par la nouvelle
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (java.io.IOException e) {
            e.printStackTrace(); // Affiche l'erreur dans la console en cas de problème
        }
    }


    private void loadRooms() {
        List<Salle> rooms = salleService.getAllSalles();
        roomList.setAll(rooms);
        roomTable.setItems(roomList);
    }

    private void addRoom() {
        String libelle = libelleField.getText();

        if (libelle.isEmpty()) {
            showAlert("Erreur", "Le nom de la salle ne peut pas être vide !", Alert.AlertType.ERROR);
            return;
        }

        Salle newRoom = new Salle(null, libelle);
        salleService.addSalle(newRoom);
        loadRooms();
        showAlert("Succès", "Salle ajoutée avec succès !", Alert.AlertType.INFORMATION);
        clearForm();
    }

    private void updateRoom() {
        Salle selectedRoom = roomTable.getSelectionModel().getSelectedItem();
        if (selectedRoom != null) {
            selectedRoom.setLibelle(libelleField.getText());

            salleService.updateSalle(selectedRoom);
            loadRooms();
            showAlert("Succès", "Salle mise à jour avec succès !", Alert.AlertType.INFORMATION);
        }
    }

    private void deleteRoom() {
        Salle selectedRoom = roomTable.getSelectionModel().getSelectedItem();
        if (selectedRoom != null) {
            salleService.deleteSalle(selectedRoom.getId());
            loadRooms();
            showAlert("Succès", "Salle supprimée avec succès !", Alert.AlertType.INFORMATION);
        }
    }

    private void loadRoomToForm(Salle salle) {
        libelleField.setText(salle.getLibelle());
    }

    private void clearForm() {
        libelleField.clear();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void goBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/gestionnaire_dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (java.io.IOException e) {
            e.printStackTrace();
            System.out.println("❌ Erreur lors du retour au dashboard administrateur.");
        }
    }
}
