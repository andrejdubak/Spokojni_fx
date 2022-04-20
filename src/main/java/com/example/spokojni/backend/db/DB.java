package com.example.spokojni.backend.db;

import com.example.spokojni.backend.*;
import com.example.spokojni.backend.users.Admin;
import com.example.spokojni.backend.users.Student;
import com.example.spokojni.backend.users.Teacher;

import java.security.Timestamp;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class DB {
    static Connection con=null;
    static Statement stmt=null;
    static ArrayList<Teacher> Teachers;
    private static Teacher getTeacher(int id){
        for(Teacher teacher : Teachers){
            if(teacher.getId()==id)
                return teacher;
        }
        return null;
    }
    static ArrayList<Admin> Admins;
    private static Admin getAdmin(int id){
        for(Admin admin : Admins){
            if(admin.getId()==id)
                return admin;
        }
        return null;
    }
    static ArrayList<Student> Students;
    private static Student getStudent(int id){
        for(Student student : Students){
            if(student.getId()==id)
                return student;
        }
        return null;
    }
    static ArrayList<Subject> Subjects;
    private static Subject getSubject(int id){
        for(Subject subject : Subjects){
            if(subject.getId()==id)
                return subject;
        }
        return null;
    }
    static ArrayList<Term> Terms;
    private static Term getTerm(int id){
        for(Term term : Terms){
            if(term.getId()==id)
                return term;
        }
        return null;
    }
    static ArrayList<Agreement> Agreements;
    private static Agreement getAgreement(int id){
        for(Agreement agreement : Agreements){
            if(agreement.getId()==id)
                return agreement;
        }
        return null;
    }

    public DB() throws Exception {
        makeConn();
    }

    public static void makeConn() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        Connection con=DriverManager.getConnection("jdbc:mysql://vesta.mojhosting.eu:3306/admin_vava?useSSL=false","admin_vava","KP2E2fvxcA");
        stmt=con.createStatement();
    }
    public static User getUserById(int id) throws SQLException{
        ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE id=" + id);
        rs.first();
        switch(rs.getInt(6)) {
            case 3:
                return new Admin(rs.getInt(1),rs.getString(2), rs.getString(3), rs.getString(4));
            case 2:
                return new Teacher(rs.getInt(1),rs.getString(2), rs.getString(3), rs.getString(4));
            default:
                return new Student(rs.getInt(1),rs.getString(2), rs.getString(3), rs.getString(4));
        }
    }
    public static User getUserByLogin(String username) throws SQLException{
        ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE login='" + username + "'");
        rs.first();
        switch(rs.getInt(6)) {
            case 3:
                return new Admin(rs.getInt(1),rs.getString(2), rs.getString(3), rs.getString(4));
            case 2:
                return new Teacher(rs.getInt(1),rs.getString(2), rs.getString(3), rs.getString(4));
            default:
                return new Student(rs.getInt(1),rs.getString(2), rs.getString(3), rs.getString(4));
        }
    }
    public static Subject getSubjectById(int id) throws SQLException{
        getTeachers();
        ResultSet rs = stmt.executeQuery("SELECT * FROM subjects WHERE id=" + id);
        rs.first();
        return new Subject(rs.getInt(1),rs.getString(2), getTeacher(rs.getInt(3)));
    }
    public static Term getTermById(int id) throws SQLException{
        getSubjects();
        ResultSet rs = stmt.executeQuery("SELECT * FROM terms WHERE id=" + id);
        rs.first();
        LocalDateTime start_time = rs.getTimestamp(3).toLocalDateTime();
        LocalDateTime end_time = rs.getTimestamp(4).toLocalDateTime();
        String description = rs.getString(5);
        return new Term(rs.getInt(1), getSubject(rs.getInt(2)), start_time, end_time, description, rs.getInt(6));
    }
    public static ArrayList<Student> getStudents() throws SQLException{
        ArrayList<Student> newStudents = new ArrayList<>();
        ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE role=1");
        while(rs.next())
            newStudents.add(new Student(rs.getInt(1),rs.getString(2), rs.getString(3), rs.getString(4)));
        Students = newStudents;
        return Students;
    }
    public static ArrayList<Teacher> getTeachers() throws SQLException{
        ArrayList<Teacher> newTeachers = new ArrayList<>();
        ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE role=2");
        while(rs.next())
            newTeachers.add(new Teacher(rs.getInt(1),rs.getString(2), rs.getString(3), rs.getString(4)));
        Teachers = newTeachers;
        return Teachers;
    }
    public static ArrayList<Admin> getAdmins() throws SQLException{
        ArrayList<Admin> newAdmins = new ArrayList<>();
        ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE role=3");
        while(rs.next())
            newAdmins.add(new Admin(rs.getInt(1),rs.getString(2), rs.getString(3), rs.getString(4)));
        Admins = newAdmins;
        return Admins;
    }
    public static ArrayList<Subject> getSubjects() throws SQLException{
        getTeachers();
        ArrayList<Subject> newSubjects = new ArrayList<>();
        ResultSet rs = stmt.executeQuery("SELECT * FROM subjects JOIN users ON master_id=users.id");
        while(rs.next())
            newSubjects.add(new Subject(rs.getInt(1),rs.getString(2), getTeacher(rs.getInt(3))));
        Subjects = newSubjects;
        return Subjects;
    }
    public static Subject getSubjectByName(String name) throws Exception {
        for(Subject subject : Subjects)
            if(subject.getName().equals(name))
                return subject;
        getSubjects();
        for(Subject subject : Subjects)
            if(subject.getName().equals(name))
                return subject;
        for(Subject subject : Subjects)
            if(subject.getName().equalsIgnoreCase(name))
                return subject;
        throw new Exception("Subject " + name + " not found");
    }
    public static ArrayList<Subject> getSubjectsByTeacherId(int id) throws SQLException{
        getTeachers();
        ArrayList<Subject> newSubjects = new ArrayList<>();
        ResultSet rs = stmt.executeQuery("SELECT * FROM subjects JOIN users ON master_id=users.id WHERE users.id=" + id);
        while(rs.next())
            newSubjects.add(new Subject(rs.getInt(1),rs.getString(2), getTeacher(rs.getInt(3))));
        return newSubjects;
    }
    public static ArrayList<Term> getTerms() throws SQLException{
        getSubjects();
        ArrayList<Term> Terms = new ArrayList<>();
        ResultSet rs = stmt.executeQuery("SELECT * FROM terms");
        while(rs.next()){
            LocalDateTime start_time = rs.getTimestamp(3).toLocalDateTime();
            LocalDateTime end_time = rs.getTimestamp(4).toLocalDateTime();
            String description = rs.getString(5);
            Terms.add(new Term(rs.getInt(1),getSubject(rs.getInt(2)), start_time, end_time, description, rs.getInt(6)));
        }
        return Terms;
    }
    public static ArrayList<Term> getTermsBySubjectId(int id) throws SQLException{
        getSubjects();
        ArrayList<Term> Terms = new ArrayList<>();
        ResultSet rs = stmt.executeQuery("SELECT * FROM terms WHERE subject_id=" + id);
        while(rs.next()){
            LocalDateTime start_time = rs.getTimestamp(3).toLocalDateTime();
            LocalDateTime end_time = rs.getTimestamp(3).toLocalDateTime();
            String description = rs.getString(5);
            Terms.add(new Term(rs.getInt(1),getSubject(rs.getInt(2)), start_time, end_time, description, rs.getInt(6)));
        }
        return Terms;
    }
    public static ArrayList<Term> getTermsByStudentId(int id) throws SQLException{
        getSubjects();
        ArrayList<Term> Terms = new ArrayList<>();
        ResultSet rs = stmt.executeQuery("SELECT * FROM terms WHERE student_id=" + id);
        while(rs.next()){
            LocalDateTime start_time = rs.getTimestamp(3).toLocalDateTime();
            LocalDateTime end_time = rs.getTimestamp(3).toLocalDateTime();
            String description = rs.getString(5);
            Terms.add(new Term(rs.getInt(1),getSubject(rs.getInt(2)), start_time, end_time, description, rs.getInt(6)));
        }
        return Terms;
    }
    public static ArrayList<Agreement> getAgreements() throws SQLException{
        getTerms();
        getStudents();
        ArrayList<Agreement> Agreements = new ArrayList<>();
        ResultSet rs = stmt.executeQuery("SELECT * FROM agreements");
        while(rs.next())
            Agreements.add(new Agreement(rs.getInt(1), getStudent(rs.getInt(2)), getTerm(rs.getInt(3))));
        return Agreements;
    }
    public static ArrayList<Agreement> getAgreementsByStudentId(int id) throws SQLException{
        getTerms();
        getStudents();
        ArrayList<Agreement> Agreements = new ArrayList<>();
        ResultSet rs = stmt.executeQuery("SELECT * FROM agreements WHERE student_id=" + id);
        while(rs.next())
            Agreements.add(new Agreement(rs.getInt(1), getStudent(rs.getInt(2)), getTerm(rs.getInt(3))));
        return Agreements;
    }
    public static ArrayList<Agreement> getAgreementsBySubjectId(int id) throws SQLException{
        getTerms();
        getStudents();
        ArrayList<Agreement> Agreements = new ArrayList<>();
        ResultSet rs = stmt.executeQuery("SELECT agreements.* FROM agreements JOIN terms ON terms.id=term_id WHERE subject_id=" + id);
        while(rs.next())
            Agreements.add(new Agreement(rs.getInt(1), getStudent(rs.getInt(2)), getTerm(rs.getInt(3))));
        return Agreements;
    }
    public static ArrayList<Agreement> getAgreementsByTermId(int id) throws SQLException{
        getTerms();
        getStudents();
        ArrayList<Agreement> Agreements = new ArrayList<>();
        ResultSet rs = stmt.executeQuery("SELECT agreements.* FROM agreements JOIN terms ON terms.id=term_id WHERE term_id=" + id);
        while(rs.next())
            Agreements.add(new Agreement(rs.getInt(1), getStudent(rs.getInt(2)), getTerm(rs.getInt(3))));
        return Agreements;
    }
    public static boolean checkPassword(int user_id, String password) throws SQLException{
        ResultSet rs = stmt.executeQuery("SELECT pass FROM users WHERE id=" + user_id + " AND pass=SHA1('" + password + "')");
        return rs.next();
    }
    public static boolean checkPassword(String username, String password) throws SQLException{
        ResultSet rs = stmt.executeQuery("SELECT pass FROM users WHERE login='" + username + "' AND pass=SHA1('" + password + "')");
        return rs.next();
    }
    public static boolean checkPassword(User user, String password) throws SQLException{
        ResultSet rs = stmt.executeQuery("SELECT pass FROM users WHERE id=" + user.getId() + " AND pass=SHA1('" + password + "')");
        return rs.next();
    }
    public static void update(Object obj) throws SQLException{
        if(obj instanceof User){
            stmt.executeUpdate("UPDATE users SET name='" + ((User) obj).getName() + "', email='" + ((User) obj).getEmail() + "', login='" + ((User) obj).getLogin() + "', role=" + ((User) obj).getRole() + " WHERE id=" + ((User) obj).getId());
        }
        else if(obj instanceof Subject){
            stmt.executeUpdate("UPDATE subjects SET name='" + ((Subject) obj).getName() + "', master_id=" + ((Subject) obj).getMaster().getId() + " WHERE id=" + ((Subject) obj).getId());
        }
        else if(obj instanceof Term){
            stmt.executeUpdate("UPDATE terms SET subject_id=" + ((Term) obj).getSubject().getId() + ", start_time='" + ((Term) obj).getStart_time() + "', end_time='" + ((Term) obj).getEnd_time() + "', description='" + ((Term) obj).getDescription() + "', capacity='" + ((Term) obj).getCapacity() + "' WHERE id=" + ((Term) obj).getId());
        }
        else if(obj instanceof Agreement){
            stmt.executeUpdate("UPDATE agreements SET student_id=" + ((Agreement) obj).getStudent().getId() + ", term_id=" + ((Agreement) obj).getTerm().getId() + " WHERE id=" + ((Agreement) obj).getId());
        }
    }
    public static void add(Object obj) throws SQLException{
        if(obj instanceof User){
            stmt.executeUpdate("INSERT INTO users (id, pass, name, email, login, role) VALUES (NULL,NULL, '" + ((User) obj).getName() + "', '" + ((User) obj).getEmail() + "', '" + ((User) obj).getLogin() + "', " + ((User) obj).getRole() + ")");
        }
        else if(obj instanceof Subject){
            stmt.executeUpdate("INSERT INTO subjects (id, name, master_id) VALUES (NULL, '" + ((Subject) obj).getName() + "', " + ((Subject) obj).getMaster().getId() + ")");
        }
        else if(obj instanceof Term){
            stmt.executeUpdate("INSERT INTO terms (id, subject_id, start_time, end_time, description) VALUES (NULL, " + ((Term) obj).getSubject().getId() + ", '" + ((Term) obj).getStart_time() + "', '" + ((Term) obj).getEnd_time() + "', '" + ((Term) obj).getDescription() + "', '" + ((Term) obj).getCapacity() + "')");
        }
        else if(obj instanceof Agreement){
            stmt.executeUpdate("INSERT INTO agreements (id, student_id, term_id) VALUES (NULL, " + ((Agreement) obj).getStudent().getId() + ", " + ((Agreement) obj).getTerm().getId() + ")");
        }
    }
    public static void delete(Object obj) throws SQLException{
        if(obj instanceof User){
            stmt.executeUpdate("DELETE FROM users WHERE id=" + ((User) obj).getId());
        }
        else if(obj instanceof Subject){
            stmt.executeUpdate("DELETE FROM subjects WHERE id=" + ((Subject) obj).getId());
        }
        else if(obj instanceof Term){
            stmt.executeUpdate("DELETE FROM terms WHERE id=" + ((Term) obj).getId());
        }
        else if(obj instanceof Agreement){
            stmt.executeUpdate("DELETE FROM agreements WHERE id=" + ((Agreement) obj).getId());
        }
    }
    public static void updatePassword(User user, String new_password) throws SQLException{
        stmt.executeUpdate("UPDATE users SET pass=SHA1('" + new_password + "') WHERE id=" + user.getId());
    }
    public static void closeConn() throws SQLException {
        con.close();
    }
}
