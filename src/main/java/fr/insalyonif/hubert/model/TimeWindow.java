package fr.insalyonif.hubert.model;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Represents a time window defined by a start instant and an end instant.
 * This class is useful for managing time-defined periods.
 */
public class TimeWindow {
    private int startTime;
    private int endTime;

    /**
     * Constructor to create a new time window.
     *
     * @param startTime The start instant of the time window.
     * @param endTime   The end instant of the time window.
     */
    public TimeWindow(int startTime, int endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Returns the start instant of the time window.
     *
     * @return The start instant.
     */
    public int getStartTime() {
        return startTime;
    }

    /**
     * Sets the start instant of the time window.
     *
     * @param startTime The new start instant.
     */
    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    /**
     * Returns the end instant of the time window.
     *
     * @return The end instant.
     */
    public int getEndTime() {
        return endTime;
    }

    /**
     * Sets the end instant of the time window.
     *
     * @param endTime The new end instant.
     */
    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    /**
     * Returns a string representation of the time window.
     *
     * @return The textual representation of the time window.
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH").withZone(ZoneId.systemDefault());


        // Retourner la représentation en chaîne de la fenêtre temporelle avec la mention spécifique
        return "Passage entre " + startTime + "h et " + endTime+"h";
    }

    /**
     * Checks if a specific instant is inside the time window.
     *
     * @param time The instant to check.
     * @return True if the instant is inside the time window, false otherwise.
     */
    public boolean isInTimeWindow(int time) {
        return (time < endTime && time >= startTime);
    }
}
