package com.example.spokojni.frontend;

import com.example.spokojni.backend.User;
import com.example.spokojni.backend.db.DB;
import com.example.spokojni.backend.users.Admin;
import com.example.spokojni.backend.users.Student;
import com.example.spokojni.backend.users.Teacher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;


public class LoginController implements Initializable {
    private Logger logger = LogManager.getLogger(LoginController.class);
    private User user;
    private Alert errorAlert;
    private ResourceBundle rb = ResourceBundle.getBundle("com.example.spokojni.messages", Locale.getDefault());

    public User getUser() {
        return user;
    }

    @FXML
    private Label loginLabel;

    @FXML
    private Button loginButton;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private ComboBox<String> language;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        language.getItems().add("Slovenčina");
        language.getItems().add("English");
        language.getItems().add("Deutsch");
        language.setValue("English");

        errorAlert = new Alert(Alert.AlertType.ERROR);
        setErrorPopupText();
    }

    @FXML
    protected void loginClick() throws IOException {

        try {
            DB.makeConn();
            if(DB.checkPassword(username.getText(), password.getText())) {
                logger.info("User" + username.getText() + "logged in");
                user = DB.getUserByLogin(username.getText());
            }
        } catch (SQLException var2) {
            System.out.println("SQLException: " + var2.getMessage());
            System.out.println("SQLState: " + var2.getSQLState());
            System.out.println("VendorError: " + var2.getErrorCode());
            logger.warn("Cannot login user " + var2);
            var2.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("No database connection " + e);
        }

        setLanguage();

        if (user instanceof Student) {      //ak sa prihlasil student

            logger.info("Student " + user.getName() + " logged in");
            ChangeWindowController controller = new ChangeWindowController("student-view.fxml", Locale.getDefault());

            StudentViewController studentViewController = controller.getFxmlLoader().getController();
            studentViewController.setCurrentUser(user);
            controller.changeWindow(loginButton);
        }
        else if (user instanceof Teacher) {     //ak sa prihlasil ucitel

            logger.info("Teacher " + user.getName() + " logged in");
            ChangeWindowController controller = new ChangeWindowController("teacher-view.fxml", Locale.getDefault());

            TeacherViewController teacherViewController = controller.getFxmlLoader().getController();
            teacherViewController.setCurrentUser(user);
            controller.changeWindow(loginButton);
        }
        else if (user instanceof Admin) {           //ak sa prihlasil admin

            logger.info("Admin logged in");
            ChangeWindowController controller = new ChangeWindowController("admin-view.fxml", Locale.getDefault());

            AdminViewController adminViewController = controller.getFxmlLoader().getController();
            adminViewController.setCurrentUser(user);
            controller.changeWindow(loginButton);
        }
        else{       //ked su pihlasovacie udaje zle zadane
            System.out.println("Login Error!!");
            logger.warn("Login failed");
            errorAlert.showAndWait();
        }
    }

    private void setLanguage() {        // zmena jazyka
        if (Objects.equals(language.getSelectionModel().getSelectedItem(), "Slovenčina"))
            Locale.setDefault(new Locale("sk", "SK"));
        else if (Objects.equals(language.getSelectionModel().getSelectedItem(), "English"))
            Locale.setDefault(new Locale("en", "UK"));
        else if (Objects.equals(language.getSelectionModel().getSelectedItem(), "Deutsch"))
            Locale.setDefault(new Locale("de", "DE"));
    }

    @FXML
    private void languageSelected() {  // on action listener, ktory dynamicky preklada prihlasovacie okno
        setLanguage();
        rb = ResourceBundle.getBundle("com.example.spokojni.messages", Locale.getDefault());
        loginButton.setText(rb.getString("Continue"));
        username.setPromptText(rb.getString("Name"));
        password.setPromptText(rb.getString("Password"));
        loginLabel.setText(rb.getString("LOGIN"));
        setErrorPopupText();

    }

    private void setErrorPopupText() {
        errorAlert.setHeaderText(rb.getString("Error"));
        errorAlert.setContentText(rb.getString("Login_error"));
    }
}
