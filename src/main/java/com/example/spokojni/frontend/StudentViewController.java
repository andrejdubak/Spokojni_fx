package com.example.spokojni.frontend;

import com.calendarfx.view.CalendarView;
import com.example.spokojni.backend.User;
import javafx.fxml.FXML;

public class StudentViewController {
    private User currentUser;

    @FXML
    private CalendarView calendarView;

    @FXML
    private void buttonClick() {
        CreateCalendarView cw = new CreateCalendarView(calendarView, currentUser);
        cw.addStudentCalendar();
        cw.setStudentPopup();
        cw.disableActionForStudent();
        cw.setStudentCalendars();
    }

    @FXML
    private void saveClick() {
        System.out.println("save");
    }

    public void setCurrentUser(User user){
        this.currentUser=user;
    }
}
