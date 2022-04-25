package com.example.spokojni.frontend;

import com.example.spokojni.MainApplication;
import com.example.spokojni.backend.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class ChangePassword {
    public ChangePassword(User currentUser) throws IOException {
        //logger.info("log_user_id:" + currentUser.getId() + "Show profile");

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("change-password-dialog.fxml"));
        ResourceBundle rb = (ResourceBundle.getBundle("com.example.spokojni.messages", Locale.getDefault()));
        fxmlLoader.setResources(rb);
        DialogPane dialogPane = fxmlLoader.load();
        ChangePasswordPopupController profilePopupController = fxmlLoader.getController();
        profilePopupController.setCurrentUser(currentUser);
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setDialogPane(dialogPane);
        dialog.setTitle(rb.getString("Profile"));

        dialog.showAndWait();
    }
}
