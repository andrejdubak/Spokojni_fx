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
import javafx.event.ActionEvent;
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
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class AdminViewController implements Initializable {

    Logger logger = LogManager.getLogger(AdminViewController.class);

    private User currentUser;
    private final ArrayList<Student> students = new ArrayList<>();
    private final ArrayList<Teacher> teachers = new ArrayList<>();
    private final ArrayList<UserTable> users = new ArrayList<>();
    private final ObservableList<UserTable> student = FXCollections.observableArrayList(users);
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

    @FXML
    private void logoutClick() throws IOException{

        logger.info("log_user_id:" + currentUser.getId() +  "Admin logged out");
        new ChangeWindowController( "login-view.fxml", Locale.getDefault()).changeWindow(logOut);

    }

    private void loadTeachers(){
        try {
            DB.makeConn();
        } catch (Exception var3) {
            var3.printStackTrace();
            logger.error("log_user_id:" + currentUser.getId() + "No database connection");
        }

        try {
            teachers.addAll(DB.getTeachers());

        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());
            e.printStackTrace();
            logger.warn("log_user_id:" + currentUser.getId() + "No teachers found");
        }

        for (User t : teachers){
            student.add(new UserTable(t.getName(), t.getEmail(), "Teacher", t.getId()));
        }
    }
    private void loadStudents(){
        try {
            DB.makeConn();
        } catch (Exception var3) {
            var3.printStackTrace();
            logger.error("log_user_id:" + currentUser.getId() + "No database connection");
        }

        try {
            students.addAll(DB.getStudents());
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());
            e.printStackTrace();
            logger.warn("log_user_id:" + currentUser.getId() + "No students found");
        }
        for (User s : students){
            student.add(new UserTable(s.getName(), s.getEmail(), "Student", s.getId()));
        }

    }
    private void showUsers()  {
        student.clear();
        teachers.clear();
        students.clear();

        loadStudents();
        loadTeachers();
    }
    private void showStudents() {
        student.clear();
        students.clear();
        loadStudents();
    }

    private void showTeachers() {
         student.clear();
         teachers.clear();
         loadTeachers();
    }

    @FXML
    private void registerPersonClick() throws IOException{
        logger.info("log_user_id:" + currentUser.getId() + "New person register");
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
        //new ChangeWindowController(registerPerson, "register-person-view.fxml");
    }

    @FXML
    private void ProfileClick() throws IOException {

        logger.info("log_user_id:" + currentUser.getId() + "Show profile");
      //  FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("profile-dialog.fxml"));

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("change-password-dialog.fxml"));
        ResourceBundle rb =  (ResourceBundle.getBundle("com.example.spokojni.messages", Locale.getDefault()));
        fxmlLoader.setResources(rb);
        DialogPane dialogPane = fxmlLoader.load();
        ChangePasswordPopupController profilePopupController = fxmlLoader.getController();
        profilePopupController.setCurrentUser(currentUser);
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setDialogPane(dialogPane);
        dialog.setTitle(rb.getString("Profile"));

        Optional<ButtonType> clickedButton = dialog.showAndWait();

    }

    @FXML
    private void exportClick() throws ParserConfigurationException {
        User[] selectedUsers = new User[student.size()];
        for (int i = 0; i < student.size(); i++){
            selectedUsers[i] = new User(
                student.get(i).getId(),
                student.get(i).getName(),
                student.get(i).getEmail(),
                student.get(i).getRole()
            );
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
        for (int i = 0; i < selectedUsers.length; i++){
            users[i] = doc.createElement("user");
            rootElement.appendChild(users[i]);
            users[i].setAttribute("id", Integer.toString(selectedUsers[i].getId()));

            names[i] = doc.createElement("name");
            names[i].setTextContent(selectedUsers[i].getName());
            users[i].appendChild(names[i]);

            emails[i] = doc.createElement("email");
            emails[i].setTextContent(selectedUsers[i].getEmail());
            users[i].appendChild(emails[i]);

            logins[i] = doc.createElement("role");
            logins[i].setTextContent(selectedUsers[i].getLogin());
            users[i].appendChild(logins[i]);
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
            logger.warn("log_user_id:" + currentUser.getId() + "Export was unsuccessful");
            e.printStackTrace();
        }
    }

    @FXML
    private void importClick(){

        FileDialog dialog = new FileDialog((Frame)null, rb.getString("Select_file_to_open"));
        dialog.setMode(FileDialog.LOAD);
        dialog.setVisible(true);
        String path = dialog.getDirectory() + dialog.getFile();
        if (path.endsWith(".xml")) {
            try
            {
                File file = new File(path);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(file);
                doc.getDocumentElement().normalize();
                NodeList nodeList = doc.getElementsByTagName("user");
                User[] importedUsers = new User[nodeList.getLength()];
                logger.info("log_user_id:" + currentUser.getId() + "Users imported");
                for (int itr = 0; itr < nodeList.getLength(); itr++)
                {
                    Node node = nodeList.item(itr);
                    if (node.getNodeType() == Node.ELEMENT_NODE)
                    {
                        Element eElement = (Element) node;
                        importedUsers[itr] = new User(
                                Integer.parseInt(eElement.getAttribute("id")),
                                eElement.getElementsByTagName("name").item(0).getTextContent(),
                                eElement.getElementsByTagName("email").item(0).getTextContent(),
                                eElement.getElementsByTagName("role").item(0).getTextContent()
                        );
                    }
                }

            }
            catch (Exception e)
            {
                logger.warn("log_user_id:" + currentUser.getId() + "Import was unsuccessful");
                e.printStackTrace();
            }
            // TODO: mame user array importedUsers, ktory treba hodit do tabulky
        }
        else{
            // TODO: error, zvoleny subor nie je xml ;)
        }


    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
       // System.out.println(this.currentUser.getName());
        nameTable.setCellValueFactory(new PropertyValueFactory<UserTable, String>("name"));
        emailTable.setCellValueFactory(new PropertyValueFactory<UserTable, String>("email"));
        roleTable.setCellValueFactory(new PropertyValueFactory<UserTable, String>("role"));
        Table.setItems(student);
        try{
            Table.setOnMouseClicked( event -> {
                if( event.getClickCount() == 1  && !Table.getSelectionModel().isEmpty()) {

                    try {
                        chosenUser(Table.getSelectionModel().getSelectedItem());
                        logger.info("log_user_id:" + currentUser.getId() + "User selected");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }});
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("log_user_id:" + currentUser.getId() + "Problem with selection");
        }


        FilteredList<UserTable> filterUsers = new FilteredList<>(student, b -> true);


            search.textProperty().addListener((observable, oldValue, newValue) ->{
                filterUsers.setPredicate(userName ->{

                    if(newValue.isEmpty() || newValue.isBlank() || newValue == null){
                        return true;
                    }
                    Pattern patternName = Pattern.compile("name:", Pattern.CASE_INSENSITIVE);
                    Pattern patternEmail = Pattern.compile("email:", Pattern.CASE_INSENSITIVE);

                    String searchedName = newValue.toLowerCase();

                    Matcher matcherName = patternName.matcher(searchedName.toLowerCase());
                    Matcher matcherEmail = patternEmail.matcher(searchedName.toLowerCase());

                    boolean userFound = matcherName.find();
                    boolean emailFound = matcherEmail.find();


                    if (userFound && userName.getName().toLowerCase().contains(searchedName.replaceFirst("name: ", ""))){
                        logger.info("log_user_id:" + currentUser.getId() + "Filter by user name");
                        return true;
                    }
                    if(emailFound && userName.getEmail().toLowerCase().contains(searchedName.replaceFirst("email: ", ""))){
                        logger.info("log_user_id:" + currentUser.getId() + "Filter by user email");
                        return true;
                    }

                    return  false;
                });

            });
            SortedList<UserTable> sortedUser = new SortedList<>(filterUsers);

            sortedUser.comparatorProperty().bind(Table.comparatorProperty());

            Table.setItems(sortedUser);

    }

    private void chosenUser(UserTable user) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("admin-popup.fxml"));
        fxmlLoader.setResources(rb);
        DialogPane dialogPane = fxmlLoader.load();
        AdminPopupController adminPopupController = fxmlLoader.getController();
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setDialogPane(dialogPane);
        dialog.setTitle(rb.getString("Selected_user"));
        adminPopupController.setCurrentUser(user,dialog,exportPeople,this);

        dialog.showAndWait();
    }
    public void setCurrentUser(User user){
        this.currentUser=user;
    }

    public void refreshUsers(){
        loadUsers();
    }

    @FXML
    private void loadUsers() {
        if (showS.isSelected() && showT.isSelected()){
            showUsers();
        }
        else{
            if(showS.isSelected())
                showStudents();
            if(showT.isSelected())
                showTeachers();
        }
        if (!showS.isSelected() && !showT.isSelected()){
            student.clear();
        }
    }
}