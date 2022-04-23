package com.example.spokojni.frontend;

import com.example.spokojni.backend.User;
import com.example.spokojni.backend.db.DB;
import com.example.spokojni.backend.users.Student;
import com.example.spokojni.backend.users.Teacher;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.ResourceBundle;

public class RegisterPersonController  implements Initializable  {
    private ResourceBundle rb = ResourceBundle.getBundle("com.example.spokojni.messages", Locale.getDefault());
    static Logger logger = LogManager.getLogger(RegisterPersonController.class);

    public RegisterPersonController() {
        successfulAlert = new Alert(Alert.AlertType.CONFIRMATION);
        successfulAlert.setHeaderText(rb.getString("Successful_registration"));
        errorAlert = new Alert(Alert.AlertType.ERROR);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.info(rb.getString("What_language"));
        comboBox.getItems().add(rb.getString("Student"));
        comboBox.getItems().add(rb.getString("Teacher"));
        comboBox.setValue(rb.getString("Student"));
    }
    public void setAdmin(AdminViewController admin){
        this.admin=admin;
    }

    private final Alert successfulAlert;
    private final Alert errorAlert;
    private boolean added= false;
    private AdminViewController admin;

    @FXML
    private ComboBox<String> comboBox;

    @FXML
    private TextField userName;

    @FXML
    private Button generatePassword;

    @FXML
    private Button saveUser;

    @FXML
    private TextField generatedPassword;

    @FXML
    private TextField userEmail;

    @FXML
    private TextField nickName;

    @FXML
    private void onMailChanged() {
        String email = userEmail.getText();
        int endpoint = email.length();
        for (int i = 0; i < email.length(); i++) {
            if (email.charAt(i) == '@') {
                endpoint = i;
                break;
            }
        }
        String nick = email.substring(0, endpoint);
        nickName.setText(nick);
    }

    public static String generateNewPass() {
        int length = 20;
        String capitalCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
        String specialCharacters = "!@#$";
        String numbers = "1234567890";
        String combinedChars = capitalCaseLetters + lowerCaseLetters + specialCharacters + numbers;
        Random random = new Random();
        char[] password = new char[length];

        password[0] = lowerCaseLetters.charAt(random.nextInt(lowerCaseLetters.length()));
        password[1] = capitalCaseLetters.charAt(random.nextInt(capitalCaseLetters.length()));
        password[2] = specialCharacters.charAt(random.nextInt(specialCharacters.length()));
        password[3] = numbers.charAt(random.nextInt(numbers.length()));

        for (int i = 4; i < length; i++) {
            password[i] = combinedChars.charAt(random.nextInt(combinedChars.length()));
        }
        logger.info("password generated");
        return String.valueOf(password);
    }

    @FXML
    private void generatePassword() {
        generatedPassword.setText(generateNewPass());
    }

    @FXML
    private void saveUser() throws SQLException, IOException {
        if(added)
            registerAnotherUser();
        else {
            if (checkValues()) {
                User user;
                if (Objects.equals(comboBox.getValue(), rb.getString("Teacher")))
                    user = new Teacher(0, userName.getText(), userEmail.getText(), nickName.getText());
                else
                    user = new Student(0, userName.getText(), userEmail.getText(), nickName.getText());
                try {
                    DB.makeConn();
                } catch (Exception var3) {
                    var3.printStackTrace();
                }
                //registrationSuccessful();
                try {
                    if (DB.addUser(user, generatedPassword.getText()))
                        registrationSuccessful();
                    else
                        registrationFailed();

                } catch (SQLException var2) {
                    System.out.println("SQLException: " + var2.getMessage());
                    System.out.println("SQLState: " + var2.getSQLState());
                    System.out.println("VendorError: " + var2.getErrorCode());
                    logger.error("No database connection");
                    var2.printStackTrace();
                }
            }
        }
        //
    }

    private void registrationFailed(){
        logger.info(" Registration failed");
        errorAlert.setHeaderText(rb.getString("Wrong_email"));
        errorAlert.setContentText(rb.getString("Email_taken"));
        errorAlert.showAndWait();
    }

    private void registrationSuccessful() {
        logger.info("Registration successful");
        admin.refreshUsers();
        added=true;
        successfulAlert.setContentText(rb.getString("Login") + ": " + nickName.getText() + " \n" + rb.getString("Password") + ": " + generatedPassword.getText());
        successfulAlert.showAndWait();
        saveUser.setText(rb.getString("Register_another_user"));
        comboBox.setEditable(false);
        userName.setEditable(false);
        userEmail.setEditable(false);
        generatePassword.setVisible(false);
    }

    private void registerAnotherUser(){
        comboBox.setEditable(true);
        userName.setEditable(true);
        userEmail.setEditable(true);
        generatePassword.setVisible(true);
        added=false;
        userName.setText("");
        generatedPassword.setText("");
        userEmail.setText("");
        nickName.setText("");
        saveUser.setText(rb.getString("Save_user"));
    }

    private boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    private boolean isPasswordGenerated(){
        if(!generatedPassword.getText().isEmpty())
            return  true;
        else
            return false;
    }

    private boolean checkValues(){
        if(!userEmail.getText().isEmpty()) {
            if(!userName.getText().isEmpty()) {
                if (isValidEmailAddress(userEmail.getText())) {
                    if (isPasswordGenerated()) {
                        return true;
                    } else {
                        errorAlert.setHeaderText(rb.getString("Password_not_generated"));
                        errorAlert.setContentText("Password_not_generated_long");
                    }
                } else {
                    errorAlert.setHeaderText(rb.getString("Email_not_valid"));
                    errorAlert.setContentText(rb.getString("Email_not_valid_long"));
                }
            }else{
                errorAlert.setHeaderText(rb.getString("Name_not_valid"));
                errorAlert.setContentText(rb.getString("Name_not_valid_long"));
            }
        }else{
            errorAlert.setHeaderText(rb.getString("Email_not_valid"));
            errorAlert.setContentText(rb.getString("Email_not_valid_long"));
        }
        errorAlert.showAndWait();
        return false;
    }
}