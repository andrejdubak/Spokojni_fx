package com.example.spokojni.frontend;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.model.Interval;
import com.example.spokojni.backend.Term;
import com.example.spokojni.backend.db.DB;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import com.calendarfx.view.CalendarView;

import java.io.IOException;
import java.sql.SQLException;
import java.time.*;
import java.time.ZonedDateTime;

public class CalendarController {
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

        Calendar vavaCalendar = new Calendar("Vava");
        Calendar oopCalendar = new Calendar("OOP");

        vavaCalendar.setStyle(Calendar.Style.STYLE1);
        oopCalendar.setStyle(Calendar.Style.STYLE2);

        CalendarSource familyCalendarSource = new CalendarSource("School");
        familyCalendarSource.getCalendars().addAll(vavaCalendar, oopCalendar);

        calendarView.getCalendarSources().setAll(familyCalendarSource);

        calendarView.createEntryAt(europeDateTime, oopCalendar);

        try {
            DB.makeConn();
        } catch (Exception var3) {
            var3.printStackTrace();
        }
        try {
            for(Term term : DB.getTerms()){
                /*System.out.print(term.getId());
                System.out.print(term.getSubject().getName());
                System.out.print(term.getStart_time());
                System.out.print(term.getEnd_time());
                System.out.println(term.getDescription());*/

                Interval interval = new Interval(term.getStart_time(), term.getEnd_time());
                Entry<String> dentistAppointment = new Entry<>(term.getSubject().getName(), interval);
                dentistAppointment.setLocation(term.getDescription());
                vavaCalendar.addEntry(dentistAppointment);
            }
        } catch (SQLException var2) {
            System.out.println("SQLException: " + var2.getMessage());
            System.out.println("SQLState: " + var2.getSQLState());
            System.out.println("VendorError: " + var2.getErrorCode());
            var2.printStackTrace();
        }
    }
}
