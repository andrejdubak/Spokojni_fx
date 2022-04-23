package com.example.spokojni;

import com.example.spokojni.backend.Term;
import com.example.spokojni.backend.db.DB;
import com.example.spokojni.frontend.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainApplication extends Application {

     Logger logger = LogManager.getLogger();
    @Override
    public void start(Stage stage) throws IOException {


        try {
            DB.makeConn();
        } catch (Exception var3) {
            var3.printStackTrace();
        }
        /*try {
            Term term = DB.getTermById(1);
            term.setDescription("Toto je prvý termín.......");
            DB.add(term);
        } catch (SQLException e) {
            e.printStackTrace();
        }*/
        logger.info("App started!");
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1500, 1000);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();


    }

    public static void main(String[] args) {
        launch();
    }
}