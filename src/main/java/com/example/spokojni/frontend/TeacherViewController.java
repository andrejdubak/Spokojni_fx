package com.example.spokojni.frontend;
import com.example.spokojni.backend.Term;
import com.example.spokojni.backend.User;
import com.example.spokojni.backend.db.DB;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import com.calendarfx.view.CalendarView;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;

public class TeacherViewController {
    private User currentUser;
    CreateCalendarView cw;

    @FXML
    private CalendarView calendarView;

    @FXML
    private Button languageEN, languageSK;

    @FXML
    private void buttonClick() {
        System.out.println(currentUser.getName());
        cw = new CreateCalendarView(calendarView, currentUser);
        cw.addTeacherHandler();
        cw.disableOtherTeachersCalendars();
        cw.setTeacherCalendars();
    }

    @FXML
    private void languageENClick() throws IOException {
        new ChangeWindowController( "teacher-view.fxml", new Locale("en", "UK")).changeWindow(languageEN);
    }

    @FXML
    private void languageSKClick() throws IOException {
        new ChangeWindowController( "teacher-view.fxml", new Locale("sk", "SK")).changeWindow(languageSK);
    }

    @FXML
    private void saveClick() {
        System.out.println("save");
        try {
            DB.makeConn();
            for (Term term : cw.getTerms()) {
                //System.out.println(term.toString());
                DB.update(term);
            }

            for (Term term : cw.getNew_terms()) {
                System.out.println(term.toString());
                DB.add(term);
            }
            for (Term term : cw.getTerms_to_del()) {
                System.out.println("delete term id: " + term.getId());
                // DB.delete(term.getId()); //TODO spravit funkciu na deletovanie termov
            }
        } catch (SQLException var2) {
            System.out.println("SQLException: " + var2.getMessage());
            System.out.println("SQLState: " + var2.getSQLState());
            System.out.println("VendorError: " + var2.getErrorCode());
            var2.printStackTrace();
        } catch (Exception var3) {
            var3.printStackTrace();
        }
    }
    public void setCurrentUser(User user){
        this.currentUser=user;
    }
}





