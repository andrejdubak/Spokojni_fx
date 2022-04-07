package com.example.spokojni.frontend;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginController {
    @FXML
    private Button LoginClick;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    protected void loginClick() throws IOException {
        int role_number = 2;
        //int role_number = userRoleGetByEmail(username, password); //TODO vytvorit getter na backende pre rolu
        if (role_number == 1)
            new ChangeWindowController(LoginClick,"student-view.fxml");
        else if (role_number == 2)
            new ChangeWindowController(LoginClick,"teacher-view.fxml");
        else if (role_number == 3)
            new ChangeWindowController(LoginClick,"admin-view.fxml");
        else
            System.out.println("Login Error!!");
    }

    @FXML
    protected void registerClick() {};
}
