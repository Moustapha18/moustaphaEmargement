package org.example.projetdemargement.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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

public class CoursController {

    @FXML private TableView<Cours> coursTable;
    @FXML private TableColumn<Cours, Long> idColumn;
    @FXML private TableColumn<Cours, String> nomColumn;
    @FXML private TableColumn<Cours, LocalDate> dateColumn;
    @FXML private TableColumn<Cours, LocalTime> heureDebutColumn;
    @FXML private TableColumn<Cours, LocalTime> heureFinColumn;
    @FXML private TableColumn<Cours, String> salleColumn;
    @FXML private TableColumn<Cours, String> professeurColumn;

    @FXML private TextField nomField;
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


    public CoursController(TableColumn<Cours, LocalDate> dateColumn) {
        this.dateColumn = dateColumn;
    }

    @FXML
    public void initialize() {
        // Configuration des colonnes du tableau
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        heureDebutColumn.setCellValueFactory(new PropertyValueFactory<>("heureDebut"));
        heureFinColumn.setCellValueFactory(new PropertyValueFactory<>("heureFin"));

        // Affichage des noms au lieu des objets dans les colonnes Salle et Professeur
        salleColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getSalle().getLibelle()));

        professeurColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getProfesseur().getNom() + " " + cellData.getValue().getProfesseur().getPrenom()));

        // Charger les données
        loadCours();
        loadSallesAndProfesseurs();

        // Configuration des événements des boutons
        addButton.setOnAction(event -> addCours());
        updateButton.setOnAction(event -> updateCours());
        deleteButton.setOnAction(event -> deleteCours());

        // Remplissage du formulaire lors du double-clic sur un cours
        coursTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Cours selectedCours = coursTable.getSelectionModel().getSelectedItem();
                if (selectedCours != null) {
                    loadCoursToForm(selectedCours);
                }
            }
        });

        // Configuration des affichages des ComboBox pour ne pas afficher les objets bruts
        configureComboBoxDisplays();
    }

    private void loadCours() {
        Long professeurId = null;
        List<Cours> coursProfesseur = coursService.getCoursByProfesseur(null);
        coursTable.getItems().setAll(coursProfesseur);
        coursList.setAll(coursService.getAllCours());
        coursTable.setItems(coursList);
    }

    private void loadSallesAndProfesseurs() {
        // Charger les salles
        List<Salle> salles = salleService.getAllSalles();
        salleComboBox.setItems(FXCollections.observableArrayList(salles));

        // Charger uniquement les utilisateurs ayant le rôle PROFESSEUR
        List<User> professeurs = userService.getAllUsers().stream()
                .filter(user -> user.getRole() == Role.PROFESSEUR)
                .collect(Collectors.toList());
        professeurComboBox.setItems(FXCollections.observableArrayList(professeurs));
    }

    private void configureComboBoxDisplays() {
        salleComboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Salle salle, boolean empty) {
                super.updateItem(salle, empty);
                setText(empty ? "" : salle.getLibelle());
            }
        });

        salleComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Salle salle, boolean empty) {
                super.updateItem(salle, empty);
                setText(empty ? "" : salle.getLibelle());
            }
        });

        professeurComboBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                setText(empty ? "" : user.getNom() + " " + user.getPrenom());
            }
        });

        professeurComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                setText(empty ? "" : user.getNom() + " " + user.getPrenom());
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

        // Juste après avoir ajouté le cours :
        coursService.addCours(newCours);

// Envoi du mail
        String subject = "Nouveau cours assigné : " + nom;
        String body = STR."Bonjour \{professeur.getPrenom()},\n\nVous avez été affecté au cours suivant :\n📚 Nom du cours : \{nom}\n📅 Date : \{date}\n🕒 De \{heureDebut} à \{heureFin}\n🏫 Salle : \{salle.getLibelle()}\n\nMerci de vérifier votre espace personnel.\nL’équipe pédagogique.";

        EmailUtil.sendEmail(professeur.getEmail(), subject, body);


        loadCours();
        showAlert("Succès", "Cours ajouté avec succès !", Alert.AlertType.INFORMATION);
    }

    private void updateCours() {
        Cours selectedCours = coursTable.getSelectionModel().getSelectedItem();
        if (selectedCours != null) {
            selectedCours.setNom(nomField.getText());
            selectedCours.setDate(datePicker.getValue());
            selectedCours.setHeure_Debut(heureDebutSpinner.getValue());
            selectedCours.setHeure_Fin(heureFinSpinner.getValue());
            selectedCours.setSalle(salleComboBox.getValue());
            selectedCours.setProfesseur(professeurComboBox.getValue());

            coursService.updateCours(selectedCours);
            loadCours();
            showAlert("Succès", "Cours modifié avec succès !", Alert.AlertType.INFORMATION);
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

    private void loadCoursToForm(Cours cours) {
        nomField.setText(cours.getNom());
        datePicker.setValue(cours.getDate());
        heureDebutSpinner.getValueFactory().setValue(cours.getHeure_Debut());
        heureFinSpinner.getValueFactory().setValue(cours.getHeure_Fin());
        salleComboBox.setValue(cours.getSalle());
        professeurComboBox.setValue(cours.getProfesseur());
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
