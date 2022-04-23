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
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
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
    private ResourceBundle rb = ResourceBundle.getBundle("com.example.spokojni.messages", Locale.getDefault());

    @FXML
    private void initialize() {

        setupAlerts();
    }

    @FXML
    private void saveSettings() {
        if (newPassword.getText().isEmpty()) {
            logger.warn("No password");
            showError(rb.getString("ERROR_new_password"), rb.getString("New_password_not_empty"));

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
                            showError(rb.getString("New_password_not_strong"),rb.getString("Contain_shit"));
                        }

                    }
                    else
                        showError(rb.getString("ERROR_old_password"),rb.getString("Old_password_not_match"));

                } catch (SQLException var2) {
                    System.out.println("SQLException: " + var2.getMessage());
                    System.out.println("SQLState: " + var2.getSQLState());
                    System.out.println("VendorError: " + var2.getErrorCode());
                    var2.printStackTrace();
                }
            } else
                showError(rb.getString("ERROR_repeat_password"),rb.getString("Repeat_password_not_match"));
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
        successfulAlert.setHeaderText(rb.getString("Password_changed_successfully"));
        successfulAlert.setContentText(rb.getString("Password_was_successfully_changed"));
        errorAlert = new Alert(Alert.AlertType.ERROR);
    }
}
