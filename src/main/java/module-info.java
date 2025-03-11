module com.example.angrytanks {
    requires javafx.controls;
    requires javafx.fxml;
    requires jbox2d.library;
    requires java.desktop;
    requires org.locationtech.jts;


    opens com.angrytanks.ui.controllers to javafx.fxml;
    exports com.angrytanks;
}