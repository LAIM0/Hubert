package fr.insalyonif.hubert.model;

import java.util.ArrayList;

/**
 * Class to implement the Inverse Dijkstra's algorithm.
 */
public class DijkstraInverse extends AbstractDijkstra {

    /**
     * Constructor for the DijkstraInverse class.
     *
     * @param sizeGraph The size of the graph.
     * @param cityMap   The city map used for navigation.
     */
    public DijkstraInverse(int sizeGraph, CityMap cityMap) {
        super(sizeGraph, cityMap);
    }

    /**
     * Relaxes a node in the Inverse Dijkstra's relaxation algorithm.
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
        ArrayList<Integer> path = new ArrayList<Integer>();
        path.add(j);
        while (j != start.getPos()) {
            path.add(this.pi[j]);
            j = this.pi[j];
        }
        for (int i = path.size() - 1; i > 0; i--) {
            piCopy[path.get(i)] = path.get(i - 1);
        }
    }

    /**
     * Gets the neighbors of an intersection used in the Inverse Dijkstra's algorithm.
     *
     * @param intersection The intersection for which neighbors are retrieved.
     * @return A collection of neighboring road segments.
     */
    @Override
    protected Iterable<RoadSegment> getNeighbors(Intersection intersection) {
        return intersection.getPredecessors();
    }

    /**
     * Selects the next node to explore during the Inverse Dijkstra's algorithm.
     *
     * @param roadSegment The road segment connecting the current node to the next node.
     * @return The origin node of the road segment.
     */
    @Override
    protected Intersection selectNode(RoadSegment roadSegment) {
        return roadSegment.getOrigin();
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
        Chemin chemin = new Chemin(destination, start, pi, cost);
        return chemin;
    }
}