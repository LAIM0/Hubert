package fr.insalyonif.hubert.views;

import fr.insalyonif.hubert.controller.Controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import java.awt.Color;
import javafx.util.StringConverter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import netscape.javascript.JSObject;

import fr.insalyonif.hubert.model.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * The ViewController class is responsible for managing the graphical user interface (GUI) of the delivery planning application.
 * It handles interactions between the user interface components, the underlying data model, and the displayed map.
 *
 * This class utilizes JavaFX for building the GUI and incorporates a WebView for rendering an interactive map using Leaflet.
 * It includes various methods for handling user actions, such as adding new couriers, deleting delivery requests,
 * opening new windows for adding deliveries, importing map files, and saving the current map state.
 *
 * The WebView displays a map with markers representing the warehouse, delivery locations, and delivery routes.
 * It uses JavaScript to interact with the Java code, enabling communication between the map and the application logic.
 * Additionally, the class supports importing and displaying delivery data from XML files.
 *
 * The ViewController class also contains methods for updating the displayed map, handling courier and delivery selection,
 * and setting up the initial state of the GUI. It is part of a larger application architecture that includes the Controller
 * class for managing the underlying data and logic.
 */
public class ViewController implements Initializable {
    @FXML
    private WebView webView;

    @FXML
    private Button delivery;


    @FXML
    private ComboBox<Courier> courier;

    @FXML
    private Button import1; //Bouton pour importer un fichier existant

    @FXML
    private Label dateLabel;  //Date du fichier à afficher

    @FXML
    private Label fileNameLabel; //Date du fichier à afficher


    private ObservableList<Courier> listCourier;


    @FXML
    private Button validate_delivery;

    @FXML
    private Button delete_delivery;

    @FXML
    private ListView<DeliveryRequest> listViewDelivery;

    private ObservableList<DeliveryRequest> listDelivery;

    private WebEngine engine;

    private Controller controller;

    private double lastClickedLat = 0.0;

    private double lastClickedLng = 0.0;

    private String selectedFilePath;

    private static final String MAP_HTML_TEMPLATE = """
            <!DOCTYPE html>
            <html>
            <head>
                <title>Simple Map</title>
                <meta charset="utf-8" />
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <link rel="stylesheet" href="https://unpkg.com/leaflet@1.7.1/dist/leaflet.css" />
                <script src="https://unpkg.com/leaflet@1.7.1/dist/leaflet.js"></script>
                <style>
                    #map { width: 800px; height: 600px; }
                </style>
            </head>
            <body>
               
                <div id="map"></div>
                <script>
                    var clickMarker;
                    var map = L.map('map').setView([45.74979, 4.87972], 14);
                    L.tileLayer('https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}.png', {
                        attribution: 'Map tiles by Carto, under CC BY 3.0. Data by OpenStreetMap, under ODbL.',
                        maxZoom: 18
                    }).addTo(map);
                    var customIcon = L.icon({
                        iconUrl: 'https://upload.wikimedia.org/wikipedia/commons/thumb/e/ed/Map_pin_icon.svg/1200px-Map_pin_icon.svg.png',
                        iconSize: [15, 20],
                        iconAnchor: [7.5, 20],
                    });
                    
                    %s
                    
                    function onMapClick(e) {
                         // Supprime l'ancien marqueur s'il existe
                         if (clickMarker) {
                             map.removeLayer(clickMarker);
                         }
                     
                         // Crée un nouveau marqueur à l'emplacement cliqué
                         clickMarker = L.marker(e.latlng, {
                             icon: L.icon({
                                 iconUrl: 'https://cdn-icons-png.flaticon.com/512/149/149059.png',
                                 iconSize: [30, 30],
                                iconAnchor: [15, 30],
                             })
                         }).addTo(map);
                         
                         // Envoi à l'application les coordonnées cliqués
                         window.javaApp.handleMapClick(e.latlng.lat, e.latlng.lng);
                    }
                    
                    map.on('click', onMapClick);
                </script>
            </body>
            </html>
            """;


    /**
     * Handles the event when the "Add New Courier" button is clicked.
     *
     * @param event The ActionEvent triggered by the button click.
     */
    @FXML
    void addNewCourrier(ActionEvent event) {
        if (controller != null) {
            controller.newDeliveryTour();
            setCourierIHM(controller.getListeDelivery());
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText("Successfully added ! :)");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Il faut d'abord choisir une MAP");
            alert.showAndWait();
        }
    }

    /**
     * Handles the deletion of a delivery request.
     *
     * @param selectedDelivery The selected delivery request to be deleted.
     * @param id The ID of the courier associated with the delivery request.
     */
    public void handleDeleteDelivery(DeliveryRequest selectedDelivery, int id) {
        if (controller != null) {
            int traceDeletePoint = controller.deleteDelivery(selectedDelivery,id);
            if(traceDeletePoint == 0) {
                String markersJs = drawPaths(controller.getCityMap(), null);
                String mapHtml = MAP_HTML_TEMPLATE.formatted(markersJs);
                engine.loadContent(mapHtml);
                this.setDeliveryRequestIHM(controller.getListeDelivery().get(courier.getValue().getId()).getRequests());
            }
        }
    }

    /**
     * Handles the event when the "Open New Window" button is clicked, either from the "Delivery" or "Validate Delivery" buttons.
     *
     * @param event The ActionEvent triggered by the button click.
     */
    @FXML
    void handleOpenNewWindow(ActionEvent event) {
        if (event.getSource() == delivery || event.getSource() == validate_delivery) {
            if (controller != null) {
                try {
                    // Load the FXML file for the new window
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/insalyonif/hubert/addDelivery.fxml"));
                    Parent root = (Parent) loader.load();

                    // Create a new stage for the new window
                    Stage newStage = new Stage();
                    newStage.setTitle("add Delivery");
                    newStage.setScene(new Scene(root));
                    DeliveryIHMController deliveryIHM = loader.getController();
                    Object[] objects = controller.findBestCourier();
                    deliveryIHM.setInitialCourier((Courier) objects[0]);
                    deliveryIHM.setInitialTimeWindow((TimeWindow) objects[1]);

                    if (event.getSource() == validate_delivery) {
                        deliveryIHM.setLat(lastClickedLat);
                        deliveryIHM.setLng(lastClickedLng);
                        Courier defaultCourier = (Courier) controller.findBestCourier()[0];
                        TimeWindow defaultTimeWindow = (TimeWindow) controller.findBestCourier()[1];

                        courier.setValue(defaultCourier);
                    }
                    deliveryIHM.setListCourier(listCourier);

                    // Show the new window
                    newStage.showAndWait();

                    if (deliveryIHM.isValiderClicked()) {
                        int traceNewDeliveryPoint = controller.newDeliveryPoint(deliveryIHM, deliveryIHM.getCourier().getId());
                        if (traceNewDeliveryPoint == 0) {
                            String markersJs = drawPaths(controller.getCityMap(), null);
                            String mapHtml = MAP_HTML_TEMPLATE.formatted(markersJs);
                            engine.loadContent(mapHtml);
                            courier.setValue(deliveryIHM.getCourier());
                            this.setDeliveryRequestIHM(controller.getListeDelivery().get(deliveryIHM.getCourier().getId()).getRequests());
                        } else if (traceNewDeliveryPoint == 1) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setContentText("Point non accessible");
                            alert.showAndWait();
                        } else if (traceNewDeliveryPoint == 2) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setContentText("Point déjà présent dans la liste");
                            alert.showAndWait();
                        } else if (traceNewDeliveryPoint == 3) {
                            for (DeliveryRequest d : controller.getListeDelivery().get(deliveryIHM.getCourier().getId()).getRequests()) {
                                if (d.isGoOff() && deliveryIHM.getTimeWindow().getStartTime() == d.getTimeWindow().getStartTime()) {
                                    Alert alert = new Alert(Alert.AlertType.WARNING);
                                    alert.setContentText("The courier " + deliveryIHM.getCourier().getId() +
                                            " will be late for the time window " +
                                            d.getTimeWindow().getStartTime() + "h to " + d.getTimeWindow().getEndTime() + "h");
                                    alert.showAndWait();
                                    break;
                                }
                            }
                            String markersJs = drawPaths(controller.getCityMap(), null);
                            String mapHtml = MAP_HTML_TEMPLATE.formatted(markersJs);
                            engine.loadContent(mapHtml);
                            courier.setValue(deliveryIHM.getCourier());
                            this.setDeliveryRequestIHM(controller.getListeDelivery().get(deliveryIHM.getCourier().getId()).getRequests());
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Il faut d'abord choisir une MAP");
                alert.showAndWait();
            }
        }
    }

    /**
     * Imports all deliveries from an XML file into the controller.
     *
     * @param pathname The pathname of the XML file containing the deliveries.
     * @throws Exception If an error occurs during the file import process.
     */
    public void importAllTheDeliveriesIntoController(String pathname) throws Exception {
        controller.loadArchiveFile(pathname);
    }

    /**
     * Display the delivery points on the map, including the warehouse and individual delivery locations.
     *
     * @param target The specific delivery request to highlight (can be null).
     * @return A StringBuilder containing JavaScript code for displaying markers on the map.
     */
    private StringBuilder displayDeliveryPoints(DeliveryRequest target) {
        Courier selectedCourier = courier.getValue();
        StringBuilder markersJs = new StringBuilder();
        String iconUrlWH   = "https://cdn-icons-png.flaticon.com/512/124/124434.png";
        String iconUrlDP = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/ed/Map_pin_icon.svg/1200px-Map_pin_icon.svg.png";

        String markerJsWH = String.format(
                "var marker = L.marker([" + controller.getCityMap().getWareHouseLocation().getLatitude() + ", " +  controller.getCityMap().getWareHouseLocation().getLongitude() + "], {icon: L.icon({iconUrl: '%s', iconSize: [25, 35], iconAnchor: [15, 30]})}).addTo(map);"
                        + "marker.bindTooltip('%s',{permanent:false}).openTooltip();", iconUrlWH, "Warehouse"
        );
        markersJs.append(markerJsWH);

        for (DeliveryTour deliveryTour : controller.getListeDelivery()) {
            int i = 0;
            for (DeliveryRequest deliveryRequest : deliveryTour.getRequests()) {
                String markerJs;
                if (deliveryTour.getCourier().equals(selectedCourier)) {
                    // Afficher les numéros seulement pour le coursier sélectionné
                    if (target != null && deliveryRequest.getDeliveryLocation().getId() == target.getDeliveryLocation().getId()) {
                        iconUrlDP = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/ed/Map_pin_icon.svg/1200px-Map_pin_icon.svg.png";
                        i++;
                        markerJs = String.format(
                                "var marker = L.marker([" + deliveryRequest.getDeliveryLocation().getLatitude() + ", " + deliveryRequest.getDeliveryLocation().getLongitude() + "],  {icon: L.icon({iconUrl: '%s', iconSize: [30, 40], iconAnchor: [15, 40]})}).addTo(map);"
                                        + "marker.bindTooltip('Nb: %d',{permanent:false}).openTooltip();",
                                //deliveryRequest.getDeliveryLocation().getId()
                                iconUrlDP, i
                        );
                    } else {
                        iconUrlDP = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/ed/Map_pin_icon.svg/1200px-Map_pin_icon.svg.png";
                        i++;
                        markerJs = String.format(
                                "var marker = L.marker([" + deliveryRequest.getDeliveryLocation().getLatitude() + ", " + deliveryRequest.getDeliveryLocation().getLongitude() + "],  {icon: L.icon({iconUrl: '%s', iconSize: [15, 20], iconAnchor: [8, 20]})}).addTo(map);"
                                        + "marker.bindTooltip('Nb: %d',{permanent:false}).openTooltip();",
                                //deliveryRequest.getDeliveryLocation().getId()
                                iconUrlDP, i
                        );
                    }
                } else {
                    // Afficher les marqueurs sans numéros pour les autres coursiers
                    markerJs = String.format(
                            "var marker = L.marker([" + deliveryRequest.getDeliveryLocation().getLatitude() + ", " + deliveryRequest.getDeliveryLocation().getLongitude() + "], {icon: L.icon({iconUrl: '%s', iconSize: [15, 20], iconAnchor: [7.5, 20]})}).addTo(map);",
                            iconUrlDP
                    );
                }
                markersJs.append(markerJs);
            }
        }
        return markersJs;
    }

    /**
     * Draw the paths on the map for each delivery tour, highlighting the specified delivery request.
     *
     * @param cityMap         The city map.
     * @param deliveryRequest The specific delivery request to highlight (can be null).
     * @return JavaScript code for drawing paths on the map.
     */
    private String drawPaths(CityMap cityMap, DeliveryRequest deliveryRequest) {
        Courier courierComboBox = courier.getValue();
        if (courierComboBox == null) {
            courierComboBox = controller.getListeDelivery().get(0).getCourier();
        }
        StringBuilder markersJs = displayDeliveryPoints(deliveryRequest);
        String polylineJsCouleur = "";
        int index = 0;
        for (DeliveryTour deliveryTour : controller.getListeDelivery()) {
            index++;
            if (deliveryTour.getPaths() != null) {
                for (int i = deliveryTour.getPaths().size() - 1; i >= 0; i--) {
                    Chemin chemin = deliveryTour.getPaths().get(i);
                    int currentIndex = chemin.getFin().getPos();
                    int nextIndex = chemin.getPi()[currentIndex];

                    StringBuilder polylineCoords = new StringBuilder("[");
                    polylineCoords.append("[").append(chemin.getFin().getLatitude()).append(", ").
                            append(chemin.getFin().getLongitude()).append("],");

                    while (nextIndex != -1) {
                        Intersection currentIntersection = cityMap.findIntersectionByPos(nextIndex);
                        if (currentIntersection != null) {
                            polylineCoords.append("[").append(currentIntersection.getLatitude()).append(", ").
                                    append(currentIntersection.getLongitude()).append("],");
                        }
                        currentIndex = nextIndex;
                        nextIndex = chemin.getPi()[currentIndex];
                    }

                    polylineCoords.append("[").append(chemin.getDebut().getLatitude()).append(", ").
                            append(chemin.getDebut().getLongitude()).append("]");
                    polylineCoords.append("]");

                    if (deliveryTour.getCourier().getId() == courierComboBox.getId()) {
                        if (deliveryRequest != null && chemin.getFin() == deliveryRequest.getDeliveryLocation()) {
                            polylineJsCouleur = polylineJsCouleur + "L.polyline(" + polylineCoords +
                                    ", {weight: 6, color: '" + generateColor(index, i, deliveryTour.getPaths().size() - 1) + "'}).addTo(map);";
                        } else {
                            polylineJsCouleur = polylineJsCouleur + "L.polyline(" + polylineCoords +
                                    ", {color: '" + generateColor(index, i, deliveryTour.getPaths().size() - 1) + "'}).addTo(map);";
                        }
                    } else {
                        String polylineJs = "L.polyline(" + polylineCoords + ", {color: 'grey'}).addTo(map);";
                        markersJs.append(polylineJs);
                    }
                }
            }
        }
        markersJs.append(polylineJsCouleur);
        return markersJs.toString();
    }

    /**
     * Generate a color for the polyline based on the given index and its position in the path.
     *
     * @param i    The index.
     * @param j    The position in the path.
     * @param maxJ The maximum position in the path.
     * @return The generated color in hexadecimal format.
     */
    public static String generateColor(int i, int j, int maxJ) {
        Color baseColor = getColorByIndex(i);
        Color adjustedColor = adjustBrightness(baseColor, j, maxJ);
        return colorToHex(adjustedColor);
    }

    /**
     * Get a color based on the given index.
     *
     * @param index The index.
     * @return The color associated with the index.
     */
    public static Color getColorByIndex(int index) {
        Color[] colors = {Color.ORANGE, Color.GREEN, Color.BLUE, Color.RED, Color.YELLOW};
        return colors[index % colors.length];
    }

    /**
     * Adjust the brightness of a given color based on the specified parameters.
     *
     * @param color The color to adjust.
     * @param j     The position in the path.
     * @param maxJ  The maximum position in the path.
     * @return The adjusted color.
     */
    public static Color adjustBrightness(Color color, int j, int maxJ) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);

        // Adjust brightness based on the value of j and maxJ
        float brightnessFactor = 0.5f + 0.5f * ((float) j / maxJ);
        hsb[2] = Math.min(hsb[2] * brightnessFactor, 1.0f);

        return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
    }

    /**
     * Convert a color to its hexadecimal representation.
     *
     * @param color The color to convert.
     * @return The hexadecimal representation of the color.
     */
    public static String colorToHex(Color color) {
        return String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
    }

    /**
     * Handle the selection of a courier, updating the displayed delivery requests and paths accordingly.
     *
     * @param newCourier The newly selected courier.
     */
    void handleCourierSelection(Courier newCourier) {
        // Filter the delivery tours for the selected courier
        DeliveryTour selectedTour = controller.getListeDelivery().stream()
                .filter(tour -> tour.getCourier().equals(newCourier))
                .findFirst()
                .orElse(null);

        if (selectedTour != null) {
            setDeliveryRequestIHM(selectedTour.getRequests());
            String markersJs = drawPaths(controller.getCityMap(), null);
            String mapHtml = MAP_HTML_TEMPLATE.formatted(markersJs);
            engine.loadContent(mapHtml);
        }
    }

    /**
     * Handles the event when the "Load Map" button is clicked. This method prompts the user to save the current map state,
     * then opens a new window for loading a new map file. The loaded map is displayed in the WebView, and the associated
     * deliveries and paths are updated accordingly.
     *
     * @param event The ActionEvent triggered by the button click.
     * @throws IOException If an error occurs during the file loading process.
     */
    @FXML
    void handleLoadMap(ActionEvent event) throws IOException {
        handleSaveMap(event);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/insalyonif/hubert/newMap.fxml"));
        Parent root = (Parent) loader.load();


        // Créer une nouvelle fenêtre
        Stage newStage = new Stage();
        newStage.setTitle("New Map");
        newStage.setScene(new Scene(root));
        newStage.showAndWait();
    }

    /**
     * Loads a new map based on the selected file path and the specified date. This method initializes a new Controller
     * with the selected map file, sets the global date, and updates the displayed map, couriers, and delivery requests.
     *
     * @param datePicker        The selected date for the new map.
     * @param selectedFilePath  The file path of the selected map file.
     */
    void loadMap(LocalDate datePicker, String selectedFilePath ) {

        // Load the selected XML map file
        controller = new Controller(selectedFilePath);
        setCourierIHM(controller.getListeDelivery());
        controller.setGlobalDate(datePicker);

        dateLabel.setText(String.valueOf(datePicker));

        // Convert the string to a Path object
        Path path = Paths.get(selectedFilePath);
        // Get the file name
        String fileName = path.getFileName().toString();
        fileNameLabel.setText(fileName);

        String markersJs = displayDeliveryPoints(null).toString();
        String mapHtml = MAP_HTML_TEMPLATE.formatted(markersJs);

        engine.loadContent(mapHtml);
    }

    /**
     * Handle the import of a map, saving the existing file, and then importing data from the selected XML file.
     *
     * @param event The action event triggering the map import.
     * @throws Exception If an error occurs during the import process.
     */
    @FXML
    void handleImportMap(ActionEvent event) throws Exception {
        // Save the existing file
        handleSaveMap(event);

        // Import data from the XML file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show open file dialog
        Stage stage = (Stage) import1.getScene().getWindow();
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
            System.out.println("loadArchiveFile");


            Element map = (Element) doc.getElementsByTagName("map").item(0);
            Element deliveryTour = (Element) doc.getElementsByTagName("deliveryTour").item(0);

            //TO DO : si map est null alors erreur
            if (map == null || deliveryTour == null){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Ce fichier ne correspond pas :(");
                alert.showAndWait();
                return;
            }

            String fileName = map.getAttribute("fileName");
            LocalDate fileDate = LocalDate.parse(map.getAttribute("globalDate"));


            System.out.println("fileName File: " + fileName);
            System.out.println("fileDate File: " + fileDate);

            String stratPath = "src/main/resources/fr/insalyonif/hubert/fichiersXML2022/";
            String pathMap = stratPath + fileName + ".xml";
            System.out.println("Path of the map: " + pathMap);

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
            loadMap(fileDate, pathMap);
            importAllTheDeliveriesIntoController(selectedFilePath);
            displayAllTheDeliveryPoints();

            stage.show();
        }
    }

    /**
     * Set the displayed delivery requests based on the provided list.
     *
     * @param delivery The list of delivery requests to display.
     */
    public void setDeliveryRequestIHM(ArrayList<DeliveryRequest> delivery) {
        listDelivery.clear();
        listDelivery.addAll(delivery);
    }

    /**
     * Set the displayed couriers based on the provided list of delivery tours.
     *
     * @param deliveryTours The list of delivery tours.
     */
    public void setCourierIHM(ArrayList<DeliveryTour> deliveryTours) {
        Courier c = courier.getValue();
        listCourier.clear();
        for (DeliveryTour deliveryTour : deliveryTours) {
            listCourier.add(deliveryTour.getCourier());
        }
        courier.setValue(c);
    }

    /**
     * Set the coordinates of the last clicked point on the map.
     *
     * @param lat The latitude of the last clicked point.
     * @param lng The longitude of the last clicked point.
     */
    public void setLastClickedCoordinates(double lat, double lng) {
        this.lastClickedLat = lat;
        this.lastClickedLng = lng;
    }

    /**
     * Initialize the controller, setting up the WebView, observable lists, and event listeners.
     *
     * @param url            The URL to initialize.
     * @param resourceBundle The resource bundle for localization.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        engine = webView.getEngine();
        listDelivery = FXCollections.observableArrayList();
        listViewDelivery.setItems(listDelivery);

        // Lien entre le Javascript et Java
        engine.setJavaScriptEnabled(true);
        engine.getLoadWorker().stateProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (Worker.State.SUCCEEDED.equals(newValue)) {
                        JSObject window = (JSObject) engine.executeScript("window");
                        window.setMember("javaApp", this);
                    }
                }
        );

        listCourier= FXCollections.observableArrayList();

        courier.setConverter(new StringConverter<Courier>() {
            @Override
            public String toString(Courier c) {
                if(c==null){
                    return "";
                }
                return "Courrier "+c.getId();
            }

            @Override
            public Courier fromString(String s) {
                return null;
            }
        });
        courier.setItems(listCourier);

        courier.valueProperty().addListener((observable, oldValue, newCourier) -> {
            // newValue is the newly selected Courier object
            if (newCourier != null) {
                handleCourierSelection(newCourier);


            }
        });

        listViewDelivery.setOnMouseClicked(event -> {
            // Obtenez l'élément sélectionné de la ListView
            DeliveryRequest selectedDelivery = listViewDelivery.getSelectionModel().getSelectedItem();

            // Vérifiez si un élément a été réellement sélectionné
            if (selectedDelivery != null) {
                // Appelez votre fonction avec la DeliveryRequest sélectionnée en tant que paramètre
                handleDeliverySelection(selectedDelivery);
                Courier correspondingCourier = getCorrespondingCourier(selectedDelivery);
                int id = correspondingCourier != null ? correspondingCourier.getId() : -1;
                //System.out.println(id);
                //System.out.println(selectedDelivery);
            }
        });

        delete_delivery.setOnAction(event -> {
            // Obtenez l'élément sélectionné de la ListView
            DeliveryRequest selectedDelivery = listViewDelivery.getSelectionModel().getSelectedItem();
            if (selectedDelivery != null) {
                Courier correspondingCourier = getCorrespondingCourier(selectedDelivery);
                int id = correspondingCourier != null ? correspondingCourier.getId() : -1;
                //System.out.println("caca");

                // Appeler la fonction de suppression ici
                handleDeleteDelivery(selectedDelivery, id);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Choisissez d'abord un point de livraison à annuler");
                alert.showAndWait();
            }
        });

    }

    /**
     * Get the corresponding courier for a given delivery request.
     *
     * @param selectedDelivery The selected delivery request.
     * @return The corresponding courier or null if not found.
     */
    private Courier getCorrespondingCourier(DeliveryRequest selectedDelivery) {
        for (DeliveryTour deliveryTour : controller.getListeDelivery()) {
            for (DeliveryRequest delivery : deliveryTour.getRequests()) {
                if (delivery.equals(selectedDelivery)) {
                    return deliveryTour.getCourier();
                }
            }
        }
        return null;
    }

    /**
     * Handle the selection of a delivery request, updating the map accordingly.
     *
     * @param selectedDelivery The selected delivery request.
     */
    private void handleDeliverySelection(DeliveryRequest selectedDelivery) {
        if (engine != null) {
            String centerMapScript = String.format("map.setView([%f, %f], 14);", selectedDelivery.getDeliveryLocation().getLatitude(), selectedDelivery.getDeliveryLocation().getLongitude()+0.004);
            int index=0;
            for(int i=0;i<centerMapScript.length();i++){
                if(centerMapScript.charAt(i)==','){
                    index++;
                    if(index==1 || index==3){
                        centerMapScript = centerMapScript.substring(0, i) + '.' + centerMapScript.substring(i + 1);
                    }
                }
            }
            String markersJs = drawPaths(controller.getCityMap(),selectedDelivery);
            String mapHtml = MAP_HTML_TEMPLATE.formatted(markersJs);
            //System.out.println("LLAAAA"+mapHtml);
            engine.loadContent(mapHtml);
        }
    }

    /**
     * Handle the saving of the current map, writing it to a file.
     *
     * @param event The action event triggering the map save.
     */
    @FXML
    void handleSaveMap(ActionEvent event) {

        //System.out.println(controller.getListeDelivery().get(0).getStartTime());
        // Construction du nom de fichier


        String fileName = String.format("Deliveries_%s_%s.xml", controller.getGlobalDate(), controller.getFileName());

        // Get the current working directory
        String workingDir = System.getProperty("user.dir");

        // Construct the file path
        Path filePath = Paths.get(workingDir, "archives", fileName);

        // Create the file
        File file = new File(filePath.toString());


        // Écriture dans le fichier
        try (FileWriter fileWriter = new FileWriter(file);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {

            // Vérifier si le fichier existe
            if (file.exists()) {
                // Si le fichier existe, effacer son contenu
                new PrintWriter(file).close();
            }

            // Sauvegarde le contenu de la carte de la ville dans le fichier
            boolean saved = controller.saveCityMapToFile(file.getAbsolutePath());

            if (saved) {
                // If saved successfully, show the success dialog
                //FXMLLoader loader = new FXMLLoader(getClass().getResource("successSave.fxml"));
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/insalyonif/hubert/successSave.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setTitle("Success");
                stage.setScene(new Scene(root));
                stage.show();
            }

            //System.out.println("Fichier enregistré avec succès à : " + file.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
            // Gérer les exceptions liées à l'écriture du fichier
        }
    }

    /**
     * Display all the delivery points on the map, updating the WebView content and associated data.
     */
    public void displayAllTheDeliveryPoints(){

        String markersJs = drawPaths(controller.getCityMap(), null);
        String mapHtml = MAP_HTML_TEMPLATE.formatted(markersJs);
        engine.loadContent(mapHtml);
        setCourierIHM(controller.getListeDelivery());
        this.setDeliveryRequestIHM(controller.getListeDelivery().get(0).getRequests());
        courier.setValue(controller.getListeDelivery().get(0).getCourier());

    }

    public void handleMapClick(double lat, double lng) {
        setLastClickedCoordinates(lat, lng);
        //System.out.println("Latitude: " + lat + ", Longitude: " + lng);
    }
}