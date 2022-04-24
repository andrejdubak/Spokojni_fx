package com.example.spokojni.frontend;

import com.calendarfx.model.*;
import com.calendarfx.view.CalendarView;
import com.example.spokojni.backend.Agreement;
import com.example.spokojni.backend.Subject;
import com.example.spokojni.backend.Term;
import com.example.spokojni.backend.User;
import com.example.spokojni.backend.db.DB;
import javafx.event.EventHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static java.lang.Integer.parseInt;

public class CreateCalendarView {
    private final CalendarView calendarView;
    private ArrayList<Term> terms = new ArrayList<>();
    private ArrayList<Term> new_terms = new ArrayList<>();
    private ArrayList<Term> terms_to_del = new ArrayList<>();
    private ArrayList<Calendar> calendars = new ArrayList<>();
    private ArrayList<Subject> subjects = new ArrayList<>();
    private CalendarSource schoolCalendarSource;
    private User user;
    private Logger logger = LogManager.getLogger(CreateCalendarView.class);

    public CreateCalendarView(CalendarView calendarView, User user) {
        this.calendarView = calendarView;
        this.user = user;

        try {
            DB.makeConn();
            subjects.addAll(DB.getSubjects());
            logger.info("log_user_id:" + user.getId() + " Get subjects from database");
        } catch (SQLException var2) {
            sqlException(var2);

            logger.error("log_user_id:" + user.getId() + "No database connection" + var2);

        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("log_user_id:" + user.getId() + "Cannot get subjects" + e);
        }

        int counter1 = 0;
        for (Subject sub: subjects) {
            calendars.add(new Calendar(sub.getName()));
            calendars.get(counter1).setStyle(Calendar.Style.getStyle(counter1));
            counter1++;
        }

        schoolCalendarSource = new CalendarSource("School");

        for (Calendar cal: calendars) {
            schoolCalendarSource.getCalendars().add(cal);
        }

        calendarView.getCalendarSources().setAll(schoolCalendarSource);
    }

    public void setStudentCalendars() {
        try {
            logger.info("log_user_id:" + user.getId() + "Student calendar setted up");
            DB.makeConn();

            ArrayList<Agreement> agreements = DB.getAgreementsByStudentId(user.getId());
            for (Term term : DB.getTerms()) {
                Entry entry = entryHelper(term);
                int counter = 0;
                boolean flag = true;
                for(Calendar cal: calendars) {
                    //System.out.println(cal.getName());
                    for (Agreement agr : agreements) {
                        if (agr.getTerm().getId() == term.getId()) {
                            calendars.get(0).addEntry(entry);
                            flag = false;
                        }
                    }
                    if (flag){
                        if (cal.getName().equals(term.getSubject().getName()))
                            calendars.get(counter).addEntry(entry);
                        else
                            counter++;
                    }
                }
            }
        } catch (SQLException var2) {
            logger.warn("log_user_id:" + user.getId() + "Cannot set up calendar" + var2);
            sqlException(var2);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Cannot finish operation" + e);
        }
    }

    public void setTeacherCalendars() {
        try {
            logger.info("log_user_id:" + user.getId() + "Teacher calendar setted up");
            DB.makeConn();

            for (Term term : DB.getTerms()) {
                Entry entry = entryHelper(term);
                int counter = 0;
                for(Calendar cal: calendars) {
                    if (cal.getName().equals(term.getSubject().getName()))
                        calendars.get(counter).addEntry(entry);
                    else
                        counter++;
                }
            }
        } catch (SQLException var2) {
            logger.warn("log_user_id:" + user.getId() + "Cannot set up calendar" + var2);
            sqlException(var2);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("Cannot finish operation" + e);
        }
    }

    private Entry entryHelper(Term term) {
        //logger.info("log_user_id:" + user.getId() + "Added term");
        terms.add(term);
        Interval interval = new Interval(term.getStart_time(), term.getEnd_time());
        Entry<String> entry = new Entry<>(term.getSubject().getName(), interval);
        entry.setLocation(term.getDescription());
        return entry;
    }

    public void setStudentPopup() {
        calendarView.setEntryDetailsPopOverContentCallback(param -> new NewPopup(param.getEntry(),param.getDateControl().getCalendars(), terms, user));
    }

    public void addTeacherHandler() {
        EventHandler<CalendarEvent> handler = evt -> eventListener(evt);

        for (Calendar cal: calendars) {
            cal.addEventHandler(handler);
        }
    }

    private void sqlException(SQLException var2) {
        System.out.println("SQLException: " + var2.getMessage());
        System.out.println("SQLState: " + var2.getSQLState());
        System.out.println("VendorError: " + var2.getErrorCode());
        var2.printStackTrace();
    }

    private void eventListener (CalendarEvent evt) {
        System.out.println(evt.getEventType() + evt.getEntry().getId());
        Entry entry = evt.getEntry();
        int entry_id = parseInt(entry.getId());

        if (evt.isEntryRemoved()) { //ak sa jedna o vymazanie
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
            updateTerm(entry);
        }
        else {
            int counter = 0;
            for (Term term : new_terms) {
                if (entry_id == term.getId()){
                    new_terms.set(counter, createTerm(entry));
                    return;
                }
                counter++;
            }
            new_terms.add(createTerm(entry));
            entry.setTitle(entry.getCalendar().getName()); //automaticke nastavenie mena podla kalendaru
        }

    }

    private void updateTerm (Entry entry) {
        //logger.info("log_user_id:" + user.getId() + "Term updated");
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
        logger.info("log_user_id:" + user.getId() + "Term created");
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
            logger.error("log_user_id:" + user.getId() + "No database connection" + var2);
        }
        return new Term(entry_id, subject, new_start, new_end, new_desc);
    }

    public void addStudentCalendar () {
        calendars.add(0, new Calendar("Moj"));
        calendars.get(0).setStyle(Calendar.Style.getStyle(6));
        schoolCalendarSource.getCalendars().add(calendars.get(0));
        calendarView.getCalendarSources().setAll(schoolCalendarSource);
    }

    public void disableOtherTeachersCalendars() {
        int counter1 = 0;
        for (Subject sub: subjects) {
            if(sub.getMaster().getId() != user.getId()) //disabluje kalendare, ktorych dany ucitel nie je garant, teda nema prava ich menit
                calendars.get(counter1).setReadOnly(true);
            counter1++;
        }
    }

    public void disableActionForStudent() {
        calendarView.setEntryFactory(param -> null);
        calendarView.setEntryEditPolicy(param -> false);
    }

    public ArrayList<Term> getTerms() {
        return terms;
    }

    public ArrayList<Term> getNew_terms() {
        return new_terms;
    }

    public ArrayList<Term> getTerms_to_del() {
        return terms_to_del;
    }
}
