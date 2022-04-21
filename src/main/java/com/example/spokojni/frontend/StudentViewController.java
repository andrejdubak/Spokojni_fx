package com.example.spokojni.frontend;

import com.calendarfx.view.CalendarView;
import com.example.spokojni.backend.User;
import javafx.fxml.FXML;

public class StudentViewController {
    private User currentUser;
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

    public void setCurrentUser(User user){
        this.currentUser=user;
    }
}
