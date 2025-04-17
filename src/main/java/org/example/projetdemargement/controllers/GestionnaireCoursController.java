package org.example.projetdemargement.controllers;

import javafx.beans.property.SimpleStringProperty;
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
import org.example.projetdemargement.models.Cours;
import org.example.projetdemargement.models.Role;
import org.example.projetdemargement.models.Salle;
import org.example.projetdemargement.models.User;
import org.example.projetdemargement.services.CoursService;
import org.example.projetdemargement.services.SalleService;
import org.example.projetdemargement.services.UserService;
import org.example.projetdemargement.utils.EmailUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public class GestionnaireCoursController {

    @FXML public Button backButton;
    @FXML private TableView<Cours> coursTable;
    @FXML private TableColumn<Cours, Long> idColumn;
    @FXML private TableColumn<Cours, String> nomColumn;
    @FXML private TableColumn<Cours, LocalDate> dateColumn;
    @FXML private TableColumn<Cours, LocalTime> heureDebutColumn;
    @FXML private TableColumn<Cours, LocalTime> heureFinColumn;
    @FXML private TableColumn<Cours, String> salleColumn;
    @FXML private TableColumn<Cours, String> professeurColumn;

    @FXML private TextField nomField;
    @FXML private TextField descriptionField;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<Salle> salleComboBox;
    @FXML private ComboBox<User> professeurComboBox;
    @FXML private Spinner<LocalTime> heureDebutSpinner;
    @FXML private Spinner<LocalTime> heureFinSpinner;

    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;

    private final CoursService coursService = new CoursService();
    private final SalleService salleService = new SalleService();
    private final UserService userService = new UserService();
    private final ObservableList<Cours> coursList = FXCollections.observableArrayList();
    private Cours cours;

    @FXML
    public void initialize() {
        setupTimeSpinners();
        loadCours();
        loadSallesAndProfesseurs();

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNom()));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateCours"));
        heureDebutColumn.setCellValueFactory(new PropertyValueFactory<>("heure_Debut"));
        heureFinColumn.setCellValueFactory(new PropertyValueFactory<>("heure_Fin"));
        salleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSalle().getLibelle()));
        professeurColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getProfesseur().getNom() + " " + cellData.getValue().getProfesseur().getPrenom()
        ));

        // Boutons
        addButton.setOnAction(event -> addCours());
        updateButton.setOnAction(event -> updateCours());
        deleteButton.setOnAction(event -> deleteCours());

        // Double-clic sur un élément
        coursTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && !coursTable.getSelectionModel().isEmpty()) {
                Cours selectedCours = coursTable.getSelectionModel().getSelectedItem();
                loadCoursToForm(selectedCours);
            }
        });
    }

    private void loadCoursToForm(Cours cours) {
        this.cours = cours;
        nomField.setText(cours.getNom());
        descriptionField.setText(cours.getDescription());
        datePicker.setValue(cours.getDate());
        heureDebutSpinner.getValueFactory().setValue(cours.getHeure_Debut());
        heureFinSpinner.getValueFactory().setValue(cours.getHeure_Fin());
        salleComboBox.setValue(cours.getSalle());
        professeurComboBox.setValue(cours.getProfesseur());
    }

    private void clearForm() {
        nomField.clear();
        descriptionField.clear(); // Ajouté !
        datePicker.setValue(null);
        heureDebutSpinner.getValueFactory().setValue(LocalTime.of(8, 0)); // Valeur par défaut
        heureFinSpinner.getValueFactory().setValue(LocalTime.of(10, 0));
        salleComboBox.setValue(null);
        professeurComboBox.setValue(null);
        cours = null; // Important : on réinitialise le cours en cours
    }

    @FXML
    private void handleBackButtonAction(ActionEvent event) {
        try {
            // Charger le fichier FXML du GestionnaireDashboard
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/gestionnaire_Dashboard.fxml"));
            Parent root = loader.load();

            // Obtenir la scène actuelle et la remplacer par GestionnaireDashboard
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (java.io.IOException e) {
            e.printStackTrace(); // Affiche l'erreur dans la console en cas de problème
        }
    }
    private void setupTimeSpinners() {
        heureDebutSpinner.setValueFactory(new SpinnerValueFactory<LocalTime>() {
            {
                setValue(LocalTime.of(8, 0)); // Heure de départ par défaut
            }

            @Override
            public void decrement(int steps) {
                setValue(getValue().minusMinutes(30)); // Diminue par 30 min
            }

            @Override
            public void increment(int steps) {
                setValue(getValue().plusMinutes(30)); // Augmente par 30 min
            }
        });

        heureFinSpinner.setValueFactory(new SpinnerValueFactory<LocalTime>() {
            {
                setValue(LocalTime.of(10, 0)); // Heure de départ par défaut
            }

            @Override
            public void decrement(int steps) {
                setValue(getValue().minusMinutes(30));
            }

            @Override
            public void increment(int steps) {
                setValue(getValue().plusMinutes(30));
            }
        });

        // Permet d'écrire manuellement l'heure dans le Spinner
        heureDebutSpinner.setEditable(true);
        heureFinSpinner.setEditable(true);
    }


    private void loadCours() {
        coursList.setAll(coursService.getAllCours());
        coursTable.setItems(coursList);
    }

    private void loadSallesAndProfesseurs() {
        List<Salle> salles = salleService.getAllSalles();
        List<User> professeurs = userService.getAllUsers().stream()
                .filter(user -> user.getRole() == Role.PROFESSEUR)
                .collect(Collectors.toList());

        // 📌 Vérification avant affichage dans les ComboBox
        System.out.println("📂 Salles chargées : " + salles);
        System.out.println("👨‍🏫 Professeurs chargés : " + professeurs);

        salleComboBox.setItems(FXCollections.observableArrayList(salles));
        professeurComboBox.setItems(FXCollections.observableArrayList(professeurs));

        // ✅ Afficher uniquement le libellé de la salle et le nom du professeur
        salleComboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Salle salle, boolean empty) {
                super.updateItem(salle, empty);
                setText(empty ? "" : salle.getLibelle());
            }
        });

        professeurComboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(User prof, boolean empty) {
                super.updateItem(prof, empty);
                setText(empty ? "" : prof.getNom() + " " + prof.getPrenom());
            }
        });

        // Pour afficher directement le bon texte après la sélection
        salleComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Salle salle, boolean empty) {
                super.updateItem(salle, empty);
                setText(empty ? "" : salle.getLibelle());
            }
        });

        professeurComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(User prof, boolean empty) {
                super.updateItem(prof, empty);
                setText(empty ? "" : prof.getNom() + " " + prof.getPrenom());
            }
        });
    }



    private void addCours() {
        String nom = nomField.getText();
        LocalDate date = datePicker.getValue();
        LocalTime heureDebut = heureDebutSpinner.getValue();
        LocalTime heureFin = heureFinSpinner.getValue();
        Salle salle = salleComboBox.getValue();
        User professeur = professeurComboBox.getValue();

        if (nom.isEmpty() || date == null || heureDebut == null || heureFin == null || salle == null || professeur == null) {
            showAlert("Erreur", "Tous les champs doivent être remplis !", Alert.AlertType.ERROR);
            return;
        }

        // Affichage debug
        System.out.println("🧪 Vérification de conflit...");
        System.out.println("Cours : " + nom);
        System.out.println("Salle ID : " + salle.getId());
        System.out.println("Date : " + date);
        System.out.println("Heure début : " + heureDebut);
        System.out.println("Heure fin : " + heureFin);

        Cours newCours = new Cours(nom, date, heureDebut, heureFin, "", salle, professeur);

        if (coursService.hasScheduleConflict(newCours)) {
            showAlert("Conflit horaire", "⚠️ La salle est déjà occupée à cet horaire !", Alert.AlertType.WARNING);
            return;
        }
        if (coursService.hasProfesseurConflict(newCours)) {
            showAlert("Conflit horaire", "⚠️ Ce professeur a déjà un cours à cet horaire !", Alert.AlertType.WARNING);
            return;
        }


        coursService.addCours(newCours);

        // Mail
        String subject = "Nouveau cours assigné : " + nom;
        String body = "Bonjour " + professeur.getPrenom() + ",\n\n"
                + "Vous avez été assigné au cours suivant :\n"
                + "Nom : " + nom + "\n"
                + "Date : " + date + "\n"
                + "Heure : " + heureDebut + " - " + heureFin + "\n"
                + "Salle : " + salle.getLibelle() + "\n\n"
                + "Cordialement,\nL'équipe Emargement";

        try {
            EmailUtil.sendEmail(professeur.getEmail(), subject, body);
            System.out.println("📨 Mail envoyé !");
        } catch (Exception e) {
            System.err.println("❌ Erreur mail : " + e.getMessage());
        }

        loadCours();
        showAlert("Succès", "Cours ajouté avec succès !", Alert.AlertType.INFORMATION);
        clearForm();
    }


    private void updateCours() {
        Cours selectedCours = coursTable.getSelectionModel().getSelectedItem();
        if (selectedCours != null) {
            selectedCours.setNom(nomField.getText());
            selectedCours.setDateCours(datePicker.getValue());
            selectedCours.setHeure_Debut(heureDebutSpinner.getValue());
            selectedCours.setHeure_Fin(heureFinSpinner.getValue());
            selectedCours.setDescription(descriptionField.getText());
            selectedCours.setSalle(salleComboBox.getValue());
            selectedCours.setProfesseur(professeurComboBox.getValue());

            coursService.updateCours(selectedCours);
            loadCours();
            showAlert("Succès", "Cours mis à jour !", Alert.AlertType.INFORMATION);
        }
    }

    private void deleteCours() {
        Cours selectedCours = coursTable.getSelectionModel().getSelectedItem();
        if (selectedCours != null) {
            coursService.deleteCours(selectedCours.getId());
            loadCours();
            showAlert("Succès", "Cours supprimé !", Alert.AlertType.INFORMATION);
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/admin_dashboard.fxml"));
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
