package com.example.spokojni.frontend;

import com.calendarfx.view.CalendarView;
import com.example.spokojni.backend.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;

public class StudentViewController {
    private User currentUser;

    @FXML
    private CalendarView calendarView;

    @FXML
    private Button loadButton;

    @FXML
    //vytvori a modifikuje kalendar pre potreby studenta
    private void buttonClick() {
        CreateCalendarView cw = new CreateCalendarView(calendarView, currentUser);
        cw.addStudentCalendar();
        cw.setStudentPopup();
        cw.disableActionForStudent();
        cw.setStudentCalendars();
        loadButton.setDisable(true);
    }

    @FXML
    private void saveClick() {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    private void changePassword(ActionEvent actionEvent) throws IOException {
        new ChangePassword(currentUser);
    }

    public void setCurrentUser(User user){
        this.currentUser=user;
    }
}
