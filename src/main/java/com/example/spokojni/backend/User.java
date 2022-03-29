package com.example.spokojni.backend;

public class User {
    private int id;
    private String name;
    private String email;
    private String login;

    public User(int id, String name, String email, String login) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.login = login;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof User && ((User) obj).getId() == this.id;
    }

    public int getRole(){
        return 0;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}
