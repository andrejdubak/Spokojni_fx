package com.example.spokojni.frontend;

import com.calendarfx.model.*;
import com.example.spokojni.backend.Subject;
import com.example.spokojni.backend.Term;
import com.example.spokojni.backend.db.DB;
import com.example.spokojni.backend.users.Teacher;
import com.mysql.jdbc.log.Log;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import com.calendarfx.view.CalendarView;

import java.io.IOException;
import java.sql.SQLException;
import java.time.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

public class TeacherViewController {
    ArrayList<Term> terms = new ArrayList<>();
    ArrayList<Term> new_terms = new ArrayList<>();
    ArrayList<Term> terms_to_del = new ArrayList<>();

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
        //calendarView.setEntryDetailsPopOverContentCallback(param -> new NewPopup(param.getEntry(),param.getDateControl().getCalendars())); //TODO modify NewPop to fit student needs

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
                //System.out.println(term.getId() + " " + term.getStart_time() + " " + term.getEnd_time());
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
    protected void saveClick() throws IOException, SQLException {
        try {
            DB.makeConn();
        } catch (Exception var3) {
            var3.printStackTrace();
        }

        System.out.println("save");
        try {
            for (Term term : terms) {
                //System.out.println(term.toString());
                DB.update(term);
            }

            for (Term term : new_terms) {
                System.out.println(term.toString());
                DB.add(term);
            }
            for (Term term : terms_to_del) {
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

    protected void eventListener (CalendarEvent evt) {
        System.out.println(evt.getEventType() + evt.getEntry().getId());
        Entry entry = evt.getEntry();
        int entry_id = parseInt(entry.getId());

        if (evt.isEntryRemoved()) { //ak sa jedna o vymazanie
            System.out.println("removed");
            //initial terms
            if (entry_id < terms.size()) {
                terms_to_del.add(terms.get(entry_id));
            }

            //newly added terms
            for (Term term : new_terms) {
                if (entry_id == term.getId()){
                    new_terms.remove(term);
                    return;
                }
            }
        }

        //ak iba modifikujeme
        if (entry_id < terms.size()) { //ak sa jedna o existujuci prvok
            //System.out.println("existujuci " + entry_id);
            //System.out.println(terms.get(parseInt(entry.getId())).getStart_time() + " " + entry.getStartAsLocalDateTime());
            updateTerm(entry);

        }
        else {
            System.out.println("z novych");
            int counter = 0;
            for (Term term : new_terms) {
                if (entry_id == term.getId()){
                    //System.out.println("naslo" + " " + new_terms.get(counter).getStart_time());
                    new_terms.set(counter, createTerm(entry));
                    //System.out.println("naslo" + " " + new_terms.get(counter).getStart_time());
                    return;
                }
                counter++;
            }
            new_terms.add(createTerm(entry));
            entry.setTitle(entry.getCalendar().getName()); //automaticke nastavenie mena podla kalendaru
            System.out.println("pridany");
        }

    }

    private void updateTerm (Entry entry) {
        //vytvori a novy modifikovany term a nahradi ho v array
        int entry_id = parseInt(entry.getId());
        Term term = terms.get(entry_id);
        int id = term.getId();

        LocalDateTime new_start = entry.getStartAsLocalDateTime();
        LocalDateTime new_end = entry.getEndAsLocalDateTime();
        String new_desc = entry.getLocation();
        Term modified = new Term(id, term.getSubject(), new_start, new_end, new_desc);

        terms.set(entry_id, modified);
    }

    private Term createTerm (Entry entry) {

        int entry_id = parseInt(entry.getId());
        LocalDateTime new_start = entry.getStartAsLocalDateTime();
        LocalDateTime new_end = entry.getEndAsLocalDateTime();
        String new_desc = entry.getLocation();
        Subject subject = terms.get(0).getSubject();//default subject

        try {
            subject = DB.getSubjectByName(entry.getCalendar().getName());
        } catch (Exception var2) {
            System.out.println("SQLException: " + var2.getMessage());
            var2.printStackTrace();
        }
        return new Term(entry_id, subject, new_start, new_end, new_desc);
    }
}





