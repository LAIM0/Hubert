package fr.insalyonif.hubert.model;

import java.util.Collection;

/**
 * Class to implement the Dijkstra's algorithm.
 */
public class Dijkstra extends AbstractDijkstra {

    private final int INFINITY = Integer.MAX_VALUE;

    /**
     * Constructor for the Dijkstra class.
     *
     * @param sizeGraph The size of the graph.
     * @param cityMap   The city map used for navigation.
     */
    public Dijkstra(int sizeGraph, CityMap cityMap) {
        super(sizeGraph, cityMap);
    }

    /**
     * Relaxes a node in the Dijkstra's relaxation algorithm.
     *
     * @param u      The source node.
     * @param v      The destination node.
     * @param weight The weight associated with the edge between u and v.
     */
    @Override
    protected void relax(Intersection u, Intersection v, double weight) {
        // Updates the distance and predecessor if a shorter path is found.
        if (distance[u.getPos()] + weight < distance[v.getPos()]) {
            distance[v.getPos()] = distance[u.getPos()] + weight;
            this.pi[v.getPos()] = u.getPos();
        }
    }

    /**
     * Builds the partial copy of the pi array when adding a delivery request.
     *
     * @param piCopy    Partially copied array.
     * @param start     The starting intersection.
     * @param delivery  The delivery intersection.
     */
    protected void piCopyConstructor(int[] piCopy, Intersection start, Intersection delivery) {
        int j = delivery.getPos();
        while (j != start.getPos()) {
            piCopy[j] = this.pi[j];
            j = this.pi[j];
        }
    }

    /**
     * Gets the neighbors of an intersection used in the Dijkstra's algorithm.
     *
     * @param intersection The intersection for which neighbors are retrieved.
     * @return A collection of neighboring road segments.
     */
    @Override
    protected Collection<RoadSegment> getNeighbors(Intersection intersection) {
        return intersection.getSuccessors();
    }

    /**
     * Selects the next node to explore during the Dijkstra's algorithm.
     *
     * @param roadSegment The road segment connecting the current node to the next node.
     * @return The destination node of the road segment.
     */
    @Override
    protected Intersection selectNode(RoadSegment roadSegment) {
        return roadSegment.getDestination();
    }

    /**
     * Creates a Chemin object representing the path between two intersections.
     *
     * @param start       The starting intersection.
     * @param destination The destination intersection.
     * @param pi          Array of predecessors representing the path.
     * @param cost        The total cost of the path.
     * @return A Chemin object representing the calculated path.
     */
    @Override
    protected Chemin createChemin(Intersection start, Intersection destination, int[] pi, double cost) {
        Chemin chemin = new Chemin(start, destination, pi, cost);
        return chemin;
    }

    /**
     * Adds a delivery intersection to the list of delivery requests.
     *
     * @param deliveryIntersection The delivery intersection to add.
     */
    public void addDeliveryRequest(Intersection deliveryIntersection) {
        deliveryRequest.add(deliveryIntersection);
    }
}
