package com.example.spokojni.frontend;

import com.calendarfx.model.*;
import com.example.spokojni.backend.Subject;
import com.example.spokojni.backend.Term;
import com.example.spokojni.backend.db.DB;
import com.mysql.jdbc.log.Log;
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

import static java.lang.Integer.parseInt;

public class TeacherViewController {
    ArrayList<Term> terms = new ArrayList<>();
    @FXML
    private CalendarView calendarView;

    @FXML
    private Button loadButton;

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

        int counter1 = 0;
        for (Subject sub: subjects) {
            calendars.add(new Calendar(sub.getName()));
            //System.out.println(sub.getMaster().getId());
            if(sub.getMaster().getId() != 5) //TODO staticke cislo zmenit na idcko ucitela aby vedel modifikovat iba sebe pridelene predmety
                calendars.get(counter1).setReadOnly(true);
            //System.out.println(sub.getName());
            counter1++;
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

        //calendarView.createEntryAt(europeDateTime, calendars.get(1));

        try {
            for (Term term : DB.getTerms()) {
                terms.add(term);
                int counter = 0;
                System.out.println(term.getId() + " " + term.getSubject().getName());
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

    @FXML
    protected void saveClick() throws IOException {
        System.out.println("save");
        //TODO comparne vsetky s idckami mensimi ako list, ostatne vytvori
    }

    protected void eventListener (CalendarEvent evt) {
        System.out.println(evt.getEventType() + evt.getEntry().getId());
        Entry entry = evt.getEntry();
        System.out.println(entry.getId()+ " " + entry.getTitle());
        if (parseInt(entry.getId()) + 1 < terms.size()) {
            System.out.println("existujuci");
        }
        else {
            System.out.println("novy");
        }

    }
}
