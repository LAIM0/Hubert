package fr.insalyonif.hubert.views;

import fr.insalyonif.hubert.controller.Controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * The StartController class is responsible for handling user interactions for starting the application.
 */
public class StartController {

    @FXML
    private StackPane root;

    @FXML
    private Label messageLabel;

    @FXML
    private Label message;

    @FXML
    private DatePicker datePicker;

    @FXML
    private Button fileLoader;

    @FXML
    private Button start;

    @FXML
    private Button allDeliveries;

    // Variable to store the selected file path
    private String selectedFilePath;

    /**
     * Initializes the controller.
     */
    public void initialize() {
    }

    /**
     * Handles the event when the "See All Deliveries" button is clicked.
     *
     * @param event The ActionEvent triggered by the button click.
     * @throws Exception If an error occurs during the loading of the FXML file.
     */
    @FXML
    public void handleSeeAllDeliveries(ActionEvent event) throws Exception {
        selectedFilePath = "";
        // Create a FileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);



        // Show open file dialog
        Stage stage = (Stage) allDeliveries.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        // If a file is selected, store its path
        if (selectedFile != null) {
            selectedFilePath = selectedFile.getAbsolutePath();
            File xmlFile = new File(selectedFilePath);

            // Initialisation du constructeur de documents XML
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            // Parsing du document XML
            Document doc = dBuilder.parse(xmlFile);

            // Normalisation du document XML pour éliminer les espaces blancs inutiles
            doc.getDocumentElement().normalize();
            //System.out.println("loadArchiveFile");


            Element map = (Element) doc.getElementsByTagName("map").item(0);
            Element deliveryTour = (Element) doc.getElementsByTagName("deliveryTour").item(0);

            if (map == null || deliveryTour == null){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Ce fichier ne correspond pas :(");
                alert.showAndWait();
                return;
            }


            String fileName = map.getAttribute("fileName");
            LocalDate fileDate = LocalDate.parse(map.getAttribute("globalDate"));

            String stratPath = "src/main/resources/fr/insalyonif/hubert/fichiersXML2022/";
            String pathMap = stratPath + fileName + ".xml";
            //System.out.println("Path of the map: " + pathMap);

            Path path = Paths.get(pathMap);
            if (!Files.exists(path)) {
                // Show an error popup if no file is selected
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("The map doesnt exist");
                alert.setHeaderText(null);
                alert.setContentText("The map doesn't exist anymore.");
                alert.showAndWait();
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/insalyonif/hubert/ihm.fxml"));
            Parent root = loader.load();

            // Add the new scene
            Scene scene = new Scene(root);
            Stage stage1 = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage1.setScene(scene);

            ViewController viewController = loader.getController();
            viewController.loadMap(fileDate, pathMap);
            viewController.importAllTheDeliveriesIntoController(selectedFilePath);
            viewController.displayAllTheDeliveryPoints();

            stage.show();
        }
    }


    /**
     * Handles the event when the "Create New Deliveries" button is clicked.
     *
     * @param event The ActionEvent triggered by the button click.
     */
    @FXML
    private void handleCreateNewDeliveries(ActionEvent event) {
        selectedFilePath = "";
        // Code to handle the "Create new Deliveries" button click
        //System.out.println("Create new Deliveries button clicked");

        // Show additional controls
        datePicker.setVisible(true);
        fileLoader.setVisible(true);
        start.setVisible(true);
        message.setVisible(true);
    }

    /**
     * Handles the event when the "Load File" button is clicked.
     *
     * @param event The ActionEvent triggered by the button click.
     */
    @FXML
    private void handleLoadFile(ActionEvent event) {
        // Create a FileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);


        // Show open file dialog
        Stage stage = (Stage) datePicker.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

       // Check if a file is selected
        if (selectedFile != null) {
            selectedFilePath = selectedFile.getAbsolutePath();
            //System.out.println("Selected File: " + selectedFilePath);

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
        //start.setVisible(true);
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

    // Add getter for selectedFilePath if needed
    public String getSelectedFilePath() {
        return selectedFilePath;
    }

}
