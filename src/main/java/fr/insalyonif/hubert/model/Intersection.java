package fr.insalyonif.hubert.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an intersection in a city map.
 * Each intersection is defined by its geographical coordinates,
 * a unique identifier, and a position. It may also have road segments
 * that precede and follow it.
 */
public class Intersection implements Comparable<Intersection> {
    private double latitude;
    private double longitude;
    private List<RoadSegment> predecessors;
    private List<RoadSegment> successors;
    private long id;
    private int pos;

    /**
     * Constructor to create a new intersection.
     *
     * @param latitude  The latitude of the intersection.
     * @param longitude The longitude of the intersection.
     * @param id        The unique identifier of the intersection.
     * @param pos       The position of the intersection in a specific context (e.g., in a list).
     */
    public Intersection(double latitude, double longitude, long id, int pos) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.predecessors = new ArrayList<>();
        this.successors = new ArrayList<>();
        this.id = id;
        this.pos = pos;
    }

    /**
     * Returns the latitude of the intersection.
     *
     * @return The latitude.
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Sets the latitude of the intersection.
     *
     * @param latitude The new latitude.
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Returns the longitude of the intersection.
     *
     * @return The longitude.
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Sets the longitude of the intersection.
     *
     * @param longitude The new longitude.
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Returns the list of road segments preceding the intersection.
     *
     * @return The list of predecessors.
     */
    public List<RoadSegment> getPredecessors() {
        return predecessors;
    }

    /**
     * Sets the list of road segments preceding the intersection.
     *
     * @param predecessors The new list of predecessors.
     */
    public void setPredecessors(List<RoadSegment> predecessors) {
        this.predecessors = predecessors;
    }

    /**
     * Returns the list of road segments following the intersection.
     *
     * @return The list of successors.
     */
    public List<RoadSegment> getSuccessors() {
        return successors;
    }

    /**
     * Sets the list of road segments following the intersection.
     *
     * @param successors The new list of successors.
     */
    public void setSuccessors(List<RoadSegment> successors) {
        this.successors = successors;
    }

    /**
     * Returns a string representation of the coordinates of the intersection.
     *
     * @return The coordinates as a string.
     */
    public String getCoordinates() {
        return "lat: " + this.latitude + " ; lng: " + this.longitude;
    }

    /**
     * Returns the unique identifier of the intersection.
     *
     * @return The identifier.
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the intersection.
     *
     * @param id The new identifier.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Returns the position of the intersection.
     *
     * @return The position.
     */
    public int getPos() {
        return pos;
    }

    /**
     * Sets the position of the intersection.
     *
     * @param pos The new position.
     */
    public void setPos(int pos) {
        this.pos = pos;
    }

    /**
     * Returns a string representation of the intersection.
     *
     * @return The textual representation of the intersection.
     */
    @Override
    public String toString() {
        return "Intersection{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", id=" + id +
                ", pos=" + pos +
                '}';
    }

    @Override
    public int compareTo(Intersection other) {
        // Implement comparison logic based on your requirements.
        // For example, you can compare based on the intersection's position.
        return Integer.compare(this.getPos(), other.getPos());
    }
}
