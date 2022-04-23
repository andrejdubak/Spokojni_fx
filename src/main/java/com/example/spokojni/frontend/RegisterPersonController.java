package com.example.spokojni.frontend;

import com.example.spokojni.backend.User;
import com.example.spokojni.backend.db.DB;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Random;

public class RegisterPersonController {

    Logger logger = LogManager.getLogger(RegisterPersonController.class);

    public RegisterPersonController() {
        logger.info("Successful Registration");
        succesfullAlert = new Alert(Alert.AlertType.CONFIRMATION);
        succesfullAlert.setHeaderText("Successful Registration");
        errorAlert = new Alert(Alert.AlertType.ERROR);
    }

    private final Alert succesfullAlert;
    private final Alert errorAlert;
    @FXML
    private TextField userName;
    @FXML
    private CheckBox checkBox;

    @FXML
    private Button generatePassword;

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

    @FXML
    private void generatePassword() {
        logger.info("New password generated");
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
        generatedPassword.setText(String.valueOf(password));
    }

    @FXML
    private void changeRole() {
        if(checkBox.isSelected()) {
            checkBox.setText("Teacher");
            logger.info("Role teacher selected");
        }
        else {
            checkBox.setText("Student");
            logger.info("Role student selected");
        }
    }

    @FXML
    private void saveUser() throws SQLException, IOException {
        if (checkValues()) {
            User user = new User(0, userName.getText(), userEmail.getText(), nickName.getText());
            logger.info("User saved");
            try {
                DB.makeConn();
            } catch (Exception var3) {
                var3.printStackTrace();
            }
            registrationSuccessful();
            /*try {
                DB.add(user);
                DB.updatePassword(user, generatedPassword.getText());
                registrationSuccessful();
            } catch (SQLException var2) {
                System.out.println("SQLException: " + var2.getMessage());
                System.out.println("SQLState: " + var2.getSQLState());
                System.out.println("VendorError: " + var2.getErrorCode());
                var2.printStackTrace();
            }*/
        }
        //
    }

    private void registrationSuccessful() {
        logger.info("Registration successful");
        succesfullAlert.setContentText("nickName: "+nickName.getText()+" \npassword: "+generatedPassword.getText());
        userName.setText("");
        checkBox.setSelected(false);
        checkBox.setText("Student");
        generatedPassword.setText("");
        userEmail.setText("");
        nickName.setText("");
        succesfullAlert.showAndWait();
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
                        errorAlert.setHeaderText("Password not generated");
                        errorAlert.setContentText("Password should be generated before saving user");

                    }
                } else {
                    errorAlert.setHeaderText("Email address not valid");
                    errorAlert.setContentText("Email address is not in the correct format");

                }
            }else{
                errorAlert.setHeaderText("Name not valid");
                errorAlert.setContentText("Name cannot be empty");
            }
        }else{
            errorAlert.setHeaderText("Email address not valid");
            errorAlert.setContentText("Email address cannot be empty");
        }
        errorAlert.showAndWait();
        return false;
    }



}
