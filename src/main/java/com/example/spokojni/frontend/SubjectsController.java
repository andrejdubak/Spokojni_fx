package com.example.spokojni.frontend;

import com.example.spokojni.backend.Subject;
import com.example.spokojni.backend.UserTable;
import com.example.spokojni.backend.db.DB;
import com.example.spokojni.backend.users.Teacher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class SubjectsController implements Initializable  {
    private Logger logger = LogManager.getLogger(SubjectsController.class);
    @FXML
    private TableColumn<Subject,String> table_column;

    @FXML
    private Button add_subject;

    @FXML
    private TableView<Subject> table;

    @FXML
    private TextField subject_name;
    private Teacher teacher;
    private final ArrayList<Subject> load_subjects = new ArrayList<>();
    private final ObservableList<Subject> subjects = FXCollections.observableArrayList(load_subjects);
    private final Alert errorAlert;
    private ResourceBundle rb = ResourceBundle.getBundle("com.example.spokojni.messages", Locale.getDefault());

    public SubjectsController(){
        errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText(rb.getString("ERROR_new_subject"));
        errorAlert.setContentText(rb.getString("Subject_already_exists"));;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        table_column.setCellValueFactory(new PropertyValueFactory<>("name"));
        table.setItems(subjects);

        try {
            table.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && !table.getSelectionModel().isEmpty()) {
                    deleteSubject(table.getSelectionModel().getSelectedItem());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("log_user_id:" + teacher.getId() + "Cannot select user" + e);
        }
    }

    public void setCurrentUser(UserTable user) {
        this.teacher = new Teacher(user.getId(), user.getName(), user.getEmail(), "");
    }

    @FXML
    public void addSubject() throws SQLException {
        if(!subject_name.getText().isEmpty()){
            Subject subject = new Subject(0,subject_name.getText(),teacher);

            try {
                DB.makeConn();
            } catch (Exception var3) {
                var3.printStackTrace();
                logger.error("log_user_id:" + teacher.getId() + "No database connection" + var3);
            }
            try {
                if(!DB.addSubject(subject))
                    objectAlreadyExist();
                else {
                    loadSubjects();
                    subject_name.setText("");
                }
            } catch (SQLException var2) {
                System.out.println("SQLException: " + var2.getMessage());
                System.out.println("SQLState: " + var2.getSQLState());
                System.out.println("VendorError: " + var2.getErrorCode());
                var2.printStackTrace();
            }
        }

    }

    private void objectAlreadyExist() {
        errorAlert.showAndWait();
    }

    @FXML
    private void loadSubjects(){
        load_subjects.clear();
        subjects.clear();
        try {
            DB.makeConn();
        } catch (Exception var3) {
            var3.printStackTrace();
            logger.error("log_user_id:" + teacher.getId() + "No database connection" + var3);
        }

        try {
            load_subjects.addAll(DB.getSubjectsByTeacherId(teacher.getId()));
            logger.info("log_user_id:" + teacher.getId() + "Subjects loaded");
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());
            e.printStackTrace();
            logger.warn("log_user_id:" + teacher.getId() + "Cannot load subjects" + e);
        }
        for (Subject s : load_subjects){
            subjects.add(new Subject(s.getId(),s.getName(), s.getMaster()));
        }
    }

    private void deleteSubject(Subject subject) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(rb.getString("Confirmation_dialog"));
        alert.setHeaderText(rb.getString("Deleting_subject"));
        alert.setContentText(rb.getString("Sure_delete2")+subject.getName()+rb.getString("End2"));
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            try {
                DB.makeConn();
            } catch (Exception var3) {
                var3.printStackTrace();
                logger.error("log_user_id:" + teacher.getId() + "No database connection" + var3);
            }
            try {
                logger.info("log_user_id:" + teacher.getId() + "Subject deleted" + subject.getName());
                DB.delete(subject);
                loadSubjects();
            } catch (SQLException var2) {
                System.out.println("SQLException: " + var2.getMessage());
                System.out.println("SQLState: " + var2.getSQLState());
                System.out.println("VendorError: " + var2.getErrorCode());
                var2.printStackTrace();
                logger.warn("log_user_id:" + teacher.getId() + "Cannot delete subject" + var2);
            }
        }
    }
}
