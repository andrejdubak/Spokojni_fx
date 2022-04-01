module com.example.spokojni {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.calendarfx.view;


    opens com.example.spokojni to javafx.fxml;
    exports com.example.spokojni;
    exports com.example.spokojni.frontend;
    opens com.example.spokojni.frontend to javafx.fxml;
}