package fr.insalyonif.hubert.model;

import java.io.File;
import java.util.List;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * Represents a city map with a set of intersections and the location of a warehouse.
 * Provides methods for managing intersections, defining the warehouse location,
 * and loading data from an XML file.
 */
public class CityMap {
    private List<Intersection> intersections;
    private Intersection wareHouseLocation;
    //private List<Chemin> chemins; // List to store Chemin objects

    /**
     * Default constructor that initializes the list of intersections.
     */
    public CityMap() {
        this.intersections = new ArrayList<>();
    }

    /**
     * Returns the list of intersections in the city.
     *
     * @return the list of intersections.
     */
    public List<Intersection> getIntersections() {
        return intersections;
    }

    /**
     * Sets the list of intersections in the city.
     *
     * @param intersections the new list of intersections.
     */
    public void setIntersections(List<Intersection> intersections) {
        this.intersections = intersections;
    }

    /**
     * Returns the location of the warehouse.
     *
     * @return the intersection representing the warehouse location.
     */
    public Intersection getWareHouseLocation() {
        return wareHouseLocation;
    }

    /**
     * Sets the location of the warehouse.
     *
     * @param wareHouseLocation the intersection representing the new warehouse location.
     */
    public void setWareHouseLocation(Intersection wareHouseLocation) {
        this.wareHouseLocation = wareHouseLocation;
    }

    /**
     * Loads map data from an XML file.
     * Parses the file and initializes intersections and road segments.
     *
     * @param filename the path of the XML file to load.
     * @throws Exception if an error occurs during file loading or parsing.
     */
    public void loadFromXML(String filename) throws Exception {
        // Create an instance of File for the XML file
        File xmlFile = new File(filename);

        // Initialize the XML document builder
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        // Parse the XML document
        Document doc = dBuilder.parse(xmlFile);

        // Normalize the XML document to eliminate unnecessary white spaces
        doc.getDocumentElement().normalize();

        // Use a Map to store intersections with their ID as the key
        Map<Long, Intersection> intersectionMap = new HashMap<>();

        // Process "intersection" elements in the XML
        NodeList intersectionList = doc.getElementsByTagName("intersection");
        for (int i = 0; i < intersectionList.getLength(); i++) {
            Element intersectionElement = (Element) intersectionList.item(i);

            // Extract attributes of each intersection
            long id = Long.parseLong(intersectionElement.getAttribute("id"));
            double latitude = Double.parseDouble(intersectionElement.getAttribute("latitude"));
            double longitude = Double.parseDouble(intersectionElement.getAttribute("longitude"));

            // Create a new Intersection object and add it to the Map and the list
            Intersection intersection = new Intersection(latitude, longitude, id, i);
            intersectionMap.put(id, intersection);
            this.intersections.add(intersection);
        }

        // Process "warehouse" element to set the warehouse location
        Element warehouse = (Element) doc.getElementsByTagName("warehouse").item(0);
        long warehouseId = Long.parseLong(warehouse.getAttribute("address"));
        this.wareHouseLocation = intersectionMap.get(warehouseId);

        // Process "segment" elements in the XML
        NodeList segmentList = doc.getElementsByTagName("segment");
        for (int i = 0; i < segmentList.getLength(); i++) {
            Element segmentElement = (Element) segmentList.item(i);

            // Extract attributes of each segment
            long originId = Long.parseLong(segmentElement.getAttribute("origin"));
            long destinationId = Long.parseLong(segmentElement.getAttribute("destination"));
            String name = segmentElement.getAttribute("name");
            double length = Double.parseDouble(segmentElement.getAttribute("length"));

            // Retrieve origin and destination intersections
            Intersection origin = intersectionMap.get(originId);
            Intersection destination = intersectionMap.get(destinationId);

            // Create a new RoadSegment and update successor and predecessor lists
            RoadSegment segment = new RoadSegment(origin, destination, name, length);
            origin.getSuccessors().add(segment);
            destination.getPredecessors().add(segment);
        }
    }

    /**
     * Finds an intersection based on its position.
     *
     * @param pos the position of the intersection to find.
     * @return the corresponding intersection, or null if not found.
     */
    public Intersection findIntersectionByPos(int pos) {
        // Iterate through all intersections
        for (Intersection intersection : this.intersections) {
            if (intersection.getPos() == pos) {
                // Return the intersection if the position matches
                return intersection;
            }
        }
        // Return null if no matching intersection is found
        return null;
    }

    /**
     * Finds an intersection based on its ID.
     *
     * @param id the ID of the intersection to find.
     * @return the corresponding intersection, or null if not found.
     */
    public Intersection findIntersectionByID(long id) {
        // Iterate through all intersections
        for (Intersection intersection : this.intersections) {
            if (intersection.getId() == id) {
                // Return the intersection if the ID matches
                return intersection;
            }
        }
        // Return null if no matching intersection is found
        return null;
    }

    /**
     * Display the intersections.
     *
     * @param filePath The path of the file with the intersection.
     */
    public void displayIntersections(String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            for (Intersection intersection : intersections) {
                writer.println("Intersection ID: " + intersection.getId());
                writer.println("Latitude: " + intersection.getLatitude());
                writer.println("Longitude: " + intersection.getLongitude());

                // Write predecessors
                writer.print("Predecessors: ");
                for (RoadSegment predecessor : intersection.getPredecessors()) {
                    writer.print(predecessor.getOrigin().getId() + " ");
                }
                writer.println();

                // Write successors
                writer.print("Successors: ");
                for (RoadSegment successor : intersection.getSuccessors()) {
                    writer.print(successor.getDestination().getId() + " ");
                }
                writer.println();

                writer.println("----------");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}