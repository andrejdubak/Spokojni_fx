package com.example.spokojni.frontend;

import com.example.spokojni.backend.User;
import com.example.spokojni.backend.UserTable;
import com.example.spokojni.backend.db.DB;
import com.example.spokojni.backend.users.Student;
import com.example.spokojni.backend.users.Teacher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Objects;

public class AdminPopupController {

    private UserTable user;
    private Dialog<ButtonType> dialog;
    private Button button;

    @FXML
    private Button addSubject;

    @FXML
    private Label nameOfUser;

    @FXML
    private Button Login;

    @FXML
    private Button deleteUser;

    @FXML
    private Text name;

    @FXML
    private Button newPassword;

    public void setCurrentUser(UserTable user, Dialog<ButtonType> dialog, Button button){
        this.user=user;
        this.button=button;
        nameOfUser.setText(user.getName());
        if (Objects.equals(user.getRole(), "Student")) {
            addSubject.setVisible(false);
        }
        this.dialog=dialog;
    }

    @FXML
    private void deleteUser() {
        String choices[] = { "Yes", "No"};
        ChoiceDialog d = new ChoiceDialog(choices[1],choices);
        d.setHeaderText("Deleting user");
        d.setContentText("Are you sure you want to delete user");
        d.showAndWait();
        if(Objects.equals(d.getSelectedItem().toString(), "Yes")){
            System.out.println("deleted");
            try {
                DB.makeConn();
            } catch (Exception var3) {
                var3.printStackTrace();
            }
            try {
                DB.delete(user);
                dialog.close();
            } catch (SQLException var2) {
                System.out.println("SQLException: " + var2.getMessage());
                System.out.println("SQLState: " + var2.getSQLState());
                System.out.println("VendorError: " + var2.getErrorCode());
                var2.printStackTrace();
            }

        }

    }

    @FXML
    private void logInUser() throws IOException {
        dialog.close();
        if(Objects.equals(this.user.getRole(), "Student")) {
            ChangeWindowController controller = new ChangeWindowController("student-view.fxml", new Locale("en", "UK"));
            StudentViewController studentViewController = controller.getFxmlLoader().getController();
            Student student = new Student(this.user.getId(),this.user.getName(),this.user.getEmail(),"");
            studentViewController.setCurrentUser(student);
            controller.changeWindow(this.button);
        }
        else{
            ChangeWindowController controller = new ChangeWindowController("teacher-view.fxml", new Locale("en", "UK"));
            TeacherViewController teacherViewController = controller.getFxmlLoader().getController();
            Teacher teacher = new Teacher(this.user.getId(),this.user.getName(),this.user.getEmail(),"");
            teacherViewController.setCurrentUser(teacher);
            controller.changeWindow(this.button);
        }

    }
}
