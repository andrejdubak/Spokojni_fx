package com.example.spokojni.frontend;

import com.calendarfx.view.CalendarView;
import com.example.spokojni.backend.User;
import javafx.application.Platform;
import javafx.fxml.FXML;

public class StudentViewController {
    private User currentUser;

    @FXML
    private CalendarView calendarView;

    @FXML
    //vytvori a modifikuje kalendar pre potreby studenta
    private void buttonClick() {
        CreateCalendarView cw = new CreateCalendarView(calendarView, currentUser);
        cw.addStudentCalendar();
        cw.setStudentPopup();
        cw.disableActionForStudent();
        cw.setStudentCalendars();
    }

    @FXML
    private void saveClick() {
        Platform.exit();
        System.exit(0);
    }

    public void setCurrentUser(User user){
        this.currentUser=user;
    }
}
