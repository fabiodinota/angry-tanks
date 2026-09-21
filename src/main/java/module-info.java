module com.example.angrytanks {
  requires javafx.controls;
  requires javafx.fxml;
  requires jbox2d.library;
  requires java.desktop;
  requires org.locationtech.jts;
  requires java.sql;
  requires org.xerial.sqlitejdbc;

  opens com.angrytanks.view.fx.screen to
      javafx.fxml,
      javafx.base;

  exports com.angrytanks.view.fx.screen;
  exports com.angrytanks;
}
