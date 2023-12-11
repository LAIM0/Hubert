package fr.insalyonif.hubert.model;

import java.util.Arrays;

/**
 * Represents a path between two intersections in a city map.
 * Provides information about the start and end intersections, the path indices, and the total cost.
 */
public class Chemin {
    private Intersection debut; // Start intersection of the path
    private Intersection fin;   // End intersection of the path
    private int[] pi;           // Array representing the path in terms of indices of visited intersections
    private double cout;        // Total cost of the path

    /**
     * Constructor for the Chemin class.
     *
     * @param debut The starting intersection of the path.
     * @param fin The ending intersection of the path.
     * @param pi An array representing the path in terms of indices of visited intersections.
     * @param cout The total cost of the path.
     */
    public Chemin(Intersection debut, Intersection fin, int[] pi, double cout) {
        this.debut = debut;
        this.fin = fin;
        this.pi = pi;
        this.cout = cout;
    }

    /**
     * Gets the starting intersection of the path.
     *
     * @return The starting intersection of the path.
     */
    public Intersection getDebut() {
        return debut;
    }

    /**
     * Gets the ending intersection of the path.
     *
     * @return The ending intersection of the path.
     */
    public Intersection getFin() {
        return fin;
    }

    /**
     * Gets the array representing the path in terms of indices of visited intersections.
     *
     * @return The array of indices representing the path.
     */
    public int[] getPi() {
        return pi;
    }

    /**
     * Gets the total cost of the path.
     *
     * @return The total cost of the path.
     */
    public double getCout() {
        return cout;
    }

    /**
     * Sets the starting intersection of the path.
     *
     * @param debut The new starting intersection of the path.
     */
    public void setDebut(Intersection debut) {
        this.debut = debut;
    }

    /**
     * Sets the ending intersection of the path.
     *
     * @param fin The new ending intersection of the path.
     */
    public void setFin(Intersection fin) {
        this.fin = fin;
    }

    /**
     * Sets the array representing the path in terms of indices of visited intersections.
     *
     * @param pi The new array of indices representing the path.
     */
    public void setPi(int[] pi) {
        this.pi = pi;
    }

    /**
     * Sets the total cost of the path.
     *
     * @param cout The new total cost of the path.
     */
    public void setCout(double cout) {
        this.cout = cout;
    }

    /**
     * Returns a string representation of the path.
     *
     * @return A string representing the path.
     */
    @Override
    public String toString() {
        return "Chemin{" +
                "debut=" + debut +
                ", fin=" + fin +
                ", pi=" + Arrays.toString(pi) +
                ", cout=" + cout +
                '}';
    }
}
