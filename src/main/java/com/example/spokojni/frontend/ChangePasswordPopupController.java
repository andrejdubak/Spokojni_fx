package com.example.spokojni.frontend;

import com.example.spokojni.backend.User;
import com.example.spokojni.backend.db.DB;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangePasswordPopupController {

    @FXML
    private Button savePassword;
    @FXML
    private PasswordField repeatPassword;
    @FXML
    private PasswordField newPassword;
    @FXML
    private PasswordField oldPassword;
    Logger logger = LogManager.getLogger(ChangePasswordPopupController.class);
    private User currentUser;
    private Alert successfulAlert;
    private Alert errorAlert;

    @FXML
    private void initialize() {

        logger.info("Initialized");

        setupAlerts();
    }

    @FXML
    private void saveSettings() {
        if (newPassword.getText().isEmpty()) {
            logger.warn("No password");
            showError("New password ERROR", "New password cannot be empty");

        }
        else {
            if (Objects.equals(repeatPassword.getText(), newPassword.getText())) {
                try {
                    DB.makeConn();
                } catch (Exception var3) {
                    var3.printStackTrace();
                    logger.error("No database connection");
                }
                try {

                    if (DB.checkPassword(currentUser.getId(), oldPassword.getText())) {
                        if(isValidPassword(newPassword.getText())) {
                            DB.updatePassword(currentUser.getId(), newPassword.getText());
                            logger.info("Password changed");
                            passwordChangeSuccessful();
                        }
                        else{
                            showError("New password is not strong enough","Contain >= 1: [a-z],[A-Z],[0-9], and has 8 to 20 digits");
                        }

                    }
                    else
                        showError("Old password ERROR","Old password is not matching");

                } catch (SQLException var2) {
                    System.out.println("SQLException: " + var2.getMessage());
                    System.out.println("SQLState: " + var2.getSQLState());
                    System.out.println("VendorError: " + var2.getErrorCode());
                    var2.printStackTrace();
                }
            } else
                showError("Repeat password ERROR","Repeat password is not matching with new password");
        }
    }

    public void setCurrentUser(User user){
        this.currentUser = user;
    }

    private void showError(String header, String content){
        errorAlert.setHeaderText(header);
        errorAlert.setContentText(content);
        errorAlert.showAndWait();
    }

    private static boolean isValidPassword(String password){
        String regex = "^(?=.*[0-9])"
                + "(?=.*[a-z])(?=.*[A-Z])"
                + "(?=\\S+$).{8,20}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(password);
        return m.matches();
    }

    private void passwordChangeSuccessful(){
        newPassword.setText("");
        oldPassword.setText("");
        repeatPassword.setText("");
        successfulAlert.showAndWait();
    }

    private void setupAlerts(){
        successfulAlert = new Alert(Alert.AlertType.CONFIRMATION);
        successfulAlert.setHeaderText("Successful change of password");
        successfulAlert.setContentText("Password was successfully changed");
        errorAlert = new Alert(Alert.AlertType.ERROR);
    }
}
