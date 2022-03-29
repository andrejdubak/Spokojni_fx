package com.example.spokojni.backend;


import com.example.spokojni.backend.users.Teacher;

public class Subject {
    private int id;
    private String name;
    private Teacher master;

    public Subject(int id, String name, Teacher master) {
        this.id = id;
        this.name = name;
        this.master = master;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Subject && ((Subject) obj).getId() == this.id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Teacher getMaster() {
        return master;
    }

    public void setMaster(Teacher master) {
        this.master = master;
    }
}
