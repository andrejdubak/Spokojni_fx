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
import java.util.Locale;
import java.util.ResourceBundle;

public class GeneratePasswordController {
    private Logger logger = LogManager.getLogger(GeneratePasswordController.class);
    @FXML
    private Button save;
    @FXML
    private TextField generated_password;
    private UserTable user;
    private final Alert successfulAlert;
    private Dialog dialog;
    private ResourceBundle rb = ResourceBundle.getBundle("com.example.spokojni.messages", Locale.getDefault());

    public GeneratePasswordController(){
        successfulAlert = new Alert(Alert.AlertType.CONFIRMATION);
        successfulAlert.setHeaderText(rb.getString("Password_changed_successfully"));
        successfulAlert.setContentText(rb.getString("Password_was_successfully_changed"));
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
            logger.error("No database connection" + var3);
        }
        try {
            DB.updatePassword(user.getId(), generated_password.getText());
            logger.info("Password saved");
            passwordChangeSuccessful();
            dialog.close();
        } catch (SQLException var2) {
            logger.warn("Cannot update password " + var2);
            System.out.println("SQLException: " + var2.getMessage());
            System.out.println("SQLState: " + var2.getSQLState());
            System.out.println("VendorError: " + var2.getErrorCode());
            var2.printStackTrace();
        }
    }
}