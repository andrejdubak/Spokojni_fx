package com.example.spokojni.frontend;

import com.calendarfx.view.CalendarView;
import javafx.fxml.FXML;

public class StudentViewController {
    CreateCalendarView cw;

    @FXML
    private CalendarView calendarView;

    @FXML
    private void buttonClick() {
        cw = new CreateCalendarView(calendarView);
        cw.setStudentPopup();
    }

    @FXML
    private void saveClick() {
        System.out.println("save");
    }
}
