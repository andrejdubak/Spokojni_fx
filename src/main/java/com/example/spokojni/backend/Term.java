package com.example.spokojni.backend;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Term {
    private int id;
    private Subject subject;
    private LocalDateTime start_time;
    private LocalDateTime end_time;
    private String description;
    private int capacity;

    public Term(int id, Subject subject, LocalDateTime start_time, LocalDateTime end_time, String description, int capacity) {
        this.id = id;
        this.subject = subject;
        this.start_time = start_time;
        this.end_time = end_time;
        this.description = description;
        this.capacity = capacity;
    }

    public Term(int id, Subject subject, LocalDateTime start_time, LocalDateTime end_time, String description) {
        this.id = id;
        this.subject = subject;
        this.start_time = start_time;
        this.end_time = end_time;
        this.description = description;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Term && ((Term) obj).getId() == this.id;
    }

    @Override
    public String toString() {
        return "ID: " + this.id + " Subject name: " + this.subject.getName() + " Start time: " + this.start_time + " End time: " + this.end_time + " Description: " + this.description + " Capacity: " + this.capacity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public LocalDateTime getStart_time() {
        return start_time;
    }

    public void setStart_time(LocalDateTime start_time) {
        this.start_time = start_time;
    }

    public LocalDateTime getEnd_time() {
        return end_time;
    }

    public void setEnd_time(LocalDateTime end_time) {
        this.end_time = end_time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
