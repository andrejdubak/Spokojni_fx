package com.example.spokojni.backend;

public class UserTable {
    // Atributy pouzivatela ulozeneho v tabulke
    private String name;
    private String email;
    private String role;
    private Integer id;

    public UserTable(String name, String email, String role, Integer id) {
        this.name = name;
        this.email = email;
        this.role = role;
        this.id = id;
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

    public Integer getId() {
        return id;
    }
}
