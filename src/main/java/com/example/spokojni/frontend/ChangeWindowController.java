package com.example.spokojni.frontend;

import com.example.spokojni.MainApplication;
import com.example.spokojni.backend.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class ChangeWindowController {
    private FXMLLoader fxmlLoader;
    private Parent pane;
    public ChangeWindowController(String file) throws IOException {
        fxmlLoader = new FXMLLoader(MainApplication.class.getResource(file));
        pane = fxmlLoader.load();
        //Parent pane =FXMLLoader.load(Objects.requireNonNull(MainApplication.class.getResource(file)));
    }
    public FXMLLoader getFxmlLoader(){
        return fxmlLoader;
    }

    public void changeWindow(Button button) throws IOException {
        Stage stage = (Stage) button.getScene().getWindow();
        stage.getScene().setRoot(pane);
    }
}

