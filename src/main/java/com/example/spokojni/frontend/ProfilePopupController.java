package com.example.spokojni.frontend;

import com.example.spokojni.backend.User;
import com.example.spokojni.backend.db.DB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;

import java.sql.SQLException;
import java.util.Objects;

public class ProfilePopupController {

    @FXML
    private Button savePassword;
    @FXML
    private PasswordField repeatPassword;
    @FXML
    private PasswordField newPassword;
    @FXML
    private PasswordField oldPassword;

    private User currentUser;
    private Alert successfulAlert;
    private Alert oldPasswordError;
    private Alert repeatPasswordError;
    private Alert newPasswordError;

    ObservableList<String> languages = FXCollections.observableArrayList("Slovak","English");

    @FXML
    private ChoiceBox<String> language;

    @FXML
    private void initialize() {
        language.setValue("English");
        language.setItems(languages);
        setupAlerts();
    }

    @FXML
    private void saveSettings() {
        if (newPassword.getText().isEmpty())
            newPasswordError.showAndWait();
        else {
            if (Objects.equals(repeatPassword.getText(), newPassword.getText())) {
                try {
                    DB.makeConn();
                } catch (Exception var3) {
                    var3.printStackTrace();
                }
                try {
                    if (DB.checkPassword(currentUser.getId(), oldPassword.getText())) {
                        DB.updatePassword(currentUser.getId(),newPassword.getText());
                        passwordChangeSuccessful();
                    }
                    else
                        oldPasswordError.showAndWait();

                } catch (SQLException var2) {
                    System.out.println("SQLException: " + var2.getMessage());
                    System.out.println("SQLState: " + var2.getSQLState());
                    System.out.println("VendorError: " + var2.getErrorCode());
                    var2.printStackTrace();
                }
            } else
                repeatPasswordError.showAndWait();
        }
    }

    @FXML
    private void changeLanguage(){
        System.out.println(language.getValue());
    }

    public void setCurrentUser(User user){
        this.currentUser = user;
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
        oldPasswordError = new Alert(Alert.AlertType.ERROR);
        oldPasswordError.setHeaderText("Old password ERROR");
        oldPasswordError.setContentText("Old password is not matching");
        repeatPasswordError = new Alert(Alert.AlertType.ERROR);
        repeatPasswordError.setHeaderText("Repeat password ERROR");
        repeatPasswordError.setContentText("Repeat password is not matching with new password");
        newPasswordError = new Alert(Alert.AlertType.ERROR);
        newPasswordError.setHeaderText("New password ERROR");
        newPasswordError.setContentText("New password cannot be empty");
    }
}
