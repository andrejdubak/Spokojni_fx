package com.example.spokojni.backend;

public class UserTable {


    private String name;
    private String email;
    private String role;

    public UserTable(String name, String email, String role) {
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }


}
