package com.example.spokojni.frontend;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.IOException;

public class AdminViewController {

    @FXML
    private Button Profile;

    @FXML
    private Button Students;

    @FXML
    private Button Teachers;

    @FXML
    private Button exportPeople;

    @FXML
    private Button importPeople;

    @FXML
    private Button logOut;

    @FXML
    private Text name;

    @FXML
    private Button registerPerson;

    @FXML
    private TextField search;


    @FXML
    private void logoutClick() throws IOException{
        new ChangeWindowController(logOut, "login-view.fxml");
    }
}