package com.example.spokojni.frontend;
import com.example.spokojni.backend.Term;
import com.example.spokojni.backend.db.DB;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import com.calendarfx.view.CalendarView;

import java.sql.SQLException;

public class TeacherViewController {
    CreateCalendarView cw;

    @FXML
    private CalendarView calendarView;

    @FXML
    private void buttonClick() {
        cw = new CreateCalendarView(calendarView);
        cw.addTeacherHandler();
    }

    @FXML
    private void saveClick() {
        try {
            DB.makeConn();
        } catch (Exception var3) {
            var3.printStackTrace();
        }

        System.out.println("save");
        try {
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
        }
    }
}





