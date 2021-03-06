//cast kodu prebrata z kniznice CalendarFx classa Popup
package com.example.spokojni.frontend;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.Entry;
import com.calendarfx.view.CalendarSelector;
import com.calendarfx.view.CalendarView;
import com.calendarfx.view.Messages;
import java.time.format.DateTimeFormatter;
import java.util.*;
import com.example.spokojni.backend.Agreement;
import com.example.spokojni.backend.Term;
import com.example.spokojni.backend.User;
import com.example.spokojni.backend.db.DB;
import com.example.spokojni.backend.users.Student;
import javafx.beans.binding.Bindings;
import javafx.geometry.VPos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import static java.lang.Integer.parseInt;

public class NewPopup extends GridPane {
    private Logger logger = LogManager.getLogger(NewPopup.class);
    private Entry<?> entry;
    private ArrayList<Term> terms;
    private ResourceBundle rb = ResourceBundle.getBundle("com.example.spokojni.messages", Locale.getDefault());
    Label numberOfStudents = new Label();

    public NewPopup(Entry<?> entry, List<Calendar> calendars, ArrayList<Term> terms, User user) {
        this.entry = (Entry)Objects.requireNonNull(entry);
        this.terms = terms;
        Objects.requireNonNull(calendars);
        this.getStylesheets().add(CalendarView.class.getResource("calendar.css").toExternalForm());
        TextField titleField = new TextField(entry.getTitle());
        Bindings.bindBidirectional(titleField.textProperty(), entry.titleProperty());
        titleField.disableProperty().bind(entry.getCalendar().readOnlyProperty());
        TextField locationField = new TextField(entry.getLocation());
        Bindings.bindBidirectional(locationField.textProperty(), entry.locationProperty());
        locationField.getStyleClass().add("location");
        locationField.setEditable(true);
        locationField.setPromptText(Messages.getString("EntryHeaderView.PROMPT_LOCATION"));
        locationField.setMinWidth(400.0D);
        locationField.setMaxWidth(500.0D);
        locationField.disableProperty().bind(entry.getCalendar().readOnlyProperty());
        titleField.getStyleClass().add("default-style-entry-popover-title");
        this.add(titleField, 0, 0);
        this.add(locationField, 0, 1);
        RowConstraints row1 = new RowConstraints();
        row1.setValignment(VPos.TOP);
        row1.setFillHeight(true);
        RowConstraints row2 = new RowConstraints();
        row2.setValignment(VPos.TOP);
        row2.setFillHeight(true);
        this.getRowConstraints().addAll(new RowConstraints[]{row1, row2});
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setFillWidth(true);
        col1.setHgrow(Priority.ALWAYS);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setFillWidth(true);
        col2.setHgrow(Priority.NEVER);
        this.getColumnConstraints().addAll(new ColumnConstraints[]{col1, col2});
        this.getStyleClass().add("popover-header");
        titleField.getStyleClass().add("title");
        titleField.setPromptText(Messages.getString("EntryHeaderView.PROMPT_TITLE"));
        titleField.setMaxWidth(500.0D);
        Calendar calendar = entry.getCalendar();
        titleField.getStyleClass().add(calendar.getStyle() + "-entry-popover-title");

        //nase zmeny
        titleField.setEditable(false);
        locationField.setEditable(false);
        CheckBox checkBox = new CheckBox(rb.getString("Add_to_my"));
        if (entry.getCalendar().getName().equals("Moj")) checkBox.setSelected(true);
        this.add(checkBox,0,5);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        Label startDate = new Label(rb.getString("Exam_from:") + entry.getStartAsLocalDateTime().format(formatter));
        this.add(startDate, 0,2);
        Label endDate = new Label(rb.getString("Exam_to:") + entry.getEndAsLocalDateTime().format(formatter));
        this.add(endDate, 0,3);
        if (!updateNumberOfSignedStudents() && !checkBox.isSelected()) checkBox.setDisable(true); //disabluje moznost pridat predmet do svojich ak uz nieje miesto
        if (checkIfAlreadyAdded(user.getId())) checkBox.setDisable(true); //disabluje checkbox ak uz je termin z daneho terminu pridany
        this.add(numberOfStudents, 0,4);

        entry.calendarProperty().addListener((observable, oldCalendar, newCalendar) -> {
            if (oldCalendar != null) {
                titleField.getStyleClass().remove(oldCalendar.getStyle() + "-entry-popover-title");
            }

            if (newCalendar != null) {
                titleField.getStyleClass().add(newCalendar.getStyle() + "-entry-popover-title");
            }

        });

        //checkbox riadiaci prihlasenie/odhlasenie na termin skusky
        checkBox.setOnAction((evt) -> {
            if (checkBox.isSelected()) { //prida do vlastneho calendaru
                entry.setCalendar(calendars.get(calendars.size() - 1));
                try {
                    DB.makeConn();
                    DB.add(new Agreement(1, new Student(user.getId(), user.getName(), user.getEmail(), user.getLogin()), terms.get(parseInt(entry.getId()))));
                    logger.info("Added to calendar");
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("No database connection" + e);
                }
            }
            else {
                for (Calendar cal : calendars) { //vtari do povodneho calendaru
                    if (cal.getName().equals(entry.getTitle())) {
                        entry.setCalendar(cal);
                        try {
                            DB.makeConn();
                            Agreement agr = DB.getAgreement( user.getId(), terms.get(parseInt(entry.getId())).getId());
                            if (agr != null) {
                                DB.delete(agr);//vymaze z databazy agreement
                                logger.info("Deleted agreement");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            logger.error("No database connection" + e);
                        }
                    }
                }
            }
            updateNumberOfSignedStudents();
        });
    }

    //ak uz ma pridany student termin z daneho predmetu
    private boolean checkIfAlreadyAdded(int userId) {
        try {
            DB.makeConn();
            ArrayList<Agreement> agreements = DB.getAgreementsByStudentId(userId);
            for (Agreement agr : agreements) {
                if (agr.getTerm().getSubject().getName().equals(entry.getCalendar().getName()))
                    return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Cannot finish operation" + e);
        }
        return false;
    }

    //aktualizuje pocet studentov prihlasenych na konkretny termin
    private boolean updateNumberOfSignedStudents() {
        logger.info("Number of students updated");
        int term_id = terms.get(parseInt(entry.getId())).getId();
        int actualNum = getNumberOfAssignedStudents(term_id); //spocita prihlasenych studenotov pre dany termin
        int maxNum = terms.get(parseInt(entry.getId())).getCapacity();
        numberOfStudents.setText(rb.getString("Number_of_assigned_students:") + actualNum + "/" + maxNum);
        return actualNum < maxNum;
    }

    //vrati pocet studentov prihlasenych na termin
    private int getNumberOfAssignedStudents(int term_id) {
        ArrayList<Agreement> list = new ArrayList<>();
        try {
            DB.makeConn();
            list = DB.getAgreementsByTermId(term_id);
            logger.info("Get mumber of assigned Students");
        } catch (Exception var3) {
            var3.printStackTrace();
            logger.error("No database connection" + var3);
        }
        return list.size();
    }
}