package com.example.spokojni.frontend;

import com.example.spokojni.MainApplication;
import com.example.spokojni.backend.UserTable;
import com.example.spokojni.backend.db.DB;
import com.example.spokojni.backend.users.Student;
import com.example.spokojni.backend.users.Teacher;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class AdminPopupController {

    Logger logger = LogManager.getLogger(AdminPopupController.class);
    private UserTable user;
    private Dialog<ButtonType> dialog;
    private Button button;
    private AdminViewController admin;
    private ResourceBundle rb = ResourceBundle.getBundle("com.example.spokojni.messages", Locale.getDefault());

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

    public void setCurrentUser(UserTable user, Dialog<ButtonType> dialog, Button button, AdminViewController admin ){
        this.user=user;
        this.button=button;
        this.admin=admin;
        nameOfUser.setText(user.getName());
        if (Objects.equals(user.getRole(), "Student")) {
            addSubject.setVisible(false);
        }
        this.dialog=dialog;
    }

    @FXML
    private void generateNewPassword() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("generate-password-popup.fxml"));
        fxmlLoader.setResources(rb);
        DialogPane dialogPane = fxmlLoader.load();
        GeneratePasswordController generatePasswordController = fxmlLoader.getController();
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setDialogPane(dialogPane);
        dialog.setTitle(rb.getString("Generate_password"));
        generatePasswordController.setCurrentUser(user,dialog);

        dialog.showAndWait();
    }

    @FXML
    private void deleteUser() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(rb.getString("Confirmation_dialog"));
        alert.setHeaderText(rb.getString("Deleting_user"));
        alert.setContentText(rb.getString("Sure_delete"));
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            try {
                DB.makeConn();
            } catch (Exception var3) {
                var3.printStackTrace();
            }
            try {
                logger.info("User" + user.getName() + "deleted");
                DB.delete(user);
                admin.refreshUsers();
                dialog.close();
            } catch (SQLException var2) {
                System.out.println("SQLException: " + var2.getMessage());
                System.out.println("SQLState: " + var2.getSQLState());
                System.out.println("VendorError: " + var2.getErrorCode());
                var2.printStackTrace();
                logger.error("No database connection");
            }
        }
    }

    @FXML
    private void logInUser() throws IOException {
        dialog.close();
        if(Objects.equals(this.user.getRole(), "Student")) {
            logger.info("Admin logged as" + user.getName());
            ChangeWindowController controller = new ChangeWindowController("student-view.fxml", Locale.getDefault());
            StudentViewController studentViewController = controller.getFxmlLoader().getController();
            Student student = new Student(this.user.getId(),this.user.getName(),this.user.getEmail(),"");
            studentViewController.setCurrentUser(student);
            controller.changeWindow(this.button);
        }
        else{
            logger.info("Admin logged as" + user.getName());
            ChangeWindowController controller = new ChangeWindowController("teacher-view.fxml", Locale.getDefault());
            TeacherViewController teacherViewController = controller.getFxmlLoader().getController();
            Teacher teacher = new Teacher(this.user.getId(),this.user.getName(),this.user.getEmail(),"");
            teacherViewController.setCurrentUser(teacher);
            controller.changeWindow(this.button);
        }

    }

    @FXML
    private void addUser() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("subjects-popup.fxml"));
        fxmlLoader.setResources(rb);
        DialogPane dialogPane = fxmlLoader.load();
        SubjectsController addSubjectController = fxmlLoader.getController();
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setDialogPane(dialogPane);
        dialog.setTitle(rb.getString("Add_subject"));
        addSubjectController.setCurrentUser(user);
        dialog.showAndWait();
    }
}
