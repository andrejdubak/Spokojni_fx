package com.example.spokojni.frontend;

import com.example.spokojni.backend.User;
import com.example.spokojni.backend.db.DB;
import com.example.spokojni.backend.users.Admin;
import com.example.spokojni.backend.users.Student;
import com.example.spokojni.backend.users.Teacher;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {

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
    protected void loginClick() throws IOException {
        user = new Student(5,"admin"," "," "); //tu zmenit pre login do ineho typu usera
        //User user = null;
       // user = new Admin(3,"Admin ", "", "");
        try {
            if(DB.checkPassword(username.getText(), password.getText())) {
                user = DB.getUserByLogin(username.getText());
            };
        } catch (SQLException var2) {
            System.out.println("SQLException: " + var2.getMessage());
            System.out.println("SQLState: " + var2.getSQLState());
            System.out.println("VendorError: " + var2.getErrorCode());
            var2.printStackTrace();
        }

        if (user instanceof Student) {
            ChangeWindowController controller = new ChangeWindowController("student-view.fxml");
            StudentViewController studentViewController = controller.getFxmlLoader().getController();
            studentViewController.setCurrentUser(user);
            controller.changeWindow(LoginClick);
        }
        else if (user instanceof Teacher) {
            ChangeWindowController controller = new ChangeWindowController("teacher-view.fxml");
            TeacherViewController teacherViewController = controller.getFxmlLoader().getController();
            teacherViewController.setCurrentUser(user);
            controller.changeWindow(LoginClick);
        }
        else if (user instanceof Admin) {
            ChangeWindowController controller = new ChangeWindowController("admin-view.fxml");
            AdminViewController adminViewController = controller.getFxmlLoader().getController();
            adminViewController.setCurrentUser(user);
            controller.changeWindow(LoginClick);
        }
        else
            System.out.println("Login Error!!");
    }

    @FXML
    protected void registerClick() {};
}
