package com.example.spokojni.frontend;

import com.example.spokojni.MainApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class LoginController {
    @FXML
    private Button LoginClick;

    @FXML
    private Label welcomeText;

    @FXML
    protected void loginClick() throws IOException {
        new ChangeWindowController(LoginClick,"main-view.fxml");
    }

    @FXML
    protected void registerClick() {};
}
