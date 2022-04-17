package com.example.spokojni.frontend;


import com.calendarfx.model.Calendar;
import com.calendarfx.model.Entry;
import com.calendarfx.view.CalendarSelector;
import com.calendarfx.view.CalendarView;
import com.calendarfx.view.Messages;
import java.util.List;
import java.util.Objects;
import javafx.beans.binding.Bindings;
import javafx.geometry.VPos;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

public class NewPopup extends GridPane {
    private final CalendarSelector calendarSelector;
    private Entry<?> entry;

    public NewPopup(Entry<?> entry, List<Calendar> calendars) {
        this.entry = (Entry)Objects.requireNonNull(entry);
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
        locationField.setMaxWidth(500.0D);
        locationField.disableProperty().bind(entry.getCalendar().readOnlyProperty());
        this.calendarSelector = new CalendarSelector();
        this.calendarSelector.disableProperty().bind(entry.getCalendar().readOnlyProperty());
        this.calendarSelector.getCalendars().setAll(calendars);
        this.calendarSelector.setCalendar(entry.getCalendar());
        Bindings.bindBidirectional(this.calendarSelector.calendarProperty(), entry.calendarProperty());
        titleField.getStyleClass().add("default-style-entry-popover-title");
        this.add(titleField, 0, 0);
        this.add(this.calendarSelector, 1, 0, 1, 2);
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
        entry.calendarProperty().addListener((observable, oldCalendar, newCalendar) -> {
            if (oldCalendar != null) {
                titleField.getStyleClass().remove(oldCalendar.getStyle() + "-entry-popover-title");
            }

            if (newCalendar != null) {
                titleField.getStyleClass().add(newCalendar.getStyle() + "-entry-popover-title");
            }

        });
    }

    public final Calendar getCalendar() {
        Calendar calendar = this.calendarSelector.getCalendar();
        if (calendar == null) {
            calendar = this.entry.getCalendar();
        }

        return calendar;
    }
}