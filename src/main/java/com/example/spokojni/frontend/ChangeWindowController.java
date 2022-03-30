package com.example.spokojni.frontend;

import com.example.spokojni.MainApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class ChangeWindowController {

    public ChangeWindowController(Button button, String file ) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(file));
        Scene scene = new Scene(fxmlLoader.load(), 1500, 1000);
        Stage stage = (Stage) button.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}

