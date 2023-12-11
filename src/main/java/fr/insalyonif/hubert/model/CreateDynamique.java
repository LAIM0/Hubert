package fr.insalyonif.hubert.model;

import java.util.ArrayList;
import java.util.List;
import java.util.*;


import static java.lang.Math.abs;
import static java.lang.Math.round;

/**
 * Implementation of the Dynamique interface for solving the Traveling Salesman Problem using dynamic programming.
 */
public class CreateDynamique implements Dynamique {
    private double[][] mat;

    /**
     * Constructor for CreateDynamique class.
     *
     * @param graph The graph for which the dynamic programming is applied.
     */
    public CreateDynamique(Graph graph) {
        mat = new double[graph.getNbVertices()][graph.getNbVertices()];
    }

    private static boolean isIn(int e, int s) {
        return ((s & (1 << (e - 1))) != 0);
    }

    private static boolean isEmpty(int s) {
        return (s == 0);
    }

    private static int addElement(int s, int e) {
        return (s | (1 << (e - 1)));
    }

    private static int removeElement(int s, int e) {
        return (s ^ (1 << (e - 1)));
    }

    /**
     * Creates a set containing all integers from 1 to n-1.
     *
     * @param n The upper limit for the set.
     * @return The created set.
     */
    public int createSet(int n) {
        return (1 << (n - 1)) - 1;
    }

    /**
     * Prints the elements of a set.
     *
     * @param s The set to be printed.
     */
    public void printSet(int s) {
        int i = 1;
        while (s != 0) {
            if (s % 2 != 0) System.out.print(" " + i);
            s /= 2;
            i++;
        }
    }

    /**
     * Finds the Chemin object in a list based on start and end positions.
     *
     * @param chemins The list of Chemin objects.
     * @param debut   The starting position.
     * @param fin     The ending position.
     * @return The Chemin object corresponding to the start and end positions, or null if not found.
     */
    public Chemin getCheminBy(List<Chemin> chemins, int debut, int fin) {
        for (Chemin chemin : chemins) {
            if (chemin.getDebut().getPos() == debut && chemin.getFin().getPos() == fin) {
                return chemin;
            }
        }
        return null;
    }

    /**
     * Finds the best set of Chemin objects to represent the optimal path based on a solution.
     *
     * @param chemins The list of all possible Chemin objects.
     * @param g       The graph.
     * @param bestSol The solution representing the optimal path.
     * @return The list of Chemin objects representing the best path.
     */
    public List<Chemin> bestCheminGlobal(List<Chemin> chemins, Graph g, List<Integer> bestSol) {
        List<Chemin> bestChemin = new ArrayList<>();

        // Récupérer la HashMap associant les positions aux indices
        Map<Integer, Integer> positionToIndex = g.getPositionToIndexMap();
        //System.out.println("Clés de la HashMap : " + positionToIndex.keySet());

        for (int i = 0; i < bestSol.size() - 1; i++) {
            int debutPosition = bestSol.get(i);
            int finPosition = bestSol.get(i + 1);
            Integer cleTrouvee = null;
            Integer cleTrouvee2 = null;
            for (Map.Entry<Integer, Integer> entry : positionToIndex.entrySet()) {
                if (entry.getValue().equals(debutPosition)) {
                    cleTrouvee = entry.getKey();
                    // On a trouvé la clé, on peut sortir de la boucle
                } else if (entry.getValue().equals(finPosition)) {
                    cleTrouvee2 = entry.getKey();
                    // On a trouvé la clé, on peut sortir de la boucle
                }
                if (cleTrouvee != null && cleTrouvee2 != null) break;
            }

            // Utiliser la HashMap pour obtenir l'indice associé à la position dans bestSol
            Integer debutIndex = cleTrouvee;
            Integer finIndex = cleTrouvee2;

            Chemin chemin = getCheminBy(chemins, debutIndex, finIndex);
            bestChemin.add(chemin);

        }

        return bestChemin;
    }

    /**
     * Finds the optimal path using classic dynamic programming approach.
     *
     * @param start The starting point.
     * @param n     The upper limit for the set.
     * @param g     The graph.
     * @param mem   The memory array for storing intermediate results.
     * @return The list of indices representing the optimal path.
     */
    public List<Integer> classicPath(int start, int n, Graph g, double[][] mem) {
        List<Integer> optimalPath = new ArrayList<>();
        classicCreatePath(start, createSet(n), n, g, mem, optimalPath);
        return optimalPath;
    }

    private void classicCreatePath(int i, int s, int n, Graph g, double[][] mem, List<Integer> path) {
        if (isEmpty(s)) {
            path.add(0, i);
            return;
        }

        for (int j = 1; j < n; j++) {
            if (isIn(j, s)) {
                double remainingCost = round((mem[i][s] - g.getCost(i, j)) * 1e7) / 1e7;
                double futureCost = round((classicDynamic(j, removeElement(s, j), n, g, mem)) * 1e7) / 1e7;
                if (abs(remainingCost - futureCost) < 0.000000001) {
                    path.add(0, i);
                    classicCreatePath(j, removeElement(s, j), n, g, mem, path);
                    break;
                }
            }
        }
    }

    /**
     * Computes the minimum cost using classic dynamic programming approach.
     *
     * @param i   The current index.
     * @param s   The set of remaining indices.
     * @param n   The upper limit for the set.
     * @param g   The graph.
     * @param mem The memory array for storing intermediate results.
     * @return The minimum cost.
     */
    public double classicDynamic(int i, int s, int n, Graph g, double[][] mem) {
        if (mem[i][s] > 0) {
            return mem[i][s];
        }

        if (isEmpty(s)) {
            mem[i][0] = g.getCost(i, 0);
            return g.getCost(i, 0);
        }

        double min = Double.MAX_VALUE;
        for (int j = 1; j < n; j++) {
            if (isIn(j, s)) {
                double d = classicDynamic(j, removeElement(s, j), n, g, mem);
                if (g.getCost(i, j) + d < min) min = g.getCost(i, j) + d;
            }
        }

        mem[i][s] = min;
        return min;
    }

    /**
     * Finds the optimal path using adaptive dynamic programming approach.
     *
     * @param start The starting point.
     * @param n     The upper limit for the set.
     * @param g     The graph.
     * @param mem   The memory array for storing intermediate results.
     * @return The list of indices representing the optimal path.
     */
    public List<Integer> adaptivePath(int start, int n, Graph g, double[][] mem) {
        List<Integer> optimalPath = new ArrayList<>();
        adaptiveCreatePath(start, createSet(n), n, g, mem, optimalPath);
        return optimalPath;
    }

    private void adaptiveCreatePath(int i, int s, int n, Graph g, double[][] mem, List<Integer> path) {
        if (isEmpty(s)) {
            path.add(i);
            return;
        }

        for (int j = 1; j < n; j++) {
            if (isIn(j, s)) {
                double remainingCost = round((mem[i][s] - g.getCost(i, j)) * 1e7) / 1e7;
                double futureCost = round((adaptiveDynamic(j, removeElement(s, j), n, g, mem)) * 1e7) / 1e7;
                if (abs(remainingCost - futureCost) < 0.000000001) {
                    path.add(0, i);
                    classicCreatePath(j, removeElement(s, j), n, g, mem, path);
                    break;
                }
            }
        }
    }

    /**
     * Computes the minimum cost using adaptive dynamic programming approach.
     *
     * @param debut The starting index.
     * @param s     The set of remaining indices.
     * @param n     The upper limit for the set.
     * @param g     The graph.
     * @param mem   The memory array for storing intermediate results.
     * @return The minimum cost.
     */
    public double adaptiveDynamic(int debut, int s, int n, Graph g, double[][] mem) {
        if (mem[debut][s] > 0) {
            return mem[debut][s];
        }

        if (isEmpty(s)) {
            mem[debut][n + 1] = g.getCost(debut, n + 1);
            return g.getCost(debut, n + 1);
        }

        double min = Double.MAX_VALUE;
        for (int j = 1; j < n - 1; j++) {
            if (isIn(j, s)) {
                double d = classicDynamic(j, removeElement(s, j), n, g, mem);
                if (g.getCost(debut, j) + d < min) min = g.getCost(debut, j) + d;
            }
        }

        mem[debut][s] = min;
        return min;
    }
}
