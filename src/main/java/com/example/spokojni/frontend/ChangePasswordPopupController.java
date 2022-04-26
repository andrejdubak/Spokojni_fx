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

public class ChangePasswordPopupController {        // kontroler, ktory zabezpecuje zmenu hesla pre pouzivatela

    @FXML
    private Button savePassword;
    @FXML
    private PasswordField repeatPassword;
    @FXML
    private PasswordField newPassword;
    @FXML
    private PasswordField oldPassword;
    private Logger logger = LogManager.getLogger(ChangePasswordPopupController.class);
    private User currentUser;
    private Alert successfulAlert;
    private Alert errorAlert;
    private ResourceBundle rb = ResourceBundle.getBundle("com.example.spokojni.messages", Locale.getDefault());

    @FXML
    private void initialize() {
        setupAlerts();
    }

    @FXML
    private void saveSettings() {       // funkcia na ulozenie hesla do databazy
        if (newPassword.getText().isEmpty()) {  //testujeme ci nove heslo nieje prazdne
            logger.warn("log_user_id:" + currentUser.getId() + "No password");
            showError(rb.getString("ERROR_new_password"), rb.getString("New_password_not_empty"));
        }
        else {
            if (Objects.equals(repeatPassword.getText(), newPassword.getText())) {      // testujeme ci sa nove heslo zhoduje so zopakovanym heslom
                try {
                    DB.makeConn();
                } catch (Exception var3) {
                    var3.printStackTrace();
                    logger.error("log_user_id:" + currentUser.getId() + "No database connection " + var3);
                }
                try {
                    if (DB.checkPassword(currentUser.getId(), oldPassword.getText())) {     //kontrolujeme v databaze, ci sa zhoduje stare heslo
                        if(isValidPassword(newPassword.getText())) {        // testujeme ci nove heslo splna vsetky minimalne poziadavky
                            DB.updatePassword(currentUser.getId(), newPassword.getText());
                            logger.info("log_user_id:" + currentUser.getId() + "Password changed");
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
                    logger.warn("Cannot finish operation" + var2);
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

    private static boolean isValidPassword(String password){    // kontrola minimalnych poziadavok na heslo
        String regex = "^(?=.*[0-9])"   // aspon jedno cislo
                + "(?=.*[a-z])(?=.*[A-Z])"  //velke aj male pismeno
                + "(?=\\S+$).{8,20}$";  //medzi 8 az 20 znakov
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
