module com.example.angrytanks {
    requires javafx.controls;
    requires javafx.fxml;
    requires jbox2d.library;


    opens com.angrytanks to javafx.fxml;
    exports com.angrytanks;
}