package com.example.spokojni.frontend;

import com.example.spokojni.backend.UserTable;
import com.example.spokojni.backend.db.DB;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class GeneratePasswordController {

    @FXML
    private TextField generated_password;
    private UserTable user;
    private final Alert successfulAlert;

    public GeneratePasswordController(){
        successfulAlert = new Alert(Alert.AlertType.CONFIRMATION);
        successfulAlert.setHeaderText("Successful change of password");
        successfulAlert.setContentText("Password was successfully changed");
    }

    public void setCurrentUser(UserTable user) {
        this.user = user;
    }

    @FXML
    private void generatePassword() {
        generated_password.setText(RegisterPersonController.generateNewPass());
        try {
            DB.makeConn();
        } catch (Exception var3) {
            var3.printStackTrace();
        }
        try {
            DB.updatePassword(user.getId(), generated_password.getText());
            passwordChangeSuccessful();
        } catch (SQLException var2) {
            System.out.println("SQLException: " + var2.getMessage());
            System.out.println("SQLState: " + var2.getSQLState());
            System.out.println("VendorError: " + var2.getErrorCode());
            var2.printStackTrace();
        }
    }

    private void passwordChangeSuccessful() {
        successfulAlert.showAndWait();
    }
}
