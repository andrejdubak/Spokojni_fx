package com.example.spokojni.backend;


import com.example.spokojni.backend.users.Student;

public class Agreement{
    private int id;
    private Student student;
    private Term term;

    public Agreement(int id, Student student, Term term) {
        this.id = id;
        this.student = student;
        this.term = term;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Agreement && ((Agreement) obj).getId() == this.id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }
}
