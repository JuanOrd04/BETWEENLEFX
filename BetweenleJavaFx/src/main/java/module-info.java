module com.example.betweenlejavafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.betweenlejavafx to javafx.fxml;
    exports com.example.betweenlejavafx;
}