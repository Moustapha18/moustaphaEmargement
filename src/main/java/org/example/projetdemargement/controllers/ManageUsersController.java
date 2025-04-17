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
import org.example.projetdemargement.models.Role;
import org.example.projetdemargement.models.User;
import org.example.projetdemargement.services.UserService;

import java.util.List;

public class ManageUsersController {

    public Button backButton;
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Long> idColumn;
    @FXML private TableColumn<User, String> nomColumn;
    @FXML private TableColumn<User, String> prenomColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> roleColumn;

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<Role> roleComboBox;

    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;

    private final UserService userService = new UserService();
    private final ObservableList<User> userList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Charger les rôles dans la ComboBox
        roleComboBox.setItems(FXCollections.observableArrayList(Role.values()));

        // Associer les colonnes aux propriétés des objets User
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        // Charger la liste des utilisateurs
        loadUsers();

        // Configuration des événements des boutons
        addButton.setOnAction(event -> addUser());
        updateButton.setOnAction(event -> updateUser());
        deleteButton.setOnAction(event -> deleteUser());

        // Gestion du double-clic sur un utilisateur pour charger les infos
        userTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                User selectedUser = userTable.getSelectionModel().getSelectedItem();
                if (selectedUser != null) {
                    loadUserToForm(selectedUser);
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

            // Obtenir la scène actuelle et la remplacer par admin_Dashboard
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (java.io.IOException e) {
            e.printStackTrace(); // Affiche l'erreur dans la console en cas de problème
        }
    }


    private void loadUsers() {
        List<User> users = userService.getAllUsers();
        userList.setAll(users);
        userTable.setItems(userList);
        userTable.refresh();  // 🔄 Rafraîchir la TableView
    }

    private void addUser() {
        String nom = nomField.getText();
        String prenom = prenomField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        Role role = roleComboBox.getValue();

        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || password.isEmpty() || role == null) {
            showAlert("Erreur", "Tous les champs doivent être remplis !", Alert.AlertType.ERROR);
            return;
        }

        User newUser = new User(null, nom, prenom, email, password, role);

        // Ajout de l'utilisateur en base
        userService.addUser(newUser);

        // 🆕 Ajouter directement à la TableView sans tout recharger
        userList.add(newUser);
        userTable.setItems(userList);
        userTable.refresh();  // 🔄 Forcer l'affichage

        showAlert("Succès", "Utilisateur ajouté avec succès !", Alert.AlertType.INFORMATION);
        clearForm();
    }

    private void updateUser() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            selectedUser.setNom(nomField.getText());
            selectedUser.setPrenom(prenomField.getText());
            selectedUser.setEmail(emailField.getText());
            selectedUser.setPassword(passwordField.getText());
            selectedUser.setRole(roleComboBox.getValue());

            userService.updateUser(selectedUser);
            userTable.refresh();  // 🔄 Rafraîchir l'affichage après mise à jour

            showAlert("Succès", "Utilisateur mis à jour avec succès !", Alert.AlertType.INFORMATION);
            clearForm();
        } else {
            showAlert("Erreur", "Sélectionnez un utilisateur à modifier !", Alert.AlertType.ERROR);
        }
    }

    private void deleteUser() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            userService.deleteUser(selectedUser.getId());
            userList.remove(selectedUser);  // ✅ Supprimer de la TableView directement
            userTable.setItems(userList);
            userTable.refresh();

            showAlert("Succès", "Utilisateur supprimé avec succès !", Alert.AlertType.INFORMATION);
            clearForm();
        } else {
            showAlert("Erreur", "Sélectionnez un utilisateur à supprimer !", Alert.AlertType.ERROR);
        }
    }

    private void loadUserToForm(User user) {
        nomField.setText(user.getNom());
        prenomField.setText(user.getPrenom());
        emailField.setText(user.getEmail());
        passwordField.setText(user.getPassword());
        roleComboBox.setValue(user.getRole());
    }

    private void clearForm() {
        nomField.clear();
        prenomField.clear();
        emailField.clear();
        passwordField.clear();
        roleComboBox.setValue(null);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void goBack(ActionEvent event) {
        try {
            // Charger la page du tableau de bord administrateur
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/admin_dashboard.fxml"));
            Parent root = loader.load();

            // Obtenir la scène actuelle et remplacer le contenu
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (java.io.IOException e) {
            e.printStackTrace();
            System.out.println("❌ Erreur lors du retour au dashboard administrateur.");
        }
    }

}
