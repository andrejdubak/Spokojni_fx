package com.example.spokojni.backend.users;


import com.example.spokojni.backend.User;

public class Teacher extends User {
    public Teacher(int id, String name, String email, String login) {
        super(id, name, email, login);
    }

    @Override
    public int getRole() {
        return 2;
    }
}
