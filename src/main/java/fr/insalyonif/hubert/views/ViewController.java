package fr.insalyonif.hubert.views;
import fr.insalyonif.hubert.controller.Controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javafx.util.StringConverter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;
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
    private ListView<DeliveryRequest> listViewDelivery;

    private ObservableList<DeliveryRequest> listDelivery;

    private  WebEngine engine;

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
                    var map = L.map('map').setView([45.755, 4.87], 15);
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

    public WebEngine getEngine() {
        return engine;
    }

    @FXML
    void addNewCourrier(ActionEvent event){
        if(controller !=null) {
            controller.newDeliveryTour();
            setCourierIHM(controller.getListeDelivery());
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText("Successfully added ! :)");
            alert.showAndWait();
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("You need to load a map first");
            alert.showAndWait();
        }
    }
    @FXML
    void handleOpenNewWindow(ActionEvent event) {
        if(event.getSource()==delivery || event.getSource()==validate_delivery) {
            if(controller !=null) {
                try {
                    // Charger le fichier FXML de la nouvelle fenêtre
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/insalyonif/hubert/addDelivery.fxml"));
                    Parent root = (Parent) loader.load();


                    // Créer une nouvelle fenêtre
                    Stage newStage = new Stage();
                    newStage.setTitle("add Delivery");
                    newStage.setScene(new Scene(root));
                    DeliveryIHMController deliveryIHM = loader.getController();
                    if(event.getSource()==validate_delivery){
                        deliveryIHM.setLat(lastClickedLat);
                        deliveryIHM.setLng(lastClickedLng);
                    }
                    deliveryIHM.setListCourier(listCourier);

                    // Afficher la nouvelle fenêtre
                    newStage.showAndWait();

                    int traceNewDeliveryPoint = controller.newDeliveryPoint(deliveryIHM,deliveryIHM.getCourier().getId());
                    if(traceNewDeliveryPoint == 0){
                        String markersJs = drawPaths(controller.getCityMap(), null);
                        String mapHtml = MAP_HTML_TEMPLATE.formatted(markersJs);
                        engine.loadContent(mapHtml);
                        courier.setValue(deliveryIHM.getCourier());
                        this.setDeliveryRequestIHM(controller.getListeDelivery().get(deliveryIHM.getCourier().getId()).getRequests());
                    } else if(traceNewDeliveryPoint == 1){
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("Point non accessible");
                        alert.showAndWait();
                    } else if(traceNewDeliveryPoint == 2){
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("Point déjà présent dans la liste");
                        alert.showAndWait();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Il faut d'abord choisir une MAP");
                alert.showAndWait();
            }

        }
    }


    public void importAllTheDeliveriesIntoController(String pathname) throws Exception {
        controller.loadArchiveFile(pathname);
    }



    // Méthode pour calculer la distance entre deux points géographiques en utilisant la formule de Haversine

    private StringBuilder displayDeliveryPoints(DeliveryRequest target) {
        StringBuilder markersJs = new StringBuilder();
        String iconUrl   = "https://cdn-icons-png.flaticon.com/512/124/124434.png";
        //https://api.iconify.design/mdi/map-marker.svg?color=%23ffae42
        String markerJs = String.format(
                "var marker = L.marker([" + controller.getCityMap().getWareHouseLocation().getLatitude() + ", " +  controller.getCityMap().getWareHouseLocation().getLongitude() + "], {icon: L.icon({iconUrl: '%s', iconSize: [25, 35], iconAnchor: [15, 30]})}).addTo(map);"
                        + "marker.bindTooltip('%s',{permanent:false}).openTooltip();", iconUrl, "Warehouse"
        );
        markersJs.append(markerJs);

        for( DeliveryTour deliveryTour : controller.getListeDelivery()) {
            int i=0;
            for (DeliveryRequest deliveryRequest : deliveryTour.getRequests()) {
                markersJs.append(markerJs);
                if(target!=null && deliveryRequest.getDeliveryLocation().getId()==target.getDeliveryLocation().getId()){
                    iconUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/ed/Map_pin_icon.svg/1200px-Map_pin_icon.svg.png";
                    i++;
                    markerJs = String.format(
                            "var marker = L.marker([" + deliveryRequest.getDeliveryLocation().getLatitude() + ", " + deliveryRequest.getDeliveryLocation().getLongitude() + "],  {icon: L.icon({iconUrl: '%s', iconSize: [30, 40], iconAnchor: [15, 40]})}).addTo(map);"
                                    + "marker.bindTooltip('Nb: %d',{permanent:false}).openTooltip();",
                            //deliveryRequest.getDeliveryLocation().getId()
                            iconUrl, i
                    );
                }else {
                    iconUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/ed/Map_pin_icon.svg/1200px-Map_pin_icon.svg.png";
                    i++;
                    markerJs = String.format(
                            "var marker = L.marker([" + deliveryRequest.getDeliveryLocation().getLatitude() + ", " + deliveryRequest.getDeliveryLocation().getLongitude() + "],  {icon: L.icon({iconUrl: '%s', iconSize: [15, 20], iconAnchor: [8, 20]})}).addTo(map);"
                                    + "marker.bindTooltip('Nb: %d',{permanent:false}).openTooltip();",
                            //deliveryRequest.getDeliveryLocation().getId()
                            iconUrl, i
                    );
                }

                markersJs.append(markerJs);
            }
        }
        return markersJs;
    }


    private String drawPaths(CityMap cityMap,DeliveryRequest deliveryRequest) {
        Courier courrierComboBox =courier.getValue();
        if(courrierComboBox==null){
            courrierComboBox = controller.getListeDelivery().get(0).getCourier();
        }
        StringBuilder markersJs = displayDeliveryPoints(deliveryRequest);
        for(DeliveryTour deliveryTour : controller.getListeDelivery()) {
            if(deliveryTour.getPaths()!=null) {
                for (int i = deliveryTour.getPaths().size() - 1; i >= 0; i--) {
                    Chemin chemin = deliveryTour.getPaths().get(i);
                    // Begin with the end intersection
                    int currentIndex = chemin.getFin().getPos();
                    int nextIndex = chemin.getPi()[currentIndex];

                    StringBuilder polylineCoords = new StringBuilder("[");
                    polylineCoords.append("[").append(chemin.getFin().getLatitude()).append(", ").append(chemin.getFin().getLongitude()).append("],");

                    System.out.println("fin" + chemin.getFin());
                    // Loop through pi array
                    while (nextIndex != -1) {
                        Intersection currentIntersection = cityMap.findIntersectionByPos(nextIndex);
                        System.out.println(currentIntersection);
                        if (currentIntersection != null) {
                            polylineCoords.append("[").append(currentIntersection.getLatitude()).append(", ").append(currentIntersection.getLongitude()).append("],");
                        }
                        currentIndex = nextIndex;
                        nextIndex = chemin.getPi()[currentIndex];
                    }
                    System.out.println("debut" + chemin.getDebut());
                    // End with the start intersection
                    polylineCoords.append("[").append(chemin.getDebut().getLatitude()).append(", ").append(chemin.getDebut().getLongitude()).append("]");
                    polylineCoords.append("]");
                    String polylineJs;
                    if (deliveryTour.getCourier().getId() == courrierComboBox.getId()) {
                        polylineJs = "L.polyline(" + polylineCoords + ", {color: '" + generateRandomColor() + "'}).addTo(map);";
                    } else {
                        polylineJs = "L.polyline(" + polylineCoords + ", {color: 'grey'}).addTo(map);";
                    }

                    markersJs.append(polylineJs);
                    System.out.println(polylineJs);
                    System.out.println("Chemin index " + i);
                }
            }
        }
        return markersJs.toString();
    }

    public static String generateRandomColor() {
        // Générateur de nombres aléatoires
        Random random = new Random();

        // Génération de trois composants de couleur (R, G, B)
        int red = random.nextInt(200) + 55;   // Entre 55 et 255 pour des couleurs plus vives
        int green = random.nextInt(200) + 55; // Entre 55 et 255 pour des couleurs plus vives
        int blue = random.nextInt(200) + 55;

        // Conversion des composants de couleur en format hexadécimal
        String hexRed = Integer.toHexString(red);
        String hexGreen = Integer.toHexString(green);
        String hexBlue = Integer.toHexString(blue);

        // Assurez-vous que chaque composant a deux chiffres hexadécimaux
        hexRed = padZero(hexRed);
        hexGreen = padZero(hexGreen);
        hexBlue = padZero(hexBlue);

        // Concaténation des composants pour obtenir la couleur complète
        String hexColor = "#" + hexRed + hexGreen + hexBlue;

        return hexColor.toUpperCase(); // Retourne la couleur en majuscules
    }

    private static String padZero(String hexComponent) {
        return hexComponent.length() == 1 ? "0" + hexComponent : hexComponent;
    }


    void handleCourierSelection(Courier newCourier){

        // Filter the delivery tours for the selected courier
        DeliveryTour selectedTour = controller.getListeDelivery().stream()
        .filter(tour -> tour.getCourier().equals(newCourier))
        .findFirst()
        .orElse(null);

        if (selectedTour != null) {
            setDeliveryRequestIHM(selectedTour.getRequests());
            String markersJs = drawPaths(controller.getCityMap(),null);
            String mapHtml = MAP_HTML_TEMPLATE.formatted(markersJs);
            engine.loadContent(mapHtml);

            }
         }

    @FXML
    void handleLoadMap(ActionEvent event) throws IOException {
        handleSaveMap(event);
//        FileChooser fileChooser = new FileChooser();
//        fileChooser.setTitle("Open XML Map File");
//        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files", "*.xml"));
//        File selectedFile = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());
//
//        if (selectedFile != null) {
//            try {
//                // Load the selected XML map file
//                controller = new Controller(selectedFile.getAbsolutePath());
//                setCourierIHM(controller.getListeDelivery());
//                controller.setGlobalDate(LocalDate.now());
//                listDelivery.clear();
//
//                String markersJs = displayDeliveryPoints(null).toString();
//                String mapHtml = MAP_HTML_TEMPLATE.formatted(markersJs);
//
//                engine.loadContent(mapHtml);
//            } catch (Exception e) {
//                e.printStackTrace();
//                // Handle the exception (e.g., show an error message)
//            }
//        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/insalyonif/hubert/newMap.fxml"));
        Parent root = (Parent) loader.load();


        // Créer une nouvelle fenêtre
        Stage newStage = new Stage();
        newStage.setTitle("New Map");
        newStage.setScene(new Scene(root));
        newStage.showAndWait();
    }

    void loadMap(LocalDate datePicker, String selectedFilePath ) {

                // Load the selected XML map file
                controller = new Controller(selectedFilePath);
                setCourierIHM(controller.getListeDelivery());
                controller.setGlobalDate(datePicker);
                System.out.println("passe");
                dateLabel.setText(String.valueOf(datePicker));

                int lastIndex = selectedFilePath.lastIndexOf('/');
                // Extraire la partie après le dernier '/'
                String dernierMorceau = selectedFilePath.substring(lastIndex + 1);
                fileNameLabel.setText(dernierMorceau);


                String markersJs = displayDeliveryPoints(null).toString();
                String mapHtml = MAP_HTML_TEMPLATE.formatted(markersJs);

                engine.loadContent(mapHtml);
    }



    @FXML
    void handleImportMap(ActionEvent event) throws Exception {
        // Save le fichier existant
        handleSaveMap(event);

        //selectedFilePath = "";
        //listDelivery.clear();

        // Importer les données du xml
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);

        //getClass().getResource("/fr/insalyonif/hubert/successSave.fxml")


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

            if (map == null){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Ce fichier ne correspond pas :(");
                alert.showAndWait();
                return;
            }

            String fileName = map.getAttribute("fileName");
            LocalDate fileDate = LocalDate.parse(map.getAttribute("globalDate"));


            System.out.println("fileName File: " + fileName);
            System.out.println("fileDate File: " + fileDate);

            // Use Path to extract file name and extension
            //Path path = Paths.get(selectedFilePath);
            //String fileName = path.getFileName().toString(); // Extracts the file name

            //String[] fileNameParts = fileName.split("_");
            //String lastWord = fileNameParts[fileNameParts.length - 1];

            String stratPath = "src/main/resources/fr/insalyonif/hubert/fichiersXML2022/";
            String pathMap = stratPath + fileName + ".xml";
            System.out.println("Path of the map: " + pathMap);


            // Extract date from the file name
            //String datePattern = "yyyy-MM-dd"; // Adjust the pattern based on the actual date format in the file name
            //DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(datePattern);

            // Extract the date substring from the file name
            //String dateString = fileName.substring(11, 21); // Adjust indices based on the actual position of the date in the file name

            // Parse the date string to LocalDate
            //LocalDate fileDate = LocalDate.parse(dateString, dateFormatter);
            //System.out.println("File Date: " + fileDate);

//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/insalyonif/hubert/ihm.fxml"));
//            Parent root = loader.load();
//
//            // Afficher la nouvelle scène
//            Scene scene = new Scene(root);
//            Stage stage1 = (Stage) ((Node) event.getSource()).getScene().getWindow();
//            stage1.setScene(scene);

            //ViewController viewController = loader.getController();
            loadMap(fileDate, pathMap);
            importAllTheDeliveriesIntoController(selectedFilePath);
            displayAllTheDeliveryPoints();

            stage.show();
        }


    }

    public void setDeliveryRequestIHM(ArrayList<DeliveryRequest> delivery) {
        listDelivery.clear();
        /*for (DeliveryRequest demande : delivery) {
            listDelivery.add(demande);
        }*/
        listDelivery.addAll(delivery);
    }

    public void setCourierIHM(ArrayList<DeliveryTour> deliveryTours) {
        Courier c =courier.getValue();
        listCourier.clear();
        for (DeliveryTour deliveryTour : deliveryTours) {
            listCourier.add(deliveryTour.getCourier());
        }
        courier.setValue(c);
    }


    public void setLastClickedCoordinates(double lat, double lng) {
        this.lastClickedLat = lat;
        this.lastClickedLng = lng;
    }

    public void handleMapClick(double lat, double lng) {
        setLastClickedCoordinates(lat, lng);
        //System.out.println("Latitude: " + lat + ", Longitude: " + lng);
    }

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
            }
        });

    }

    private void handleDeliverySelection(DeliveryRequest selectedDelivery) {
        if (engine != null) {
            String centerMapScript = String.format("map.setView([%f, %f], 14);", selectedDelivery.getDeliveryLocation().getLatitude(), selectedDelivery.getDeliveryLocation().getLongitude()+0.009);
            int index=0;
            for(int i=0;i<centerMapScript.length();i++){
                if(centerMapScript.charAt(i)==','){
                    index++;
                    if(index==1 || index==3){
                        centerMapScript = centerMapScript.substring(0, i) + '.' + centerMapScript.substring(i + 1);
                    }
                }
            }
            String markersJs = drawPaths(controller.getCityMap(),selectedDelivery)+centerMapScript;
            String mapHtml = MAP_HTML_TEMPLATE.formatted(markersJs);
            System.out.println("LLAAAA"+mapHtml);
            engine.loadContent(mapHtml);
        }
    }

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

            System.out.println("Fichier enregistré avec succès à : " + file.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
            // Gérer les exceptions liées à l'écriture du fichier
        }
    }

    public void displayAllTheDeliveryPoints(){
        
        String markersJs = drawPaths(controller.getCityMap(), null);
        String mapHtml = MAP_HTML_TEMPLATE.formatted(markersJs);
        engine.loadContent(mapHtml);
        setCourierIHM(controller.getListeDelivery());
        this.setDeliveryRequestIHM(controller.getListeDelivery().get(0).getRequests());
        this.setDeliveryRequestIHM(controller.getListeDelivery().get(1).getRequests());

//        for(DeliveryTour delivery : controller.getListeDelivery()){
//            for(Chemin request : delivery.getPaths()){
//                 System.out.println("Ici "+request);
//            }
//        }
    }


}