module com.example.spokojni {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.calendarfx.view;
    requires mysql.connector.java;
    requires java.prefs;
    requires java.desktop;
    requires org.apache.logging.log4j;


    opens com.example.spokojni to javafx.fxml;
    exports com.example.spokojni;
    exports com.example.spokojni.frontend;
    opens com.example.spokojni.frontend to javafx.fxml;
    opens  com.example.spokojni.backend to javafx.base;
}