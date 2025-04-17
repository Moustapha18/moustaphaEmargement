package org.example.projetdemargement;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.projetdemargement.utils.SceneSwitcher;
import org.kordamp.bootstrapfx.BootstrapFX;

public class MainApp extends Application {

    public static Stage primaryStage; // 👈 accessible globalement

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        stage.setTitle("Authentification");

        // Charger la scène initiale avec taille par défaut
        SceneSwitcher.switchScene(stage, "/views/login.fxml");

        // Activer BootstrapFX
        stage.getScene().getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
