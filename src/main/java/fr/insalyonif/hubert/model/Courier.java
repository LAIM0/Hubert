package fr.insalyonif.hubert.model;

/**
 * Represents a courier in a delivery system.
 */
public class Courier {
    private int id;

    /**
     * Constructor for the Courier class.
     *
     * @param id The unique identifier of the courier.
     */
    public Courier(int id) {
        this.id = id;
    }

    /**
     * Gets the unique identifier of the courier.
     *
     * @return The unique identifier of the courier.
     */
    public int getId() {
        return this.id;
    }

    /**
     * Returns a string representation of the courier.
     *
     * @return A string representing the courier.
     */
    @Override
    public String toString() {
        return "Courier " + this.id;
    }
}
