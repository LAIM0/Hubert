package fr.insalyonif.hubert.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a complete directed graph where each edge has a weight within [MIN_COST, MAX_COST].
 * The graph is designed to model delivery routes in a city map.
 */
public class CompleteGraph implements Graph {
	private static final int MAX_COST = 40;
	private static final int MIN_COST = 10;
	int nbVertices;
	double[][] cost;

	// Mapping of intersection positions to graph indices
	public Map<Integer, Integer> positionToIndex;

	/**
	 * Creates a complete directed graph based on the given delivery routes and intersections.
	 *
	 * @param chemins       List of paths representing delivery routes.
	 * @param intersections List of delivery requests (intersections).
	 * @param cityMap       The city map containing intersections and routes.
	 */
	public CompleteGraph(List<Chemin> chemins, ArrayList<DeliveryRequest> intersections, CityMap cityMap) {
		this.nbVertices = intersections.size() + 1;
		cost = new double[nbVertices][nbVertices];

		// Initialize the cost matrix with positive infinity values
		for (int i = 0; i < nbVertices; i++) {
			for (int j = 0; j < nbVertices; j++) {
				cost[i][j] = Double.POSITIVE_INFINITY;
			}
		}

		// Initialize the mapping of intersection positions to graph indices
		positionToIndex = new HashMap<>();
		positionToIndex.put(cityMap.getWareHouseLocation().getPos(), 0);

		for (int i = 1; i < nbVertices; i++) {
			positionToIndex.put(intersections.get(i - 1).getDeliveryLocation().getPos(), i);
		}

		// Fill the cost matrix with correct values
		for (Chemin chemin : chemins) {
			if ((positionToIndex.get(chemin.getDebut().getPos()) != null) &&
					(positionToIndex.get(chemin.getFin().getPos()) != null)) {
				int debutIndex = positionToIndex.get(chemin.getDebut().getPos());
				int finIndex = positionToIndex.get(chemin.getFin().getPos());
				double cout = chemin.getCout();

				cost[debutIndex][finIndex] = cout;
			}
		}

		// Display the cost matrix
		//System.out.println("Cost Matrix:");
//		for (int i = 0; i < cost.length; i++) {
//			for (int j = 0; j < cost[i].length; j++) {
//				System.out.print(cost[i][j] + "\t");
//			}
//			System.out.println(); // Move to the next line after each row of the matrix
//		}
	}

	/**
	 * Gets the number of vertices in the graph.
	 *
	 * @return The number of vertices.
	 */
	@Override
	public int getNbVertices() {
		return nbVertices;
	}

	/**
	 * Gets the cost of the edge between vertices i and j in the graph.
	 *
	 * @param i The index of the starting vertex.
	 * @param j The index of the ending vertex.
	 * @return The cost of the edge between vertices i and j.
	 */
	@Override
	public double getCost(int i, int j) {
		if (i < 0 || i >= nbVertices || j < 0 || j >= nbVertices)
			return -1;
		return cost[i][j];
	}

	/**
	 * Checks if there is a directed arc between vertices i and j in the graph.
	 *
	 * @param i The index of the starting vertex.
	 * @param j The index of the ending vertex.
	 * @return true if there is an arc, otherwise false.
	 */
	@Override
	public boolean isArc(int i, int j) {
		if (i < 0 || i >= nbVertices || j < 0 || j >= nbVertices)
			return false;
		return i != j;
	}

	/**
	 * Gets the mapping of intersection positions to graph indices.
	 *
	 * @return The mapping of intersection positions to graph indices.
	 */
	public Map<Integer, Integer> getPositionToIndexMap() {
		return positionToIndex;
	}
}