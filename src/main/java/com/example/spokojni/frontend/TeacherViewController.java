package com.example.spokojni.frontend;
import com.example.spokojni.backend.Term;
import com.example.spokojni.backend.User;
import com.example.spokojni.backend.db.DB;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import com.calendarfx.view.CalendarView;
import javafx.scene.control.Button;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;

public class TeacherViewController {
    private Logger logger = LogManager.getLogger(TeacherViewController.class);
    private User currentUser;
    private CreateCalendarView cw;

    @FXML
    private CalendarView calendarView;

    @FXML
    private Button loadButton;

    @FXML
    //funkcia vytvori instanciu kalendara a prida jej potrebne veci pre fungovanie teacher obrazovky
    private void buttonClick() {
        cw = new CreateCalendarView(calendarView, currentUser);
        cw.addTeacherHandler();
        cw.disableOtherTeachersCalendars();
        cw.setTeacherCalendars();
        loadButton.setDisable(true);
    }

    @FXML
    //ulozi modifikovane zaznamy do databazy
    private void saveClick() {
        try {
            DB.makeConn();
            //aktualizuje existujuce
            for (Term term : cw.getTerms()) {
                DB.update(term);
            }
            //prida novo vytvorene
            for (Term term : cw.getNew_terms()) {
                DB.add(term);
            }
            //vymaze z existujucich
            for (Term term : cw.getTerms_to_del()) {
                DB.delete(term.getId());
            }
            logger.info("log_user_id:" + currentUser.getId() + "Save and exit");
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
        Platform.exit();
        System.exit(0);
    }

    @FXML
    private void exitClick() {
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





