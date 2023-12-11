package fr.insalyonif.hubert.model;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Represents a delivery request characterized by a delivery location and a time window.
 * This class is used to manage information related to a specific delivery request.
 */
public class DeliveryRequest {
    private Intersection deliveryLocation;
    private TimeWindow timeWindow;
    private Instant deliveryTime;

    private boolean goOff = false;

    /**
     * Checks if there is a slight delay for this delivery request.
     *
     * @return true if there is a slight delay, false otherwise.
     */
    public boolean isGoOff() {
        return goOff;
    }

    /**
     * Sets whether there is a slight delay for this delivery request.
     *
     * @param goOff true if there is a slight delay, false otherwise.
     */
    public void setGoOff(boolean goOff) {
        this.goOff = goOff;
    }

    /**
     * Constructor to create a new delivery request.
     *
     * @param deliveryLocation The intersection representing the delivery location.
     * @param timeWindow       The time window during which the delivery should be made.
     */
    public DeliveryRequest(Intersection deliveryLocation, TimeWindow timeWindow) {
        this.deliveryLocation = deliveryLocation;
        this.timeWindow = timeWindow;
    }

    /**
     * Constructor to create a new delivery request without a time window.
     *
     * @param deliveryLocation The intersection representing the delivery location.
     */
    public DeliveryRequest(Intersection deliveryLocation) {
        this.deliveryLocation = deliveryLocation;
        this.timeWindow = new TimeWindow(0, 0);
    }

    /**
     * Gets the delivery location of the request.
     *
     * @return The intersection representing the delivery location.
     */
    public Intersection getDeliveryLocation() {
        return deliveryLocation;
    }

    /**
     * Sets the delivery location of the request.
     *
     * @param deliveryLocation The new delivery location.
     */
    public void setDeliveryLocation(Intersection deliveryLocation) {
        this.deliveryLocation = deliveryLocation;
    }

    /**
     * Gets the time window associated with the delivery request.
     *
     * @return The time window.
     */
    public TimeWindow getTimeWindow() {
        return timeWindow;
    }

    /**
     * Sets the time window associated with the delivery request.
     *
     * @param timeWindow The new time window.
     */
    public void setTimeWindow(TimeWindow timeWindow) {
        this.timeWindow = timeWindow;
    }

    /**
     * Gets the delivery time.
     *
     * @return The delivery time.
     */
    public Instant getDeliveryTime() {
        return this.deliveryTime;
    }

    /**
     * Sets the delivery time.
     *
     * @param time The new delivery time.
     */
    public void setDeliveryTime(Instant time) {
        this.deliveryTime = time;
    }

    /**
     * Gets a string representation of the delivery request.
     *
     * @return The textual representation of the delivery request.
     */
    @Override
    public String toString() {
        String deliveryTimeString = deliveryTime != null ? DateTimeFormatter.ofPattern("HH:mm").format(deliveryTime.atZone(ZoneId.of("UTC"))) : "Not specified";
        String timeWindowString = timeWindow != null ? timeWindow.toString() : "Not specified";
        if (goOff == false) {
            return "Delivery at intersection: " + deliveryLocation.getId() +
                    "\nScheduled for: " + deliveryTimeString +
                    "\n" + timeWindowString;
        } else {
            return "Delivery at intersection: " + deliveryLocation.getId() +
                    "\nScheduled for: " + deliveryTimeString +
                    "\n" + timeWindowString +
                    "\nWARNING: Slight delay!";
        }
    }
}