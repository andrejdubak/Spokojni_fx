package com.example.spokojni.frontend;
import com.example.spokojni.backend.Term;
import com.example.spokojni.backend.User;
import com.example.spokojni.backend.db.DB;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import com.calendarfx.view.CalendarView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;

public class TeacherViewController {
    Logger logger = LogManager.getLogger(TeacherViewController.class);
    private User currentUser;
    CreateCalendarView cw;

    @FXML
    private CalendarView calendarView;

    @FXML
    private void buttonClick() {
        System.out.println(currentUser.getName());
        cw = new CreateCalendarView(calendarView, currentUser);
        cw.addTeacherHandler();
        cw.disableOtherTeachersCalendars();
        cw.setTeacherCalendars();
    }

    @FXML
    private void saveClick() {

        try {
            DB.makeConn();
        } catch (Exception var3) {
            var3.printStackTrace();
            logger.error("log_user_id:" + currentUser.getId() + "No database connection");
        }

        System.out.println("save");
        try {
            DB.makeConn();
            for (Term term : cw.getTerms()) {
                //System.out.println(term.toString());
                DB.update(term);
                logger.info("log_user_id:" + currentUser.getId() + "Term updated, id:" + term.getId());
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





