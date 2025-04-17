module org.example.projetdemargement {
    requires java.persistence;
    requires org.hibernate.orm.core;
    requires java.sql;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires javafx.media;
    requires org.postgresql.jdbc;
    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires jakarta.persistence;
    requires static lombok;
    requires org.kordamp.ikonli.fontawesome5;
    requires jbcrypt;
    requires jakarta.transaction;
    requires jakarta.inject;
    requires java.desktop;
    requires com.github.librepdf.openpdf;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires jakarta.mail;

    opens org.example.projetdemargement.models to javafx.base, org.hibernate.orm.core, jakarta.inject;
    opens org.example.projetdemargement to javafx.graphics, javafx.fxml;
    opens org.example.projetdemargement.controllers to javafx.fxml; // ✅ Ajout nécessaire
}