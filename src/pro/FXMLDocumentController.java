/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pro;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author Razvan
 */
public class FXMLDocumentController implements Initializable {

    ObservableList<Contact> obj = FXCollections.observableArrayList();
    @FXML
    private Label label;
    @FXML
    private Button nou;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnUpdate;
    @FXML
    private TableColumn tabTelefon;
    @FXML
    private TableColumn tabNume;
    @FXML
    private TableColumn tabPrenume;

    @FXML
    private TextField telefonInsert;
    @FXML
    private TextField numeInsert;
    @FXML
    private TextField prenumeInsert;

    @FXML
    private Label textAlert;
    @FXML
    private Label alert;
    @FXML
    private Label test;
    @FXML
    private TableView tabelContacte;

    @FXML
    private TextField tabCautare;
    @FXML
    private Label Lcautare;
    
    @FXML
    private void refresh(ActionEvent event) {

        Connection conn = null;
        try {

            conn = getConnection();

            Afisare(conn);
            alert.setText("");
            Lcautare.setText("");

        } catch (ClassNotFoundException ex) {
            System.out.println("eror1");
        } catch (SQLException ex) {
            System.out.println("eror3");
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    System.out.println("eror2");
                }
            }
        }
    }

    @FXML
    void saveButtonHandle(ActionEvent event) throws ClassNotFoundException, SQLException {
        Connection conn = null;
        conn = getConnection();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All", "*.*"),
                new FileChooser.ExtensionFilter("CSV File", "*.csv")
        );

        Stage window = Pro.getInstance().getMainWindow();
        File file = fileChooser.showSaveDialog(window);
        saveToFile(conn, file);
    }

    private void saveToFile(Connection conn, File file) throws SQLException {
        if (file != null) {
            try (BufferedWriter bfw = new BufferedWriter(new FileWriter(file))) {
                Statement stmt = conn.createStatement();
                ResultSet results = stmt.executeQuery("select * from Agenda");
                ResultSetMetaData rsmd = results.getMetaData();
                int numberCols = rsmd.getColumnCount();

                while (results.next()) {

                    String name = results.getString(2);
                    String prenume = results.getString(3);
                    String tel = results.getString(4);

                    bfw.write(name + "," + prenume + "," + tel + "\n");
                }
                results.close();
                stmt.close();

            } catch (IOException ex) {
            }
        }
    }

    @FXML
    void openButtonHandle(ActionEvent event) throws ClassNotFoundException, SQLException {
        Connection conn = null;
        conn = getConnection();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All", "*.*"),
                new FileChooser.ExtensionFilter("CSV File", "*.csv")
        );

        Stage window = Pro.getInstance().getMainWindow();
        File file = fileChooser.showOpenDialog(window);
        openFile(conn, file);
        Afisare(conn);
    }

    private void openFile(Connection conn, File file) throws SQLException {
        String linie = "";
        String desp = ",";
        if (file != null) {
            try (BufferedReader bfw = new BufferedReader(new FileReader(file))) {
                while ((linie = bfw.readLine()) != null) {
                    String[] contact = linie.split(desp);       
                    Statement stmt = conn.createStatement();
                    stmt.executeUpdate("INSERT INTO Agenda (nume,prenume,telefon)  VALUES('" + contact[0] + "','" + contact[1] + "','" + contact[2] + "')");
                }

               
            } catch (IOException ex) {
                System.out.println(ex);
            }
        }
    }

    @FXML
    private void insertContact(ActionEvent event) {
        Connection conn = null;
        String nume = numeInsert.getText();
        String prenume = prenumeInsert.getText();
        String nr = telefonInsert.getText();
        if (nume.isEmpty() || prenume.isEmpty() || nr.isEmpty()) {
            alert.setText("Completeaza toate campurile");
        } else {
            try {
                conn = getConnection();
                adauga(conn, nume, prenume, nr);
                Afisare(conn);
                alert.setText("Contact Adaugat");
                numeInsert.setText("");
                prenumeInsert.setText("");
                telefonInsert.setText("");

                //((Node)(event.getSource())).getScene().getWindow().hide();
            } catch (ClassNotFoundException | SQLException ex) {
               
                System.out.println("ex");
            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException ex) {
                        System.out.println("eror2");
                    }
                }
            }
        }

    }

    @FXML
    private void test(MouseEvent event) {
        btnDelete.setVisible(true);
        btnUpdate.setVisible(true);
        Contact ceva = (Contact) tabelContacte.getSelectionModel().getSelectedItem();
        String nume = ceva.getNume();
        String prenume = ceva.getPrenume();
        String nr = ceva.getNumar();

        numeInsert.setText(nume);
        prenumeInsert.setText(prenume);
        telefonInsert.setText(nr);

        Connection conn = null;

    }

    @FXML
    private void stergere(ActionEvent event) {
        btnDelete.setVisible(false);
        Contact ceva = (Contact) tabelContacte.getSelectionModel().getSelectedItem();
        String nume = ceva.getNume();
        String prenume = ceva.getPrenume();
        String nr = ceva.getNumar();
        Connection conn = null;
        try {
            conn = getConnection();
            delete(conn, nume, prenume, nr);
            Afisare(conn);
            alert.setText("Contact sters ");

        } catch (ClassNotFoundException ex) {
            System.out.println(ex);
        } catch (SQLException ex) {
            System.out.println(ex);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    System.out.println(ex);
                }
            }
        }

    }

    @FXML
    private void Update(ActionEvent event) {
        btnUpdate.setVisible(false);
        Contact ceva = (Contact) tabelContacte.getSelectionModel().getSelectedItem();
        String nume = ceva.getNume();
        String prenume = ceva.getPrenume();
        String nr = ceva.getNumar();
        Connection conn = null;
        try {
            conn = getConnection();
            update(conn, nume, prenume, nr);
            Afisare(conn);
            alert.setText("Update efectuat ");

        } catch (ClassNotFoundException ex) {
            System.out.println("s1");
            ex.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("2");
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    System.out.println("s3");
                }
            }
        }

    }

    public void Afisare(Connection conn) throws SQLException {
        obj.removeAll(obj);

        Statement stmt = conn.createStatement();

        String sql1;
        sql1 = " SELECT *FROM Agenda";
        final ResultSet rs = stmt.executeQuery(sql1);

        while (rs.next()) {
            String nume1 = rs.getString("nume");
            String pren = rs.getString("prenume");
            String nr = rs.getString("telefon");

            obj.add(new Contact(nume1, pren, nr));
            tabNume.setCellValueFactory(new PropertyValueFactory<>("nume"));
            tabPrenume.setCellValueFactory(new PropertyValueFactory<>("prenume"));
            tabTelefon.setCellValueFactory(new PropertyValueFactory<>("numar"));
            tabelContacte.setItems(obj);

            // System.out.println(nume1 + " " + pren + " " + nr);
        }
        //stmt.close();
        //conn.close();

    }

    public void afisareCautare(Connection conn) throws SQLException {
        obj.removeAll(obj);
        String cauta = tabCautare.getText();
        System.out.println(cauta);
        Statement stmt = conn.createStatement();

        String sql1;
        sql1 = "SELECT nume, prenume, telefon FROM Agenda where nume='" + cauta + "' OR prenume='" + cauta + "' OR telefon='" + cauta + "'";
        final ResultSet rs = stmt.executeQuery(sql1);

        while (rs.next()) {
            String nume1 = rs.getString("nume");
            String pren = rs.getString("prenume");
            String nr = rs.getString("telefon");

            obj.add(new Contact(nume1, pren, nr));
            tabNume.setCellValueFactory(new PropertyValueFactory<>("nume"));
            tabPrenume.setCellValueFactory(new PropertyValueFactory<>("prenume"));
            tabTelefon.setCellValueFactory(new PropertyValueFactory<>("numar"));
            tabelContacte.setItems(obj);

        }

    }

    private static Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        String dbUrl = "jdbc:derby:agendaDB;create=true";
        Properties props = new Properties();
        props.put("user", "raz");
        props.put("password", "raz");
        Connection conn = DriverManager.getConnection(dbUrl, props);

        DatabaseMetaData verificareTabel = conn.getMetaData();
        ResultSet verificare = verificareTabel.getTables(null, null, "AGENDA", null);
        if (!verificare.next()) {
            System.out.println("nu exista");
            creareTabel(conn);

        }

        return conn;
    }

    private static void adauga(Connection conn, String nume1, String prenume1, String telefon1) throws SQLException {

        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("INSERT INTO Agenda (nume,prenume,telefon)  VALUES('" + nume1 + "','" + prenume1 + "','" + telefon1 + "')");

        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("Mare eroare");

        }

    }

    private static void creareTabel(Connection conn) throws SQLException {
        try {
            conn.createStatement().execute("CREATE TABLE Agenda (id INTEGER not null GENERATED ALWAYS AS IDENTITY primary key,nume VARCHAR(30),prenume VARCHAR(30),telefon VARCHAR(10))");
            System.out.println("Adaugat");
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("fasfasf");
        }

    }

    private static void save(Connection conn) throws SQLException {
        //READ
        Statement stmt = conn.createStatement();
        ResultSet results = stmt.executeQuery("select * from Agenda");
        ResultSetMetaData rsmd = results.getMetaData();
        int numberCols = rsmd.getColumnCount();

        while (results.next()) {
            int id = results.getInt(1);
            String name = results.getString(2);
            String prenume = results.getString(3);
            String tel = results.getString(4);
            System.out.println(name + prenume + tel);
        }
        results.close();
        stmt.close();
    }

    

    @FXML
    private void cautare(ActionEvent event) {
        String conditie = tabCautare.getText();
        if (!conditie.isEmpty()) {
            Connection conn = null;
            try {

                conn = getConnection();

                afisareCautare(conn);
                Lcautare.setText("Cautare Terminata");
            } catch (ClassNotFoundException ex) {
                System.out.println("eror1");
            } catch (SQLException ex) {
                System.out.println("eror3");
            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException ex) {
                        System.out.println("eror2");
                    }
                }
            }
        } else {

            Lcautare.setText("Campul este gol");
        }
    }

    public void update(Connection conn, String num, String pren, String nr) throws SQLException {
        String nume = numeInsert.getText();
        String prenume = prenumeInsert.getText();
        String numar = telefonInsert.getText();
        Statement stmt1 = conn.createStatement();
        stmt1.executeUpdate("UPDATE Agenda SET nume='" + nume + "',prenume='" + prenume + "',telefon='" + numar + "' WHERE nume = '" + num + "' AND prenume='" + pren + "' AND telefon='" + nr + "' ");

    }

    public void delete(Connection conn, String num, String pren, String nr) throws SQLException {

        Statement stmt1 = conn.createStatement();
        stmt1.executeUpdate("DELETE  FROM Agenda where nume='" + num + "'AND prenume='" + pren + "'  AND telefon='" + nr + "' ");

    }
    @FXML
    private void exit(ActionEvent event) {
        Platform.exit();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        Connection conn = null;
        try {
            conn = getConnection();
            Afisare(conn);

        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
