package com.example.spokojni.frontend;

import com.example.spokojni.MainApplication;
import com.example.spokojni.backend.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class ChangeWindowController {
    private FXMLLoader fxmlLoader;
    private Parent pane;
    Logger logger = LogManager.getLogger(ChangeWindowController.class);
    public ChangeWindowController(String file, Locale loc) throws IOException {
        logger.info("Scene changed");
        fxmlLoader = new FXMLLoader(MainApplication.class.getResource(file));
        ResourceBundle rb =  (ResourceBundle.getBundle("com.example.spokojni.messages", loc));
        fxmlLoader.setResources(rb);
        pane = fxmlLoader.load();
        //Parent pane =FXMLLoader.load(Objects.requireNonNull(MainApplication.class.getResource(file)));
    }
    public FXMLLoader getFxmlLoader(){
        return fxmlLoader;
    }

    public void changeWindow(Button button) throws IOException {
        logger.info("Scene changed");
        Stage stage = (Stage) button.getScene().getWindow();
        stage.getScene().setRoot(pane);
    }
}

