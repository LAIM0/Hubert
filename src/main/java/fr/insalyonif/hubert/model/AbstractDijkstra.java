package fr.insalyonif.hubert.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Abstract class to implement the Dijkstra algorithm.
 */
public abstract class AbstractDijkstra {
    private final int INFINITY = Integer.MAX_VALUE;

    public double[] distance;
    public int[] pi;
    public boolean[] visited;

    public String[] colors;

    private ArrayList<Chemin> chemins;

    public ArrayList<Intersection> deliveryRequest;

    protected CityMap cityMap;

    /**
     * Constructor for the AbstractDijkstra class.
     *
     * @param sizeGraph The size of the graph.
     * @param cityMap   The city map.
     */
    public AbstractDijkstra(int sizeGraph, CityMap cityMap) {
        this.distance = new double[sizeGraph];
        this.pi = new int[sizeGraph];
        this.visited = new boolean[sizeGraph];
        this.cityMap = cityMap;
        this.chemins = new ArrayList<Chemin>();
        deliveryRequest = new ArrayList<Intersection>();
        deliveryRequest.add(cityMap.getWareHouseLocation());

        colors = new String[sizeGraph];
        Arrays.fill(colors, "white");
    }

    public void cleanDij(){
        chemins.clear();
    }

    /**
     * Calcule la distance euclidienne entre deux intersections.
     *
     * @param a Intersection A.
     * @param b Intersection B.
     * @return La distance euclidienne entre les deux intersections.
     */
    protected double calculateEuclideanDistance(Intersection a, Intersection b) {
        double deltaX = a.getLatitude() - b.getLatitude();
        double deltaY = a.getLongitude() - b.getLongitude();
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    /**
     * Fonction heuristique pour l'algorithme A*.
     *
     * @param current L'intersection actuelle.
     * @param goal    L'intersection objectif.
     * @return La valeur heuristique entre l'intersection actuelle et l'objectif.
     */
    protected double heuristic(Intersection current, Intersection goal) {
        return calculateEuclideanDistance(current, goal);
    }

    /**
     * Checks if there is a gray node in the graph.
     *
     * @return true if there is a gray node, otherwise false.
     */
    protected boolean hasGrayNode() {
        for (String color : colors) {
            if (color.equals("gray")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Finds the gray node with the smallest distance in the graph.
     *
     * @return The intersection corresponding to the gray node with the smallest distance.
     */
    protected Intersection minGrayNode() {
        double min = INFINITY;
        Intersection minNode = null;

        for (int i = 0; i < colors.length; i++) {
            if (colors[i].equals("gray") && distance[i] < min) {
                min = distance[i];
                minNode = cityMap.findIntersectionByPos(i);
            }
        }

        return minNode;
    }

    /**
     * Executes the Dijkstra algorithm to find the shortest paths.
     *
     * @param start     The starting intersection.
     * @param sizeGraph The size of the graph.
     * @return true if the starting point can reach all delivery points, otherwise false.
     */
    public boolean runDijkstra(Intersection start, int sizeGraph) {
        if (!deliveryRequest.contains(start)) {
            deliveryRequest.add(start);
        }

        for (int i = 0; i < sizeGraph; i++) {
            distance[i] = INFINITY;
            this.pi[i] = -1;
            colors[i] = "white";
        }

        distance[start.getPos()] = 0.0;
        colors[start.getPos()] = "gray";

        while (hasGrayNode()) {
            Intersection u = minGrayNode();

            for (RoadSegment roadSegment : getNeighbors(u)) {
                Intersection v = selectNode(roadSegment);

                if (colors[v.getPos()].equals("white") || colors[v.getPos()].equals("gray")) {
                    relax(u, v, roadSegment.getLength());
                    colors[v.getPos()] = "gray";
                }
            }
            colors[u.getPos()] = "black";
        }

        boolean canReachAllDeliveryPoints = true;
        for (Intersection deliveryPoint : deliveryRequest) {
            if (distance[deliveryPoint.getPos()] == INFINITY) {
                canReachAllDeliveryPoints = false;
                break;
            }
        }

        if (!canReachAllDeliveryPoints) {

            return false;
        }

        for (Intersection deliveryRequest : deliveryRequest) {
            if (deliveryRequest != start) {
                int[] piCopy = new int[sizeGraph];
                Arrays.fill(piCopy, -1);
                if (pi[deliveryRequest.getPos()] == -1) {
                    return false;
                }
                piCopyConstructor(piCopy, start, deliveryRequest);
                Chemin chemin = createChemin(start, deliveryRequest, piCopy, distance[deliveryRequest.getPos()]);
                chemins.add(chemin);
            }
        }
        return true;
    }

    public void cantReachAllDeliveryPoints(Intersection start){
        deliveryRequest.remove(start);
        chemins.removeIf(chemin -> chemin.getDebut().equals(start));
        chemins.removeIf(chemin -> chemin.getFin().equals(start));
    }

    protected abstract Iterable<RoadSegment> getNeighbors(Intersection intersection);

    protected abstract Intersection selectNode(RoadSegment roadSegment);

    protected abstract Chemin createChemin(Intersection start, Intersection destination, int[] pi, double cout);

    protected abstract void relax(Intersection u, Intersection v, double weight);

    /**
     * Copies the predecessors of the path for a specific delivery request.
     *
     * @param piCopy    The array of predecessors to copy.
     * @param start     The starting intersection.
     * @param delivery  The destination intersection (delivery request).
     */
    protected abstract void piCopyConstructor(int[] piCopy, Intersection start, Intersection delivery);

    /**
     * Returns the list of paths calculated by the Dijkstra algorithm.
     *
     * @return A list of paths.
     */
    public ArrayList<Chemin> getChemins() {
        return chemins;
    }

    /**
     * Returns the list of intersections that are the subject of delivery requests.
     *
     * @return A list of intersections representing delivery requests.
     */
    public List<Intersection> getDeliveryRequest() {
        return deliveryRequest;
    }
}