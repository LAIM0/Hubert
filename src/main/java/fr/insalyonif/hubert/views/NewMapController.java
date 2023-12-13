package fr.insalyonif.hubert.views;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Objects;

/**
 * The NewMapController class is responsible for handling user interactions for loading a new map.
 */
public class NewMapController {

    @FXML
    private DatePicker datePicker;

    @FXML
    private Button fileLoader;

    private String selectedFilePath;

    /**
     * Initializes the controller.
     */
    public void initialize() {
    }

    /**
     * Handles the event when the "Load File" button is clicked.
     *
     * @param event The ActionEvent triggered by the button click.
     */
    @FXML
    private void handleLoadFile(ActionEvent event) throws ParserConfigurationException, IOException, SAXException {

        // Create a FileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);


        // Show open file dialog
        Stage stage = (Stage) fileLoader.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        // Check if a file is selected
        if (selectedFile != null) {
            selectedFilePath = selectedFile.getAbsolutePath();

            // Initialisation du constructeur de documents XML
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            // Parsing du document XML
            Document doc = dBuilder.parse(selectedFile);

            // Normalisation du document XML pour éliminer les espaces blancs inutiles
            doc.getDocumentElement().normalize();
            Element map = (Element) doc.getElementsByTagName("map").item(0);
            Element intersection = (Element) doc.getElementsByTagName("intersection").item(0);
            Element segment = (Element) doc.getElementsByTagName("segment").item(0);
            if (map == null || intersection == null || segment == null){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("The file doesn't have the good format :(");
                alert.showAndWait();
                return;
            }

            System.out.println("Selected File: " + selectedFilePath);

            // Show a success popup
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("File Loaded");
            alert.setHeaderText(null);
            alert.setContentText("File loaded successfully: " + selectedFilePath);
            alert.showAndWait();
        } else {
            // Show an error popup if no file is selected
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("File Loading Failed");
            alert.setHeaderText(null);
            alert.setContentText("No file was selected.");
            alert.showAndWait();
        }
    }

    /**
     * Handles the event when the "Start" button is clicked.
     *
     * @param event The ActionEvent triggered by the button click.
     * @throws IOException If an error occurs during the loading of the FXML file.
     */
    @FXML
    private void handleStart(ActionEvent event) throws IOException {
        // Récupérer la date du DatePicker
        if (datePicker.getValue()==null || Objects.equals(selectedFilePath, null) || Objects.equals(selectedFilePath, "")){

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("File Loading Failed");
            alert.setHeaderText(null);
            alert.setContentText("Please enter Date and File");
            alert.showAndWait();
            return;
        }

        // Charger le fichier FXML "ihm.fxml"
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/insalyonif/hubert/ihm.fxml"));
        Parent root = loader.load();

        // Afficher la nouvelle scène
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);

        ViewController viewController = loader.getController();
        viewController.loadMap(datePicker.getValue(), selectedFilePath);

        stage.show();
    }
}
