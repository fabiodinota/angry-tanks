module com.example.angrytanks {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.angrytanks to javafx.fxml;
    exports com.example.angrytanks;
}