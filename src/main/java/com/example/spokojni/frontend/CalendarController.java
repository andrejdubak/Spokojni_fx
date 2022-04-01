package com.example.spokojni.frontend;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.model.Interval;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import com.calendarfx.view.CalendarView;

import java.io.IOException;
import java.time.*;
import java.time.ZonedDateTime;
import java.util.Locale;

public class CalendarController {
    @FXML
    private CalendarView calendarView;

    @FXML
    private Button button;

    @FXML
    protected void buttonClick() throws IOException {
        //Calendar calendar = ...
/*        Entry<String> dentistAppointment = new Entry<>("Dentist");
        calendar.addEntry(dentistAppointment);*/
        System.out.println("btn");
        LocalDateTime now = LocalDateTime.now();
        System.out.println(now);
        System.out.println("ZoneId.systemDefault(): " + ZoneId.systemDefault());

        // convert LocalDateTime to ZonedDateTime, with default system zone id
        ZonedDateTime zonedDateTime = now.atZone(ZoneId.systemDefault());

        // convert LocalDateTime to ZonedDateTime, with specified zoneId
        ZonedDateTime europeDateTime = zonedDateTime.withZoneSameInstant(ZoneId.of("Europe/Kaliningrad"));
        System.out.println(europeDateTime);

        // convert LocalDateTime to ZonedDateTime, with specified off set
        ZonedDateTime offSetNegative5 = now.atOffset(ZoneOffset.of("-05:00")).toZonedDateTime();
        System.out.println(offSetNegative5);

        calendarView.createEntryAt(europeDateTime);


        Calendar defCalendar = new Calendar("School");



        CalendarSource familyCalendarSource = new CalendarSource("Family");
        familyCalendarSource.getCalendars().addAll(defCalendar);

        calendarView.getCalendarSources().setAll(familyCalendarSource);
        Interval interval = new Interval(LocalDateTime.now(),LocalDateTime.now());
        Entry<String> dentistAppointment = new Entry<>("Dentist", interval);
        defCalendar.addEntry(dentistAppointment);

        System.out.println(calendarView.getCalendars());
    }


}
