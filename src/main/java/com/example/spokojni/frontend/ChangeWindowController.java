package com.example.spokojni.frontend;

import com.example.spokojni.MainApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class ChangeWindowController {

    public ChangeWindowController(Button button, String file ) throws IOException {
        Parent pane =FXMLLoader.load(Objects.requireNonNull(MainApplication.class.getResource(file)));
        Stage stage = (Stage) button.getScene().getWindow();
        stage.getScene().setRoot(pane);
    }
}

