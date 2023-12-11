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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class NewMapController {

    @FXML
    private DatePicker datePicker;

    @FXML
    private Button fileLoader;

    private String selectedFilePath;

    public void initialize() {
    }

    @FXML
    private void handleLoadFile(ActionEvent event) {

        // Create a FileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);

        //getClass().getResource("/fr/insalyonif/hubert/successSave.fxml")


        // Show open file dialog
        Stage stage = (Stage) fileLoader.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        // Check if a file is selected
        if (selectedFile != null) {
            selectedFilePath = selectedFile.getAbsolutePath();
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

    @FXML
    private void handleStart(ActionEvent event) throws IOException {
        // Récupérer la date du DatePicker
        //start.setVisible(true);
        if (datePicker.getValue()==null || Objects.equals(selectedFilePath, null)){
            //System.out.println("selectedFilePath = "+selectedFilePath);
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
