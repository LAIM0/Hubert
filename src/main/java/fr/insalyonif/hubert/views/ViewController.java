package fr.insalyonif.hubert.views;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Random;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.stage.FileChooser;
import java.io.File;

import fr.insalyonif.hubert.model.*;
import javafx.stage.Stage;


public class ViewController implements Initializable {
    @FXML
    private WebView webView;

    @FXML
    private Button delivery;

    @FXML
    private Button saveButton;

    private CityMap cityMap;
    private Dijkstra dij;

    private DijkstraInverse dijInv;
    private int sizeGraph;

    private  WebEngine engine;
    private ArrayList<DeliveryRequest> listeDelivery;

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
                var map = L.map('map').setView([45.755, 4.87], 15);
                L.tileLayer('https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}.png', {
                    attribution: 'Map tiles by Carto, under CC BY 3.0. Data by OpenStreetMap, under ODbL.',
                    maxZoom: 18
                }).addTo(map);
                var customIcon = L.icon({
                    iconUrl: 'https://upload.wikimedia.org/wikipedia/commons/thumb/e/ed/Map_pin_icon.svg/1200px-Map_pin_icon.svg.png',
                    iconSize: [15, 20],
                    iconAnchor: [12, 12],
                });
                %s
            </script>
        </body>
        </html>
        """;


    @FXML
    void handleOpenNewWindow(ActionEvent event) {
        if(event.getSource()==delivery) {
            try {
                // Charger le fichier FXML de la nouvelle fenêtre
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/insalyonif/hubert/addDelivery.fxml"));
                Parent root = (Parent) loader.load();


                // Créer une nouvelle fenêtre
                Stage newStage = new Stage();
                newStage.setTitle("add Delivery");
                newStage.setScene(new Scene(root));
                DeliveryIHMController deliveryIHM = loader.getController();


                // Afficher la nouvelle fenêtre
                newStage.showAndWait();
                Intersection intersectionPlusProche = trouverIntersectionPlusProche(deliveryIHM.getLatDouble(), deliveryIHM.getLngDouble(), cityMap.getIntersections());

                // Afficher les résultats
                System.out.println("Coordonnées de l'emplacement donné : " + deliveryIHM.getLatDouble() + ", " + deliveryIHM.getLngDouble());
                System.out.println("Intersection la plus proche : " + intersectionPlusProche.getLatitude() + ", " + intersectionPlusProche.getLongitude());


                boolean b1 = dij.runDijkstra(intersectionPlusProche, sizeGraph);
                boolean b2 = dijInv.runDijkstra(intersectionPlusProche, sizeGraph);
                //Si un des deux false alors pop up BOOL1 && BOOL2
                if(b1 && b2) {
                    DeliveryRequest deli= new DeliveryRequest((intersectionPlusProche));
                    listeDelivery.add(deli);
                    Graph g = new CompleteGraph(dij.getChemins(), listeDelivery, cityMap);


                    TSP tsp = new TSP1();
                    tsp.searchSolution(20000, g);
                    System.out.print("Solution of cost " + tsp.getSolutionCost());
                    for (int i = 0; i < listeDelivery.size(); i++)
                        System.out.print(tsp.getSolution(i) + " ");
                    System.out.println("0");
                    List<Chemin> bestChemin = tsp.bestCheminGlobal(dij.getChemins());

                    System.out.println("Meilleur chemin global :");
                    for (Chemin chemin : bestChemin) {
                        System.out.println(chemin);
                        //System.out.println("Départ : " + chemin.getDebut() + " -> Arrivée : " + chemin.getFin()+ " | Coût : " + chemin.getCout());
                    }

                    cityMap.setChemins(bestChemin);
                    MAJDeliveryPointList();
                    String markersJs = drawPaths(cityMap);
                    String mapHtml = MAP_HTML_TEMPLATE.formatted(markersJs);

                    engine.loadContent(mapHtml);
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Point non accessible ");
                    alert.showAndWait();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void MAJDeliveryPointList(){
        List<Chemin> chemins =cityMap.getChemins();
        Map<Intersection, Integer> intersectionIndexMap = new HashMap<>();
        for (int i = 0; i < chemins.size(); i++) {
            Chemin chemin = chemins.get(i);
            intersectionIndexMap.put(chemin.getFin(), i);
        }

        // Trier la liste de points de livraison en fonction de l'ordre des intersections dans les chemins
        listeDelivery.sort((dp1, dp2) -> {
            int index1 = intersectionIndexMap.get(dp1.getDeliveryLocation());
            int index2 = intersectionIndexMap.get(dp2.getDeliveryLocation());
            return Integer.compare(index1, index2);
        });
    }


    public static Intersection trouverIntersectionPlusProche(double lat, double lng, List<Intersection> intersections) {
        if (intersections == null || intersections.isEmpty()) {
            return null; // La liste d'intersections est vide
        }

        Intersection intersectionPlusProche = intersections.get(0);
        double distanceMin = distance(lat, lng, intersectionPlusProche.getLatitude(), intersectionPlusProche.getLongitude());

        for (Intersection intersection : intersections) {
            double distanceActuelle = distance(lat, lng, intersection.getLatitude(), intersection.getLongitude());
            if (distanceActuelle < distanceMin) {
                distanceMin = distanceActuelle;
                intersectionPlusProche = intersection;
            }
        }

        return intersectionPlusProche;
    }

    // Méthode pour calculer la distance entre deux points géographiques en utilisant la formule de Haversine
    private static double distance(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371; // Rayon de la Terre en kilomètres

        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return earthRadius * c; // Distance en kilomètres
    }
    private StringBuilder displayDeliveryPoints() {
        StringBuilder markersJs = new StringBuilder();
        String iconUrl   = "https://cdn-icons-png.flaticon.com/512/124/124434.png";
        //https://api.iconify.design/mdi/map-marker.svg?color=%23ffae42
        String markerJs = String.format(
                "var marker = L.marker([" + cityMap.getWareHouseLocation().getLatitude() + ", " +  cityMap.getWareHouseLocation().getLongitude() + "], {icon: L.icon({iconUrl: '%s', iconSize: [30, 40], iconAnchor: [15, 30]})}).addTo(map);"
                        + "marker.bindTooltip('%s').openTooltip();", iconUrl, "Warehouse"
        );
        int i=0;
        for (DeliveryRequest deliveryRequest : listeDelivery) {
            markersJs.append(markerJs);
            iconUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/ed/Map_pin_icon.svg/1200px-Map_pin_icon.svg.png";
            i++;
            markerJs = String.format(
                    "var marker = L.marker([" + deliveryRequest.getDeliveryLocation().getLatitude() + ", " +  deliveryRequest.getDeliveryLocation().getLongitude() + "],  {icon: L.icon({iconUrl: '%s', iconSize: [15, 20], iconAnchor: [8, 20]})}).addTo(map);"
                            + "marker.bindTooltip('Nb: %d').openTooltip();",
                     //deliveryRequest.getDeliveryLocation().getId()
                    iconUrl,i
            );

            markersJs.append(markerJs);
        }
        return markersJs;
    }


    private String drawPaths(CityMap cityMap) {
        StringBuilder markersJs = displayDeliveryPoints();
        int index=0;
        for (int i = cityMap.getChemins().size() - 1; i >= 0; i--) {
            Chemin chemin = cityMap.getChemins().get(i);
            // Begin with the end intersection
            int currentIndex = chemin.getFin().getPos();
            int nextIndex = chemin.getPi()[currentIndex];

            StringBuilder polylineCoords = new StringBuilder("[");
            polylineCoords.append("[").append(chemin.getFin().getLatitude()).append(", ").append(chemin.getFin().getLongitude()).append("],");

            System.out.println("fin"+chemin.getFin());
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
            System.out.println("debut"+chemin.getDebut());
            // End with the start intersection
            polylineCoords.append("[").append(chemin.getDebut().getLatitude()).append(", ").append(chemin.getDebut().getLongitude()).append("]");
            polylineCoords.append("]");


            //if(index==0){
                //String polylineJs = "L.polyline(" + polylineCoords + ", {color: '#0000FF'}).addTo(map);";
                //markersJs.append(polylineJs);
              //   System.out.println(polylineJs);
            //}else{
                String polylineJs = "L.polyline(" + polylineCoords + ", {color: '"+generateRandomColor()+"'}).addTo(map);";
                markersJs.append(polylineJs);
                 System.out.println(polylineJs);
            //}

                 System.out.println("Chemin index "+ i);
           

            index++;
        }
        return markersJs.toString();
    }

    public static String generateRandomColor() {
        // Générateur de nombres aléatoires
        Random random = new Random();

        // Génération de trois composants de couleur (R, G, B)
        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);

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

    /*@Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cityMap = new CityMap();
        listeDelivery = new ArrayList<>();
        engine = webView.getEngine();
        try {
            String xmlMap = "src/main/resources/fr/insalyonif/hubert/fichiersXML2022/mediumMap.xml";
            cityMap.loadFromXML(xmlMap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        sizeGraph = cityMap.getIntersections().size(); // Mettez la taille correcte de votre graphe
        dij = new Dijkstra(sizeGraph, cityMap);
        //dij.runDijkstra(cityMap.getIntersections().get(7), sizeGraph);
        dijInv = new DijkstraInverse(sizeGraph,cityMap);
        // dijInv.runDijkstra(cityMap.getIntersections().get(7), sizeGraph);
        // dij.runDijkstra(cityMap.getIntersections().get(10), sizeGraph);
        // dijInv.runDijkstra(cityMap.getIntersections().get(10), sizeGraph);
        // cityMap.setChemins(dijInv.getChemins());
        
        String mapHtml = MAP_HTML_TEMPLATE;

        engine.loadContent(mapHtml);
    }*/
    @FXML
    private Button loadMapButton; // Add a button for loading the map

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize the UI without loading the map
        cityMap = new CityMap();
        listeDelivery = new ArrayList<>();
        engine = webView.getEngine();
    }

    @FXML
    void handleLoadMap(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open XML Map File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files", "*.xml"));
        File selectedFile = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());

        if (selectedFile != null) {
            try {
                // Load the selected XML map file
                cityMap.loadFromXML(selectedFile.getAbsolutePath());

                // Set up Dijkstra and other necessary initialization
                sizeGraph = cityMap.getIntersections().size();
                dij = new Dijkstra(sizeGraph, cityMap);
                dijInv = new DijkstraInverse(sizeGraph, cityMap);

                // Load the map
                loadMap();
            } catch (Exception e) {
                e.printStackTrace();
                // Handle the exception (e.g., show an error message)
            }
        }
    }

    private void loadMap() {
        // Code for loading the map, as previously in the initialize method

        // For example:
        String mapHtml = MAP_HTML_TEMPLATE;
        engine.loadContent(mapHtml);
    }

    @FXML
    void handleSaveMap(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save CityMap File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files", "*.xml"));
        File selectedFile = fileChooser.showSaveDialog(((Node) event.getSource()).getScene().getWindow());

        if (selectedFile != null) {
            try {
                saveCityMapToFile(selectedFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
                // Handle the exception (e.g., show an error message)
            }
        }
    }

    private void saveCityMapToFile(String filePath) throws IOException {
        // Get the CityMap instance from your controller
        ; // Replace with the actual reference to your CityMap instance

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // Save the CityMap data to the file
            // You can customize this based on your CityMap data structure and attributes
            writer.println("<citymap>");
            writer.println("    <warehouse address=\"" + cityMap.getWareHouseLocation().getId() + "\" />");

            for (Intersection intersection : cityMap.getIntersections()) {
                writer.println("    <intersection id=\"" + intersection.getId() +
                        "\" latitude=\"" + intersection.getLatitude() +
                        "\" longitude=\"" + intersection.getLongitude() + "\" />");
            }

            for (Chemin chemin : cityMap.getChemins()) {
                writer.println("    <chemin debut=\"" + chemin.getDebut().getId() +
                        "\" fin=\"" + chemin.getFin().getId() +
                        "\" cout=\"" + chemin.getCout() + "\">");

                for (int i = 0; i < chemin.getPi().length; i++) {
                    writer.println("        <pi index=\"" + i + "\">" + chemin.getPi()[i] + "</pi>");
                }

                writer.println("    </chemin>");
            }

            writer.println("</citymap>");
        }
    }
}
