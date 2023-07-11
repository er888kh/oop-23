module com.example.snapfoodgui {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.edgedb.driver;
    requires io.github.cdimascio.dotenv.java;
    requires org.slf4j;

    opens com.example.snapfoodgui to javafx.fxml;
    exports com.example.snapfoodgui;
}