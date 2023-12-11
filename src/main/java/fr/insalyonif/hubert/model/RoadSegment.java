package fr.insalyonif.hubert.model;

/**
 * Represents a road segment in a city map.
 * Each segment is defined by an origin, a destination, a name, and a length.
 */
public class RoadSegment {
    private Intersection origin;
    private Intersection destination;
    private String name;
    private double length;

    /**
     * Constructor to create a new road segment.
     *
     * @param origin      The origin intersection of the road segment.
     * @param destination The destination intersection of the road segment.
     * @param name        The name of the road segment.
     * @param length      The length of the road segment in kilometers.
     */
    public RoadSegment(Intersection origin, Intersection destination, String name, double length) {
        this.origin = origin;
        this.destination = destination;
        this.name = name;
        this.length = length;
    }

    /**
     * Returns the origin intersection of the road segment.
     *
     * @return The origin intersection.
     */
    public Intersection getOrigin() {
        return origin;
    }

    /**
     * Returns the destination intersection of the road segment.
     *
     * @return The destination intersection.
     */
    public Intersection getDestination() {
        return destination;
    }

    /**
     * Returns the name of the road segment.
     *
     * @return The name of the segment.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the length of the road segment.
     *
     * @return The length of the segment in kilometers.
     */
    public double getLength() {
        return length;
    }

    /**
     * Sets the origin intersection of the road segment.
     *
     * @param origin The new origin intersection.
     */
    public void setOrigin(Intersection origin) {
        this.origin = origin;
    }

    /**
     * Sets the destination intersection of the road segment.
     *
     * @param destination The new destination intersection.
     */
    public void setDestination(Intersection destination) {
        this.destination = destination;
    }

    /**
     * Sets the name of the road segment.
     *
     * @param name The new name of the segment.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the length of the road segment.
     *
     * @param length The new length of the segment in kilometers.
     */
    public void setLength(double length) {
        this.length = length;
    }

    /**
     * Returns a string representation of the road segment.
     *
     * @return The textual representation of the road segment.
     */
    @Override
    public String toString() {
        return "RoadSegment{" +
                "origin=" + origin +
                ", destination=" + destination +
                ", name='" + name + '\'' +
                ", length=" + length +
                '}';
    }
}
