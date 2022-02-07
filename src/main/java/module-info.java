module com.example.imagesorter {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.imagesorter to javafx.fxml;
    exports com.imagesorter;
}