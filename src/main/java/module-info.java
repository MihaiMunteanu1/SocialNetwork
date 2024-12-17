module org.example.llab {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens org.example.llab67 to javafx.fxml;
    //opens org.example.llab67.controller to javafx.fxml

    exports org.example.llab67;
}

