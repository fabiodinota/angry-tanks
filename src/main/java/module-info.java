module com.example.angrytanks {
    requires javafx.controls;
    requires javafx.fxml;
    requires jbox2d.library;


    opens com.example.angrytanks to javafx.fxml;
    exports com.example.angrytanks;
}