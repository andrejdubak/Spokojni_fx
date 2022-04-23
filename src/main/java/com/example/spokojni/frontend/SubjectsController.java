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
    Logger logger = LogManager.getLogger(SubjectsController.class);
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

    public SubjectsController(){
        errorAlert = new Alert(Alert.AlertType.ERROR);
        if (Locale.getDefault().equals(new Locale("en", "UK"))){
            errorAlert.setHeaderText("New subject error");
            errorAlert.setContentText("Subject with this name already exists");
        }
        else if (Locale.getDefault().equals(new Locale("sk", "SK"))){
            errorAlert.setHeaderText("Error: Nový predmet");
            errorAlert.setContentText("Predmet s týmto menom už existuje");
        }
        else if (Locale.getDefault().equals(new Locale("de", "DE"))){
            errorAlert.setHeaderText("Fehler bei neuem Schulfach");
            errorAlert.setContentText("Schulfach mit diesem Namen existiert bereits");
        }
        //loadSubjects();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // System.out.println(this.currentUser.getName());
        table_column.setCellValueFactory(new PropertyValueFactory<>("name"));
        table.setItems(subjects);

        try {
            table.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && !table.getSelectionModel().isEmpty()) {
                   // try {
                        //System.out.println(table.getSelectionModel().getSelectedItem().getName());
                        deleteSubject(table.getSelectionModel().getSelectedItem());
                   /* } catch (IOException e) {
                        e.printStackTrace();
                    }*/
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("log_user_id:" + teacher.getId() + "Cannot select user");
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
                logger.error("log_user_id:" + teacher.getId() + "No database connection");
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
            logger.error("log_user_id:" + teacher.getId() + "No database connection");
        }

        try {
            load_subjects.addAll(DB.getSubjectsByTeacherId(teacher.getId()));
            logger.info("log_user_id:" + teacher.getId() + "Subjects loaded");
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());
            e.printStackTrace();
            logger.warn("log_user_id:" + teacher.getId() + "Cannot load subjects");
        }
        for (Subject s : load_subjects){
            subjects.add(new Subject(s.getId(),s.getName(), s.getMaster()));
        }
    }

    private void deleteSubject(Subject subject) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        if (Locale.getDefault().equals(new Locale("en", "UK"))){
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("Deleting Subject");
            alert.setContentText("Are you sure you want to delete "+subject.getName()+" ?");
        }
        else if (Locale.getDefault().equals(new Locale("sk", "SK"))){
            alert.setTitle("Potvrdzujúci dialóg");
            alert.setHeaderText("Vymazávanie predmetu");
            alert.setContentText("Si si istý, že chceš vymazať "+subject.getName()+" ?");
        }
        else if (Locale.getDefault().equals(new Locale("de", "DE"))){
            alert.setTitle("Bestätigungsdialog");
            alert.setHeaderText("Schulfach löschen");
            alert.setContentText("Möchten Sie " +subject.getName()+" wirklich löschen?");
        }
        Optional<ButtonType> result = alert.showAndWait();
        System.out.println(subject.getId());
        System.out.println(subject.getMaster().getId());
        if (result.get() == ButtonType.OK){
            try {
                DB.makeConn();
            } catch (Exception var3) {
                var3.printStackTrace();
                logger.error("log_user_id:" + teacher.getId() + "No database connection");
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
                logger.warn("log_user_id:" + teacher.getId() + "Subject was not deleted");
            }
        }
    }
}
