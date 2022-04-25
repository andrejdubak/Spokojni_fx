package com.example.spokojni.frontend;

import com.example.spokojni.MainApplication;
import com.example.spokojni.backend.User;
import com.example.spokojni.backend.UserTable;
import com.example.spokojni.backend.db.DB;
import com.example.spokojni.backend.users.Student;
import com.example.spokojni.backend.users.Teacher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class AdminViewController implements Initializable {

    private Logger logger = LogManager.getLogger(AdminViewController.class);

    private User currentUser; // Momentalne prihlaseny pouzivatel (Admin)
    private final ArrayList<Student> students = new ArrayList<>();
    private final ArrayList<Teacher> teachers = new ArrayList<>();
    private final ArrayList<UserTable> users = new ArrayList<>();
    private final ObservableList<UserTable> user = FXCollections.observableArrayList(users);
    private ResourceBundle rb = ResourceBundle.getBundle("com.example.spokojni.messages", Locale.getDefault());

    @FXML
    private CheckBox showS;

    @FXML
    private CheckBox showT;

    @FXML
    private Button Profile;

    @FXML
    private TableView<UserTable> Table;

    @FXML
    private TableColumn<UserTable, String> emailTable;

    @FXML
    private TableColumn<UserTable, String> nameTable;

    @FXML
    private TableColumn<UserTable, String> roleTable;

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

    //Scena sa prepne na uvodnu obrazovku a Admin je odlhaseny zo systemu.
    @FXML
    private void logoutClick() throws IOException {
        // Vytvorenie logu pri odhlaseni Admina
        logger.info("log_user_id:" + currentUser.getId() + "Admin logged out");
        new ChangeWindowController("login-view.fxml", Locale.getDefault()).changeWindow(logOut);

    }
    // Nacitanie vsetkych ucitelov z databazy
    private void loadTeachers() {
        try {
            DB.makeConn();  // Nadviazanie spojenia s databazou
        } catch (Exception var3) {
            var3.printStackTrace();
            // Vytvorenie logu v pripade neuspesneho pripojenia
            logger.error("log_user_id:" + currentUser.getId() + "No database connection " + var3);
        }

        try {
            teachers.addAll(DB.getTeachers()); // Pridanie vsetkych ucitelov do ArrayList teachers

        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());
            e.printStackTrace();
            // Vytvorenie logu v pripade chyby
            logger.warn("log_user_id:" + currentUser.getId() + "Cannot get teachers " + e);
        }

        for (User t : teachers) { // Pridanie ucitelov do listu ktory sa zobrazi v tabulke
            user.add(new UserTable(t.getName(), t.getEmail(), "Teacher", t.getId()));
        }
    }
    // Nacitanie studentov z databazy
    private void loadStudents() {
        try {
            DB.makeConn(); // Nadviazanie spojenia s databazou
        } catch (Exception var3) {
            var3.printStackTrace(); // Vytvorenie logu v pripade neuspesneho pripojenia
            logger.error("log_user_id:" + currentUser.getId() + "No database connection " + var3);
        }

        try {
            students.addAll(DB.getStudents()); // Pridanie vsetkych studentov do ArrayList students
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());
            e.printStackTrace();
            // Vytvorenie logu v pripade chyby
            logger.warn("log_user_id:" + currentUser.getId() + "Cannot get students " + e);
        }
        for (User s : students) {
            user.add(new UserTable(s.getName(), s.getEmail(), "Student", s.getId()));  // Pridanie studentov do listu ktory sa zobrazi v tabulke
        }

    }

    private void showUsers() { // Zobrazenie vsetkych pouzivatelov v tabulke
        user.clear();
        teachers.clear();
        students.clear();

        loadStudents();
        loadTeachers();
    }

    private void showStudents() { // Zobrazenie iba studentov v tabulke
        user.clear();
        students.clear();
        loadStudents();
    }

    private void showTeachers() { // Zobrazenie iba ucitelov v tabulke
        user.clear();
        teachers.clear();
        loadTeachers();
    }

    @FXML
    private void registerPersonClick() throws IOException { // Registracia novej osoby
        logger.info("log_user_id:" + currentUser.getId() + "New person register"); // Vytvorenie logu z registracie
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("register-person-view.fxml"));
        fxmlLoader.setResources(rb);
        Parent dialogPane = fxmlLoader.load();
        RegisterPersonController registerPersonController = fxmlLoader.getController();
        registerPersonController.setAdmin(this);
        Scene scene = new Scene(dialogPane, 600, 400);
        Stage stage = new Stage();
        stage.setTitle(rb.getString("User_registration"));
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void changePasswordClick() throws IOException { // Zobrazenie okna na zmenu hesla
        new ChangePassword(currentUser);
    }

    @FXML
    private void exportClick() throws ParserConfigurationException { // Exportovanie pouzivatelov do xml suboru
        int[] selectedUsers = new int[user.size()];
        for (int i = 0; i < user.size(); i++) {
            selectedUsers[i] = user.get(i).getId();
        }
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("exportedUsers");
        doc.appendChild(rootElement);
        Element[] users = new Element[selectedUsers.length];
        Element[] names = new Element[selectedUsers.length];
        Element[] emails = new Element[selectedUsers.length];
        Element[] logins = new Element[selectedUsers.length];
        Element[] passwords = new Element[selectedUsers.length];
        Element[] roles = new Element[selectedUsers.length];
        try {
            DB.makeConn();
            for (int i = 0; i < selectedUsers.length; i++) {
                ResultSet rs = DB.getUserResultSet(selectedUsers[i]);
                rs.next();
                users[i] = doc.createElement("user");
                rootElement.appendChild(users[i]);
                users[i].setAttribute("id", Integer.toString(selectedUsers[i]));

                names[i] = doc.createElement("name");
                names[i].setTextContent(rs.getString(2));
                users[i].appendChild(names[i]);

                emails[i] = doc.createElement("email");
                emails[i].setTextContent(rs.getString(3));
                users[i].appendChild(emails[i]);

                logins[i] = doc.createElement("login");
                logins[i].setTextContent(rs.getString(4));
                users[i].appendChild(logins[i]);

                passwords[i] = doc.createElement("pass");
                passwords[i].setTextContent(rs.getString(5));
                users[i].appendChild(passwords[i]);

                roles[i] = doc.createElement("role");
                roles[i].setTextContent(Integer.toString(rs.getInt(6)));
                users[i].appendChild(roles[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn(e);
        }
        try (FileOutputStream output = new FileOutputStream(".\\exported.xml")) {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(output);
            transformer.transform(source, result);
            logger.info("log_user_id:" + currentUser.getId() + "Users exported");
        } catch (IOException | TransformerException e) {
            logger.warn("log_user_id:" + currentUser.getId() + "Export was unsuccessful " + e);
            e.printStackTrace();
        }
    }

    @FXML
    private void importClick() { // Importovanie pouzivatelov z xml suboru
        FileDialog dialog = new FileDialog((Frame) null, rb.getString("Select_file_to_open"));
        dialog.setMode(FileDialog.LOAD);
        dialog.setVisible(true);
        String path = dialog.getDirectory() + dialog.getFile();
        if (path.endsWith(".xml")) {
            try {
                File file = new File(path);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(file);
                doc.getDocumentElement().normalize();
                NodeList nodeList = doc.getElementsByTagName("user");
                User[] importedUsers = new User[nodeList.getLength()];
                logger.info("log_user_id:" + currentUser.getId() + "Users imported");
                DB.makeConn();
                for (int itr = 0; itr < nodeList.getLength(); itr++) {
                    Node node = nodeList.item(itr);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) node;
                        importedUsers[itr] = new User(
                                Integer.parseInt(eElement.getAttribute("id")),
                                eElement.getElementsByTagName("name").item(0).getTextContent(),
                                eElement.getElementsByTagName("email").item(0).getTextContent(),
                                eElement.getElementsByTagName("login").item(0).getTextContent()
                        );
                        DB.addUserImportWithHash(importedUsers[itr],
                                eElement.getElementsByTagName("pass").item(0).getTextContent(),
                                Integer.parseInt(eElement.getElementsByTagName("role").item(0).getTextContent()));
                    }
                }
                refreshUsers();
            } catch (Exception e) {
                logger.warn("log_user_id:" + currentUser.getId() + "Import was unsuccessful " + e);
                e.printStackTrace();
            }
        } else {
            logger.warn("log_user_id:" + currentUser.getId() + "Import was unsuccessful");
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        nameTable.setCellValueFactory(new PropertyValueFactory<UserTable, String>("name")); // Inicializovanie jednotlivych stlpcov v tabulke
        emailTable.setCellValueFactory(new PropertyValueFactory<UserTable, String>("email"));
        roleTable.setCellValueFactory(new PropertyValueFactory<UserTable, String>("role"));
        Table.setItems(user);
        try {
            Table.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && !Table.getSelectionModel().isEmpty()) { // Vyber pouzivatela v tabulke na jeden klik

                    try {
                        chosenUser(Table.getSelectionModel().getSelectedItem());
                        logger.info("log_user_id:" + currentUser.getId() + "User selected"); // Vytvorenie logu pri vybrani pouzivatela
                    } catch (IOException e) {
                        e.printStackTrace();
                        logger.warn("log_user_id:" + currentUser.getId() + e); // Log v pripade chyby
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("log_user_id:" + currentUser.getId() + "Problem with selection " + e); // Log v pripade chyby
        }


        FilteredList<UserTable> filterUsers = new FilteredList<>(user, b -> true);


        search.textProperty().addListener((observable, oldValue, newValue) -> {
            filterUsers.setPredicate(userName -> {

                if (newValue.isEmpty() || newValue.isBlank() || newValue == null) {
                    return true;
                }
                Pattern patternName = Pattern.compile("name:", Pattern.CASE_INSENSITIVE); // Vzor regexu na vyhladavanie podla mena
                Pattern patternEmail = Pattern.compile("email:", Pattern.CASE_INSENSITIVE); // Vzor regexu na vyhladanie podla emailu

                String searchedValue = newValue.toLowerCase(); // Vstup od pouzivatela aplikacie

                Matcher matcherName = patternName.matcher(searchedValue.toLowerCase());
                Matcher matcherEmail = patternEmail.matcher(searchedValue.toLowerCase());

                boolean userFound = matcherName.find();
                boolean emailFound = matcherEmail.find();


                if (userFound && userName.getName().toLowerCase().contains(searchedValue.replaceFirst("name: ", ""))) {
                    //Vrati vsetky zaznamy ktore obsahuju napisane meno
                    return true;
                }
                if (emailFound && userName.getEmail().toLowerCase().contains(searchedValue.replaceFirst("email: ", ""))) {
                    //Vrati vsetky zaznamy ktore obsahuju napisany email
                    return true;
                }

                return false;
            });

        });
        SortedList<UserTable> sortedUser = new SortedList<>(filterUsers);

        sortedUser.comparatorProperty().bind(Table.comparatorProperty());

        Table.setItems(sortedUser); // Zobrazenie vysledkov do tabulky

    }

    private void chosenUser(UserTable user) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("admin-popup.fxml"));
        fxmlLoader.setResources(rb);
        DialogPane dialogPane = fxmlLoader.load();
        AdminPopupController adminPopupController = fxmlLoader.getController();
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setDialogPane(dialogPane);
        dialog.setTitle(rb.getString("Selected_user"));
        adminPopupController.setCurrentUser(user, dialog, exportPeople, this);

        dialog.showAndWait();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public void refreshUsers() {
        loadUsers();
    }

    @FXML
    private void loadUsers() { // Zobrazenie pouzivatelov podla check box-u
        if (showS.isSelected() && showT.isSelected()) {
            showUsers();
        } else {
            if (showS.isSelected())
                showStudents();
            if (showT.isSelected())
                showTeachers();
        }
        if (!showS.isSelected() && !showT.isSelected()) {
            user.clear();
        }
    }
}