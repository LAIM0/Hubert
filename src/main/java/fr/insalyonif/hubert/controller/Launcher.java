package fr.insalyonif.hubert.controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * The Launcher class is responsible for launching the JavaFX application and setting up the initial stage.
 */
public class Launcher extends Application {

    /**
     * Overrides the start method in Application to set up the initial stage.
     *
     * @param stage The primary stage for the application.
     * @throws IOException If an error occurs during the loading of the FXML file.
     */
    @Override
    public void start(Stage stage) throws IOException {
        // Load the FXML file and set up the scene
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/insalyonif/hubert/start.fxml"));
        Scene scene = new Scene(loader.load(), 1200, 800);

        // Set the title and scene for the stage
        stage.setTitle("Hubert!");
        stage.setScene(scene);

        // Display the stage
        stage.show();
    }

    /**
     * The main method that launches the JavaFX application.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        launch();
    }
}
