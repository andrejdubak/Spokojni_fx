package com.example.spokojni.frontend;

import com.example.spokojni.MainApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class ChangeWindowController {
    private FXMLLoader fxmlLoader;
    private Parent pane;
    private Logger logger = LogManager.getLogger(ChangeWindowController.class);
    private Scene scene;

    //zmena okna s lokalizaciou
    public ChangeWindowController(String file, Locale loc) throws IOException {
        logger.info("Scene changed to" + file);
        fxmlLoader = new FXMLLoader(MainApplication.class.getResource(file));
        ResourceBundle rb =  (ResourceBundle.getBundle("com.example.spokojni.messages", loc));
        fxmlLoader.setResources(rb);

        scene = new Scene(fxmlLoader.load());
    }
    public FXMLLoader getFxmlLoader(){
        return fxmlLoader;
    }

    public void changeWindow(Button button) throws IOException {
        Stage stage = (Stage) button.getScene().getWindow();
        stage.setScene(scene);
        stage.setResizable(false);
    }
}

