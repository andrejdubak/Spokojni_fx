package com.example.spokojni.frontend;

import com.example.spokojni.backend.User;
import com.example.spokojni.backend.UserTable;
import com.example.spokojni.backend.db.DB;
import com.example.spokojni.backend.users.Student;
import com.example.spokojni.backend.users.Teacher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AdminViewController implements Initializable {
ArrayList<Student> students = new ArrayList<>();
ArrayList<Teacher> teachers = new ArrayList<>();
ArrayList<UserTable> users = new ArrayList<>();
    ObservableList<UserTable> student = FXCollections.observableArrayList(users);

    @FXML
    private Button Profile;

    @FXML
    private Button Students;

    @FXML
    private TableView<UserTable> Table;

    @FXML
    private TableColumn<UserTable, String> emailTable;

    @FXML
    private TableColumn<UserTable, String> nameTable;

    @FXML
    private TableColumn<UserTable, String> roleTable;

    @FXML
    private Button Teachers;

    @FXML
    private Button exportPeople;

    @FXML
    private Button importPeople;

    @FXML
    private Button logOut;

    @FXML
    private Text name;

    @FXML
    private Button registerPerson;

    @FXML
    private TextField search;

    @FXML
    void logoutClick(ActionEvent event) throws IOException {
        new ChangeWindowController(logOut, "login-view.fxml");

    }

    @FXML
    void showStudents(ActionEvent event) {
        student.clear();
        students.clear();
        Student();
    }

    @FXML
    void showTeachers(ActionEvent event) {
        student.clear();
        teachers.clear();
        Teacher();

    }
    private void Teacher(){

        try {
            teachers.addAll(DB.getTeachers());

        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());
            e.printStackTrace();
        }

        for (User t : teachers){
            student.add(new UserTable(t.getName(), t.getEmail(), "Teacher"));

        }
    }
    private void Student(){

        try {
            students.addAll(DB.getStudents());
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());
            e.printStackTrace();
        }
        for (User s : students){
            student.add(new UserTable(s.getName(), s.getEmail(), "Student"));
        }

    }
    @FXML
    void showUsers(ActionEvent event)  {
        student.clear();
        teachers.clear();
        students.clear();

        Student();
        Teacher();
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        nameTable.setCellValueFactory(new PropertyValueFactory<UserTable, String>("name"));
        emailTable.setCellValueFactory(new PropertyValueFactory<UserTable, String>("email"));
        roleTable.setCellValueFactory(new PropertyValueFactory<UserTable, String>("role"));
        Table.setItems(student);
    }
}
