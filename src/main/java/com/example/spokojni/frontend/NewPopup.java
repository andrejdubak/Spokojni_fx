package com.example.spokojni.frontend;


import com.calendarfx.model.Calendar;
import com.calendarfx.model.Entry;
import com.calendarfx.view.CalendarSelector;
import com.calendarfx.view.CalendarView;
import com.calendarfx.view.Messages;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

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
    Logger logger = LogManager.getLogger(NewPopup.class);
    private final CalendarSelector calendarSelector;
    private Entry<?> entry;
    private ArrayList<Term> terms;
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
        this.calendarSelector = new CalendarSelector();
        this.calendarSelector.disableProperty().bind(entry.getCalendar().readOnlyProperty());
        this.calendarSelector.getCalendars().setAll(calendars);
        this.calendarSelector.setCalendar(entry.getCalendar());
        Bindings.bindBidirectional(this.calendarSelector.calendarProperty(), entry.calendarProperty());
        titleField.getStyleClass().add("default-style-entry-popover-title");
        this.add(titleField, 0, 0);
        //this.add(this.calendarSelector, 1, 0, 1, 2);
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

        //moje zmeny
        titleField.setEditable(false);
        locationField.setEditable(false);
        CheckBox checkBox = new CheckBox("Add to my");
        if (entry.getCalendar().getName().equals("Moj")) checkBox.setSelected(true);
        //checkBox.disableProperty().bind(entry.getCalendar().readOnlyProperty());
        this.add(checkBox,0,5);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        Label startDate = new Label("Skúška od: " + entry.getStartAsLocalDateTime().format(formatter));
        this.add(startDate, 0,2);
        Label endDate = new Label("Skúška do: " + entry.getEndAsLocalDateTime().format(formatter));
        this.add(endDate, 0,3);
        if (!updateNumberOfSignedStudents() && !checkBox.isSelected()) checkBox.setDisable(true); //disabluje moznost pridat predmet do svojich ak uz nieje miesto
        this.add(numberOfStudents, 0,4);

        entry.calendarProperty().addListener((observable, oldCalendar, newCalendar) -> {
            if (oldCalendar != null) {
                titleField.getStyleClass().remove(oldCalendar.getStyle() + "-entry-popover-title");
            }

            if (newCalendar != null) {
                titleField.getStyleClass().add(newCalendar.getStyle() + "-entry-popover-title");
            }

        });

        checkBox.setOnAction((evt) -> {
            if (checkBox.isSelected()) { //prida do vlastneho calendaru
                entry.setCalendar(calendars.get(calendars.size() - 1));
                try {
                    DB.makeConn();
                    DB.add(new Agreement(1, new Student(user.getId(), user.getName(), user.getEmail(), user.getLogin()), terms.get(parseInt(entry.getId()))));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                for (Calendar cal : calendars) { //vtari do povodneho calendaru
                    if (cal.getName().equals(entry.getTitle()))
                        entry.setCalendar(cal);
                    try {
                        Agreement agr = DB.getAgreement( user.getId(), terms.get(parseInt(entry.getId())).getId());
                        DB.makeConn();
                        DB.delete(agr);//vymaze z databazy agreement
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            updateNumberOfSignedStudents();
            //System.out.println(entry.getCalendar().getName());
        });
    }

    private boolean updateNumberOfSignedStudents() {
        logger.info("Number of students updated");
        //for (Term tr : terms) System.out.println(tr);
        int term_id = terms.get(parseInt(entry.getId())).getId();
        int actualNum = getNumberOfAssignedStudents(term_id); //spocita prihlasenych studenotov pre dany termin
        int maxNum = terms.get(parseInt(entry.getId())).getCapacity();
        if (Locale.getDefault().equals(new Locale("en", "UK"))){
            numberOfStudents.setText("Number of assigned students: " + actualNum + "/" + maxNum);
        }
        else if (Locale.getDefault().equals(new Locale("sk", "SK"))){
            numberOfStudents.setText("Počet prihlásených študentov: " + actualNum + "/" + maxNum);
        }
        else if (Locale.getDefault().equals(new Locale("de", "DE"))){
            numberOfStudents.setText("Anzahl der zugewiesenen Schüler: " + actualNum + "/" + maxNum);
        }
        System.out.println(actualNum < maxNum);
        return actualNum < maxNum;
    }

    public final Calendar getCalendar() {
        Calendar calendar = this.calendarSelector.getCalendar();
        if (calendar == null) {
            calendar = this.entry.getCalendar();
        }

        return calendar;
    }
    private int getNumberOfAssignedStudents(int term_id) {
        ArrayList<Agreement> list = new ArrayList<>();
        try {
            DB.makeConn();
            list = DB.getAgreementsByTermId(term_id);
            logger.info("Get mumber of assigned Students");
            //System.out.println(list);
        } catch (Exception var3) {
            var3.printStackTrace();
            logger.error("No database conncetion");
        }
        return list.size();
    }
}