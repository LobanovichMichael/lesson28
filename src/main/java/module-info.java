module org.example.lesson28 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens org.example.lesson28 to javafx.fxml;
    exports org.example.lesson28;
}