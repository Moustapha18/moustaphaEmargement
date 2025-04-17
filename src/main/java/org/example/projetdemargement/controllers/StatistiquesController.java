package org.example.projetdemargement.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.example.projetdemargement.models.Emargement;
import org.example.projetdemargement.services.EmargementService;
import org.example.projetdemargement.utils.SessionManager;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class StatistiquesController {

    @FXML
    private Button backButton;

    @FXML
    private PieChart emargementChart;

    private final EmargementService emargementService = new EmargementService();
    @FXML
    private BarChart<String, Number> barChart;

    @FXML
    private LineChart<String, Number> lineChart;

    @FXML
    public void initialize() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Émargements");

        // Exemple de données; remplacez par vos données réelles
        Map<String, Integer> emargementsParPeriode = getEmargementsParPeriode();

        for (Map.Entry<String, Integer> entry : emargementsParPeriode.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        lineChart.getData().add(series);

        chargerStatistiques();
        backButton.setOnAction(event -> goBack());
        series = new XYChart.Series<>();
        series.setName("Émargements");

        // Exemple de données; remplacez par vos données réelles
        Map<String, Integer> emargementsParProf = getEmargementsParProfesseur();

        for (Map.Entry<String, Integer> entry : emargementsParProf.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        barChart.getData().add(series);
    }

    private Map<String, Integer> getEmargementsParPeriode() {
        Map<String, Integer> stats = new TreeMap<>();
        List<Emargement> emargements = emargementService.getAllEmargements();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");

        for (Emargement e : emargements) {
            String dateStr = e.getDateEmargement().format(formatter);
            stats.put(dateStr, stats.getOrDefault(dateStr, 0) + 1);
        }
        return stats;
    }


    private Map<String, Integer> getEmargementsParProfesseur() {
        Map<String, Integer> stats = new HashMap<>();
        List<Emargement> emargements = emargementService.getAllEmargements();
        for (Emargement e : emargements) {
            String prof = e.getProfesseur().getNom();
            stats.put(prof, stats.getOrDefault(prof, 0) + 1);
        }
        return stats;
    }


    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/admin_dashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backButton.getScene().getWindow(); // <- ici le bouton doit exister
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("❌ Erreur lors du retour au menu admin");
        }
    }

    private void chargerStatistiques() {
        List<Emargement> emargements = emargementService.getAllEmargements();

        long valides = emargements.stream().filter(e -> "Validé".equalsIgnoreCase(e.getStatut())).count();
        long rejetes = emargements.stream().filter(e -> "Rejeté".equalsIgnoreCase(e.getStatut())).count();
        long enAttente = emargements.stream().filter(e -> "En attente".equalsIgnoreCase(e.getStatut())).count();

        ObservableList<PieChart.Data> data = FXCollections.observableArrayList(
                new PieChart.Data("Validé", valides),
                new PieChart.Data("Rejeté", rejetes),
                new PieChart.Data("En attente", enAttente)
        );

        emargementChart.setData(data);
        emargementChart.setTitle("Répartition des Émargements");
    }

    private void revenirDashboard() {
        String dashboard = SessionManager.getCurrentUser().getRole() == org.example.projetdemargement.models.User.Role.ADMIN
                ? "/views/admin_dashboard.fxml"
                : "/views/professeur_dashboard.fxml";

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(dashboard));
            Parent root = loader.load();
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("❌ Erreur lors du retour au tableau de bord.");
        }
    }
}
