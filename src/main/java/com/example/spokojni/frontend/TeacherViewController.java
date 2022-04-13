package com.example.spokojni.frontend;

import com.calendarfx.model.*;
import com.example.spokojni.backend.Subject;
import com.example.spokojni.backend.Term;
import com.example.spokojni.backend.db.DB;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import com.calendarfx.view.CalendarView;

import java.io.IOException;
import java.sql.SQLException;
import java.time.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class TeacherViewController {
    @FXML
    private CalendarView calendarView;

    @FXML
    private Button button;

    @FXML
    protected void buttonClick() throws IOException {
        LocalDateTime now = LocalDateTime.now();

        // convert LocalDateTime to ZonedDateTime, with default system zone id
        ZonedDateTime zonedDateTime = now.atZone(ZoneId.systemDefault());

        // convert LocalDateTime to ZonedDateTime, with specified zoneId
        ZonedDateTime europeDateTime = zonedDateTime.withZoneSameInstant(ZoneId.of("Europe/Kaliningrad"));
        //System.out.println(europeDateTime);

        ArrayList<Subject> subjects = new ArrayList<>();
        ArrayList<Calendar> calendars = new ArrayList<>();
        try {
            //subjects = new ArrayList<Subject>();
            subjects.addAll(DB.getSubjects());
        } catch (SQLException var2) {
            System.out.println("SQLException: " + var2.getMessage());
            System.out.println("SQLState: " + var2.getSQLState());
            System.out.println("VendorError: " + var2.getErrorCode());
            var2.printStackTrace();
        }

        for (Subject sub: subjects) {
            calendars.add(new Calendar(sub.getName()));
            System.out.println(sub.getName());
        }

        EventHandler<CalendarEvent> handler = evt -> eventListener(evt);

        for (Calendar cal: calendars) {
            cal.addEventHandler(handler);
        }

        calendars.get(0).setStyle(Calendar.Style.STYLE1);
        calendars.get(1).setStyle(Calendar.Style.STYLE2);

        CalendarSource schoolCalendarSource = new CalendarSource("School");

        for (Calendar cal: calendars) {
            schoolCalendarSource.getCalendars().add(cal);
        }

        calendarView.getCalendarSources().setAll(schoolCalendarSource);

        calendarView.createEntryAt(europeDateTime, calendars.get(1));

        try {
            for (Term term : DB.getTerms()) {
                int counter = 0;
                Interval interval = new Interval(term.getStart_time(), term.getEnd_time());
                Entry<String> entry = new Entry<>(term.getSubject().getName(), interval);
                entry.setLocation(term.getDescription());
                for(Calendar cal: calendars) {
                    if (cal.getName().equals(term.getSubject().getName()))
                        calendars.get(counter).addEntry(entry);
                    else
                        counter++;
                }
            }
        } catch (SQLException var2) {
            System.out.println("SQLException: " + var2.getMessage());
            System.out.println("SQLState: " + var2.getSQLState());
            System.out.println("VendorError: " + var2.getErrorCode());
            var2.printStackTrace();
        }
    }
    protected void eventListener (CalendarEvent evt) {
        System.out.println(evt.getEventType());

    }
}
