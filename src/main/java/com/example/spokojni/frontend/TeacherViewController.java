package com.example.spokojni.frontend;
import com.example.spokojni.backend.Term;
import com.example.spokojni.backend.User;
import com.example.spokojni.backend.db.DB;
import javafx.fxml.FXML;
import com.calendarfx.view.CalendarView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.SQLException;

public class TeacherViewController {
    private Logger logger = LogManager.getLogger(TeacherViewController.class);
    private User currentUser;
    private CreateCalendarView cw;

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
            logger.error("log_user_id:" + currentUser.getId() + "No database connection" + var3);
        }

        try {
            DB.makeConn();
            for (Term term : cw.getTerms()) {
                DB.update(term);
                logger.info("log_user_id:" + currentUser.getId() + "Term updated, id:" + term.getId());
            }

            for (Term term : cw.getNew_terms()) {
                DB.add(term);
                logger.info("log_user_id:" + currentUser.getId() + "Term added, id:" + term.getId());
            }
            for (Term term : cw.getTerms_to_del()) {
                logger.info("log_user_id:" + currentUser.getId() + "Term deleted, id:" + term.getId());
                DB.delete(term.getId());
            }
        } catch (SQLException var2) {
            System.out.println("SQLException: " + var2.getMessage());
            System.out.println("SQLState: " + var2.getSQLState());
            System.out.println("VendorError: " + var2.getErrorCode());
            logger.warn("Cannot save" + var2);
            var2.printStackTrace();
        } catch (Exception var3) {
            var3.printStackTrace();
            logger.error("Cannot finish operation" + var3);
        }
    }
    public void setCurrentUser(User user){
        this.currentUser=user;
    }
}





