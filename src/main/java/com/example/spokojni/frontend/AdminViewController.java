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



    @FXML
    private void exportClick() throws ParserConfigurationException {
        User[] selectedUsers = new User[2];
        selectedUsers[0] = new User(5,"andrej", "dmail", "xdubovski");
        selectedUsers[1] = new User(6, "sumo", "smail", "xkadzeriak");

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

            logins[i] = doc.createElement("login");
            logins[i].setTextContent(selectedUsers[i].getLogin());
            users[i].appendChild(logins[i]);
        }

        try (FileOutputStream output = new FileOutputStream(".\\test.xml")) {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(output);
            transformer.transform(source, result);
        } catch (IOException | TransformerException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void importClick(){
        FileDialog dialog = new FileDialog((Frame)null, "Select File to Open");
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
                                eElement.getElementsByTagName("login").item(0).getTextContent()
                        );
                    }
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            // TODO: mame user array importedUsers, ktory treba hodit do tabulky
        }
        else{
            // TODO: error, zvoleny subor nie je xml ;)
        }


    }
}

