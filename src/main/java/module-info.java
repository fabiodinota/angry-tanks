module com.example.angrytanks {
    requires javafx.controls;
    requires javafx.fxml;
    requires jbox2d.library;
    requires java.desktop;
    requires org.locationtech.jts;
    requires java.sql;
    requires org.postgresql.jdbc;

    opens com.angrytanks.ui.controllers to javafx.fxml, javafx.base;

    exports com.angrytanks.ui.controllers;
    exports com.angrytanks;
}