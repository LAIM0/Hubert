package fr.insalyonif.hubert.model;

import java.util.List;

/**
 * Interface representing a dynamic programming algorithm for solving a routing problem.
 */
public interface Dynamique {
    /**
     * Creates a set containing all integers from 1 to n-1.
     *
     * @param n The upper limit of the set.
     * @return The created set.
     */
    public int createSet(int n);

    /**
     * Displays all elements of the given set.
     *
     * @param s The set to display.
     */
    public void printSet(int s);

    /**
     * Gets a Chemin object from a list of paths based on the provided start and end positions.
     *
     * @param chemins The list of paths.
     * @param debut   The starting position.
     * @param fin     The ending position.
     * @return The Chemin object corresponding to the start and end positions, or null if not found.
     */
    public Chemin getCheminBy(List<Chemin> chemins, int debut, int fin);

    /**
     * Finds the best path in a list of paths based on a given solution.
     *
     * @param chemins The list of paths.
     * @param g       The graph.
     * @param bestSol The best solution.
     * @return The list of paths representing the best path.
     */
    public List<Chemin> bestCheminGlobal(List<Chemin> chemins, Graph g, List<Integer> bestSol);

    /**
     * Finds the optimal path using classic dynamic programming.
     *
     * @param start The starting position.
     * @param n     The upper limit of the set.
     * @param g     The graph.
     * @param mem   The memoization matrix.
     * @return The optimal path as a list of integers.
     */
    public List<Integer> classicPath(int start, int n, Graph g, double[][] mem);

    /**
     * Recursive function for classic dynamic programming.
     *
     * @param i   The current position.
     * @param s   The current set of remaining positions.
     * @param n   The upper limit of the set.
     * @param g   The graph.
     * @param mem The memoization matrix.
     * @return The minimum cost for the given parameters.
     */
    public double classicDynamic(int i, int s, int n, Graph g, double[][] mem);

    /**
     * Finds the optimal path using adaptive dynamic programming.
     *
     * @param start The starting position.
     * @param n     The upper limit of the set.
     * @param g     The graph.
     * @param mem   The memoization matrix.
     * @return The optimal path as a list of integers.
     */
    public List<Integer> adaptivePath(int start, int n, Graph g, double[][] mem);

    /**
     * Recursive function for adaptive dynamic programming.
     *
     * @param debut The starting position.
     * @param s     The current set of remaining positions.
     * @param n     The upper limit of the set.
     * @param g     The graph.
     * @param mem   The memoization matrix.
     * @return The minimum cost for the given parameters.
     */
    public double adaptiveDynamic(int debut, int s, int n, Graph g, double[][] mem);
}