package com.example.spokojni;

import com.example.spokojni.backend.db.DB;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1500, 1000);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();

        try {
            DB.makeConn();
        } catch (Exception var3) {
            var3.printStackTrace();
        }
        try {
            System.out.print(DB.getTermById(1).getStart_time());
        } catch (SQLException var2) {
            System.out.println("SQLException: " + var2.getMessage());
            System.out.println("SQLState: " + var2.getSQLState());
            System.out.println("VendorError: " + var2.getErrorCode());
            var2.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}