package com.example.spokojni.frontend;

import com.example.spokojni.backend.UserTable;
import com.example.spokojni.backend.db.DB;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;

public class GeneratePasswordController {
    Logger logger = LogManager.getLogger(GeneratePasswordController.class);
    @FXML
    private Button save;
    @FXML
    private TextField generated_password;
    private UserTable user;
    private final Alert successfulAlert;
    private Dialog dialog;

    public GeneratePasswordController(){
        successfulAlert = new Alert(Alert.AlertType.CONFIRMATION);
        successfulAlert.setHeaderText("Successful change of password");
        successfulAlert.setContentText("Password was successfully changed");
    }

    public void setCurrentUser(UserTable user, Dialog dialog) {
        this.user = user;
        this.dialog = dialog;
    }

    @FXML
    private void generatePassword() {
        generated_password.setText(RegisterPersonController.generateNewPass());
        save.setVisible(true);
    }

    private void passwordChangeSuccessful() {
        logger.info("Password changed successfully");
        successfulAlert.showAndWait();
    }

    @FXML
    private void savePassword(ActionEvent actionEvent) {
        try {
            DB.makeConn();
        } catch (Exception var3) {
            var3.printStackTrace();
        }
        try {
            DB.updatePassword(user.getId(), generated_password.getText());
            logger.info("Password saved");
            passwordChangeSuccessful();
            dialog.close();
        } catch (SQLException var2) {
            logger.error("No database connection");
            System.out.println("SQLException: " + var2.getMessage());
            System.out.println("SQLState: " + var2.getSQLState());
            System.out.println("VendorError: " + var2.getErrorCode());
            var2.printStackTrace();
        }
    }
}