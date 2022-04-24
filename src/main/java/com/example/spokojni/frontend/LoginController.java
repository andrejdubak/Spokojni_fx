package com.example.spokojni.frontend;

import com.example.spokojni.backend.User;
import com.example.spokojni.backend.db.DB;
import com.example.spokojni.backend.users.Admin;
import com.example.spokojni.backend.users.Student;
import com.example.spokojni.backend.users.Teacher;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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

    public User getUser() {
        return user;
    }

    @FXML
    private Button LoginClick;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private ChoiceBox<String> language;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        language.getItems().add("Slovenčina");
        language.getItems().add("English");
        language.getItems().add("Deutsch");
        language.setValue("Slovenčina");
    }

    @FXML
    protected void loginClick() throws IOException {
        user = new Student(4,"admin"," "," "); //tu zmenit pre login do ineho typu usera

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
            logger.warn("Cannot login user" + var2);
            var2.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("No database connection" + e);
        }
        if (Objects.equals(language.getSelectionModel().getSelectedItem(), "Slovenčina"))
            Locale.setDefault(new Locale("sk", "SK"));
        else if (Objects.equals(language.getSelectionModel().getSelectedItem(), "English"))
            Locale.setDefault(new Locale("en", "UK"));
        else if (Objects.equals(language.getSelectionModel().getSelectedItem(), "Deutsch"))
            Locale.setDefault(new Locale("de", "DE"));
        if (user instanceof Student) {

            logger.info("Student" + user.getName() + "logged in");
            ChangeWindowController controller = new ChangeWindowController("student-view.fxml", Locale.getDefault());

            StudentViewController studentViewController = controller.getFxmlLoader().getController();
            studentViewController.setCurrentUser(user);
            controller.changeWindow(LoginClick);
        }
        else if (user instanceof Teacher) {

            logger.info("Teacher" + user.getName() + "logged in");
            ChangeWindowController controller = new ChangeWindowController("teacher-view.fxml", Locale.getDefault());

            TeacherViewController teacherViewController = controller.getFxmlLoader().getController();
            teacherViewController.setCurrentUser(user);
            controller.changeWindow(LoginClick);
        }
        else if (user instanceof Admin) {

            logger.info("Admin logged in");
            ChangeWindowController controller = new ChangeWindowController("admin-view.fxml", Locale.getDefault());

            AdminViewController adminViewController = controller.getFxmlLoader().getController();
            adminViewController.setCurrentUser(user);
            controller.changeWindow(LoginClick);
        }
        else{
            System.out.println("Login Error!!");
            logger.warn("Login failed");
        }
    }
}
