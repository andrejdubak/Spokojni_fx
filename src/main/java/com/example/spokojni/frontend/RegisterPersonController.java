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

    static Logger logger = LogManager.getLogger(RegisterPersonController.class);

    public RegisterPersonController() {
        successfulAlert = new Alert(Alert.AlertType.CONFIRMATION);
        successfulAlert.setHeaderText("Successful Registration");
        errorAlert = new Alert(Alert.AlertType.ERROR);

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        if (Locale.getDefault().equals(new Locale("en", "UK"))){
            logger.info("Language EN");
            comboBox.getItems().add("Student");
            comboBox.getItems().add("Teacher");
            comboBox.setValue("Student");
        }
        else if (Locale.getDefault().equals(new Locale("sk", "SK"))){
            logger.info("Language SK");
            comboBox.getItems().add("Študent");
            comboBox.getItems().add("Učiteľ");
            comboBox.setValue("Študent");
        }
        else if (Locale.getDefault().equals(new Locale("de", "DE"))){
            logger.info("Language DE");
            comboBox.getItems().add("Schüler");
            comboBox.getItems().add("Lehrer");
            comboBox.setValue("Schüler");
        }

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
                if (Objects.equals(comboBox.getValue(), "Teacher") || Objects.equals(comboBox.getValue(), "Učiteľ") || Objects.equals(comboBox.getValue(), "Lehrer"))
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

        if (Locale.getDefault().equals(new Locale("en", "UK"))){
            errorAlert.setHeaderText("Wrong email address");
            errorAlert.setContentText("Email address is already taken");
        }
        else if (Locale.getDefault().equals(new Locale("sk", "SK"))){
            errorAlert.setHeaderText("Zlá emailová adresa");
            errorAlert.setContentText("Emailová adresa je už používaná");
        }
        else if (Locale.getDefault().equals(new Locale("de", "DE"))){
            errorAlert.setHeaderText("Falsche E-Mail Adresse");
            errorAlert.setContentText("E-Mail-Adresse ist bereits vergeben");
        }

        errorAlert.showAndWait();
    }

    private void registrationSuccessful() {
        logger.info("Registration successful");
        admin.refreshUsers();
        added=true;
        if (Locale.getDefault().equals(new Locale("en", "UK"))){
            successfulAlert.setContentText("Login: " + nickName.getText() + " \npassword: " + generatedPassword.getText());
            successfulAlert.showAndWait();
            saveUser.setText("Register another user");
        }
        else if (Locale.getDefault().equals(new Locale("sk", "SK"))){
            successfulAlert.setContentText("Meno: " + nickName.getText() + " \nHeslo: " + generatedPassword.getText());
            successfulAlert.showAndWait();
            saveUser.setText("Registrovať ďalšieho používateľa");
        }
        else if (Locale.getDefault().equals(new Locale("de", "DE"))){
            successfulAlert.setContentText("Name: " + nickName.getText() + " \nPasswort: " + generatedPassword.getText());
            successfulAlert.showAndWait();
            saveUser.setText("Registrieren Sie einen anderen Benutzer");
        }
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
        if (Locale.getDefault().equals(new Locale("en", "UK"))){
            saveUser.setText("Save user");
        }
        else if (Locale.getDefault().equals(new Locale("sk", "SK"))){
            saveUser.setText("Uložiť používateľa");
        }
        else if (Locale.getDefault().equals(new Locale("de", "DE"))){
            saveUser.setText("Benutzer speichern");
        }
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
                        if (Locale.getDefault().equals(new Locale("en", "UK"))){
                            errorAlert.setHeaderText("Password not generated");
                            errorAlert.setContentText("Password should be generated before creating new user");
                        }
                        else if (Locale.getDefault().equals(new Locale("sk", "SK"))){
                            errorAlert.setHeaderText("Heslo nebolo vygenerované");
                            errorAlert.setContentText("Pred vytvorením používateľa vygeneruj nové heslo");
                        }
                        else if (Locale.getDefault().equals(new Locale("de", "DE"))){
                            errorAlert.setHeaderText("Passwort wurde nicht generiert");
                            errorAlert.setContentText("Das Passwort sollte generiert werden, bevor ein neuer Benutzer erstellt wird");
                        }
                    }
                } else {
                    if (Locale.getDefault().equals(new Locale("en", "UK"))){
                        errorAlert.setHeaderText("Email address not valid");
                        errorAlert.setContentText("Email address is not in the correct format");
                    }
                    else if (Locale.getDefault().equals(new Locale("sk", "SK"))){
                        errorAlert.setHeaderText("Email je neplatný");
                        errorAlert.setContentText("Email adresa nie je v správnom formáte");
                    }
                    else if (Locale.getDefault().equals(new Locale("de", "DE"))){
                        errorAlert.setHeaderText("Email Adresse nicht gültig");
                        errorAlert.setContentText("Die E-Mail-Adresse hat nicht das richtige Format");
                    }
                }
            }else{
                if (Locale.getDefault().equals(new Locale("en", "UK"))){
                    errorAlert.setHeaderText("Name not valid");
                    errorAlert.setContentText("Name cannot be empty");
                }
                else if (Locale.getDefault().equals(new Locale("sk", "SK"))){
                    errorAlert.setHeaderText("Meno nie je platné");
                    errorAlert.setContentText("Meno používateľa nemôže byť prázdne");
                }
                else if (Locale.getDefault().equals(new Locale("de", "DE"))){
                    errorAlert.setHeaderText("Name ungültig");
                    errorAlert.setContentText("Der Name darf nicht leer sein");
                }
            }
        }else{
            if (Locale.getDefault().equals(new Locale("en", "UK"))){
                errorAlert.setHeaderText("Email address not valid");
                errorAlert.setContentText("Email address cannot be empty");
            }
            else if (Locale.getDefault().equals(new Locale("sk", "SK"))){
                errorAlert.setHeaderText("Email je neplatný");
                errorAlert.setContentText("Email adresa nemôže byť prázdna");
            }
            else if (Locale.getDefault().equals(new Locale("de", "DE"))){
                errorAlert.setHeaderText("Email Adresse nicht gültig");
                errorAlert.setContentText("Die E-Mail-Adresse darf nicht leer sein");
            }
        }
        errorAlert.showAndWait();
        return false;
    }
}