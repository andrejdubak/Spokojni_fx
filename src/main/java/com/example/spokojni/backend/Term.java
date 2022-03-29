package com.example.spokojni.backend;

import java.sql.Timestamp;

public class Term {
    private int id;
    private Subject subject;
    private Timestamp start_time;

    public Term(int id, Subject subject, Timestamp start_time) {
        this.id = id;
        this.subject = subject;
        this.start_time = start_time;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Term && ((Term) obj).getId() == this.id;
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

    public Timestamp getStart_time() {
        return start_time;
    }

    public void setStart_time(Timestamp start_time) {
        this.start_time = start_time;
    }
}
