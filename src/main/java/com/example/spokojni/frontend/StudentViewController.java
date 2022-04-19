package com.example.spokojni.frontend;

import com.calendarfx.view.CalendarView;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;

public class StudentViewController {
    @FXML
    private CalendarView calendarView;

    @FXML
    private Button saveButton;

    public StudentViewController() {
        System.out.println("const");
    }

    @FXML
    protected void saveClick() throws IOException {
        System.out.println("save");
    }
}
