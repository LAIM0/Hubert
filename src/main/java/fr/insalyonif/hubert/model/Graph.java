package fr.insalyonif.hubert.model;

import java.util.Map;

/**
 * Interface representing a graph for a routing problem.
 */
public interface Graph {
	/**
	 * Gets the number of vertices in this graph.
	 *
	 * @return The number of vertices.
	 */
	public abstract int getNbVertices();

	/**
	 * Gets the cost of the edge (i, j) if (i, j) is an edge; -1 otherwise.
	 *
	 * @param i The source vertex.
	 * @param j The destination vertex.
	 * @return The cost of the edge (i, j) if (i, j) is an edge; -1 otherwise.
	 */
	public abstract double getCost(int i, int j);

	/**
	 * Checks if (i, j) is an edge in this graph.
	 *
	 * @param i The source vertex.
	 * @param j The destination vertex.
	 * @return True if (i, j) is an edge in this graph.
	 */
	public abstract boolean isArc(int i, int j);

	/**
	 * Gets a Map of positions to corresponding indices in the graph.
	 *
	 * @return A Map of positions to corresponding indices.
	 */
	public abstract Map<Integer, Integer> getPositionToIndexMap();
}