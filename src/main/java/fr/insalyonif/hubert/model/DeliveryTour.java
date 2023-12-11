package fr.insalyonif.hubert.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a delivery tour performed by a courier, including delivery requests, paths, and time information.
 */
public class DeliveryTour {
    private Courier courier;
    private ArrayList<DeliveryRequest> requests;
    private List<Chemin> paths;
    private Instant startTime;
    private Instant endTime;
    private Dijkstra dij;
    private ArrayList<Chemin> cheminDij;
    private DijkstraInverse dijInv;

    /**
     * Default constructor to create a new DeliveryTour.
     */
    public DeliveryTour() {
        requests = new ArrayList<>();
        cheminDij = new ArrayList<>();
    }

    /**
     * Gets the courier associated with the delivery tour.
     *
     * @return The courier.
     */
    public Courier getCourier() {
        return courier;
    }

    /**
     * Sets the Dijkstra algorithm for the delivery tour.
     *
     * @param dij The Dijkstra algorithm.
     */
    public void setDijkstra(Dijkstra dij) {
        this.dij = dij;
    }

    /**
     * Gets the Dijkstra algorithm associated with the delivery tour.
     *
     * @return The Dijkstra algorithm.
     */
    public Dijkstra getDijkstra() {
        return this.dij;
    }

    /**
     * Sets the Inverse Dijkstra algorithm for the delivery tour.
     *
     * @param dijInv The Inverse Dijkstra algorithm.
     */
    public void setDijkstraInverse(DijkstraInverse dijInv) {
        this.dijInv = dijInv;
    }

    /**
     * Gets the Inverse Dijkstra algorithm associated with the delivery tour.
     *
     * @return The Inverse Dijkstra algorithm.
     */
    public DijkstraInverse getDijkstraInverse() {
        return this.dijInv;
    }

    /**
     * Sets the courier for the delivery tour.
     *
     * @param courier The courier.
     */
    public void setCourier(Courier courier) {
        this.courier = courier;
    }

    /**
     * Updates the list of paths computed by Dijkstra.
     *
     * @param chemins The list of paths.
     */
    public void majCheminsDij(ArrayList<Chemin> chemins) {
        cheminDij.addAll(chemins);
    }

    /**
     * Clears the list of paths computed by Dijkstra.
     */
    public void clearCheminsDij() {
        cheminDij.clear();
    }

    /**
     * Gets the list of paths computed by Dijkstra.
     *
     * @return The list of paths.
     */
    public ArrayList<Chemin> getCheminDij() {
        return cheminDij;
    }

    /**
     * Gets the list of delivery requests associated with the delivery tour.
     *
     * @return The list of delivery requests.
     */
    public ArrayList<DeliveryRequest> getRequests() {
        return requests;
    }

    /**
     * Sets the list of delivery requests for the delivery tour.
     *
     * @param requests The list of delivery requests.
     */
    public void setRequests(ArrayList<DeliveryRequest> requests) {
        this.requests = requests;
    }

    /**
     * Gets the list of paths associated with the delivery tour.
     *
     * @return The list of paths.
     */
    public List<Chemin> getPaths() {
        return paths;
    }

    /**
     * Sets the list of paths for the delivery tour.
     *
     * @param paths The list of paths.
     */
    public void setPaths(List<Chemin> paths) {
        this.paths = paths;
    }

    /**
     * Gets the start time of the delivery tour.
     *
     * @return The start time.
     */
    public Instant getStartTime() {
        return startTime;
    }

    /**
     * Sets the start time of the delivery tour.
     *
     * @param startTime The start time.
     */
    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    /**
     * Gets the end time of the delivery tour.
     *
     * @return The end time.
     */
    public Instant getEndTime() {
        return endTime;
    }

    /**
     * Sets the end time of the delivery tour.
     *
     * @param endTime The end time.
     */
    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    /**
     * Gets a string representation of the delivery tour.
     *
     * @return The textual representation of the delivery tour.
     */
    @Override
    public String toString() {
        return "DeliveryTour{" +
                "courier=" + courier +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}