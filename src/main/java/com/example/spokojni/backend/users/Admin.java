package com.example.spokojni.backend.users;


import com.example.spokojni.backend.User;

public class Admin extends User {
    public Admin(int id, String name, String email, String login) {
        super(id, name, email, login);
    }

    @Override
    public int getRole() {
        return 3;
    }
}
