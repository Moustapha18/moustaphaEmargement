package org.example.projetdemargement.controllers;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.lowagie.text.Document;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.projetdemargement.models.Emargement;
import org.example.projetdemargement.models.User;
import org.example.projetdemargement.services.EmargementService;
import org.example.projetdemargement.utils.SessionManager;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class HistoriqueEmargementController {

    @FXML private Button exportExcelButton;
    @FXML private Button exportPdfButton;
    @FXML private TableView<Emargement> historiqueTable;
    @FXML private TableColumn<Emargement, String> dateColumn;
    @FXML private TableColumn<Emargement, String> coursColumn;
    @FXML private TableColumn<Emargement, String> professeurColumn;
    @FXML private TableColumn<Emargement, String> statutColumn;
    @FXML private Button backButton;

    private final EmargementService emargementService = new EmargementService();

    @FXML
    public void initialize() {
        dateColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDateEmargement() != null ? data.getValue().getDateEmargement().toString() : ""));
        coursColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCours() != null ? data.getValue().getCours().getNom() : ""));
        professeurColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getProfesseur() != null ? data.getValue().getProfesseur().getNomComplet() : ""));
        statutColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getStatut() != null ? data.getValue().getStatut() : ""));

        loadHistorique();

        backButton.setOnAction(event -> goBack());
        exportPdfButton.setOnAction(event -> exporterPDF());
        exportExcelButton.setOnAction(event -> exporterExcel());
    }

    private void exporterExcel() {
        List<Emargement> emargements = EmargementService.getAllEmargements()
                .stream()
                .filter(e -> "Validé".equalsIgnoreCase(e.getStatut()))
                .toList();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le fichier Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers Excel", "*.xlsx"));
        fileChooser.setInitialFileName("emargements_valides.xlsx");

        Stage stage = (Stage) exportExcelButton.getScene().getWindow();
        java.io.File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Émargements Validés");

                Row header = sheet.createRow(0);
                header.createCell(0).setCellValue("Date");
                header.createCell(1).setCellValue("Cours");
                header.createCell(2).setCellValue("Professeur");
                header.createCell(3).setCellValue("Statut");

                int rowNum = 1;
                for (Emargement e : emargements) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(e.getDateEmargement().toString());
                    row.createCell(1).setCellValue(e.getCours().getNom());
                    row.createCell(2).setCellValue(e.getProfesseur().getNomComplet());
                    row.createCell(3).setCellValue(e.getStatut());
                }

                try (FileOutputStream fileOut = new FileOutputStream(file)) {
                    workbook.write(fileOut);
                }

                System.out.println("✅ Fichier Excel exporté avec succès !");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("❌ Erreur lors de l'exportation Excel.");
            }
        }
    }

    private void exporterPDF() {
        List<Emargement> emargements = historiqueTable.getItems()
                .stream()
                .filter(e -> "Validé".equalsIgnoreCase(e.getStatut()))
                .toList();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le fichier PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));
        fileChooser.setInitialFileName("emargements_valides.pdf");

        Stage stage = (Stage) exportPdfButton.getScene().getWindow();
        java.io.File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            Document document = new Document();
            try {
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();

                document.add(new Paragraph("Liste des émargements validés", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
                document.add(new Paragraph(" ")); // espace

                PdfPTable table = new PdfPTable(4);
                table.addCell("Date");
                table.addCell("Cours");
                table.addCell("Professeur");
                table.addCell("Statut");

                for (Emargement e : emargements) {
                    table.addCell(e.getDateEmargement().toString());
                    table.addCell(e.getCours().getNom());
                    table.addCell(e.getProfesseur().getNomComplet());
                    table.addCell(e.getStatut());
                }

                document.add(table);
                System.out.println("✅ PDF exporté avec succès !");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("❌ Erreur lors de l'exportation PDF.");
            } finally {
                document.close();
            }
        }
    }

    private void loadHistorique() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser != null) {
            if (currentUser.getRole() == User.Role.PROFESSEUR) {
                loadHistoriqueProfesseur(currentUser.getId());
            } else {
                loadHistoriqueAdmin();
            }
        }
    }

    public void loadHistoriqueProfesseur(Long professeurId) {
        List<Emargement> historique = EmargementService.getHistoriqueEmargements(professeurId);
        historiqueTable.setItems(FXCollections.observableArrayList(historique));
    }

    public void loadHistoriqueAdmin() {
        List<Emargement> historique = EmargementService.getAllEmargements();
        historiqueTable.setItems(FXCollections.observableArrayList(historique));
    }

    @FXML
    private void goBack() {
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
