package fr.insalyonif.hubert.model;

import java.util.*;

public class Dynamique {
    private double[][] mat;

    public Dynamique(Graph graph){
        mat = new double[graph.getNbVertices()][graph.getNbVertices()];

        /*for(Chemin iterator : chemins) {
            mat[iterator.getDebut().getPos()][iterator.getFin().getPos()] = iterator.getCout();
        }*/
    }


    private static int iseed = 1; // Seed used for initializing the pseudo-random number generator

    private static boolean isIn(int e, int s) {
        // Precondition: 1 <= e <= 32
        // Postrelation: return true if e belongs to s
        return ((s & (1 << (e - 1))) != 0);
    }

    private static boolean isEmpty(int s) {
        // Postrelation: return true if s is empty
        return (s == 0);
    }

    private static int addElement(int s, int e) {
        // Precondition: 1 <= e <= 32
        // Postrelation: return the set s U {e}
        return (s | (1 << (e - 1)));
    }

    private static int removeElement(int s, int e) {
        // Precondition: 1 <= e <= 32
        // Postrelation: return the set s \ {e}
        return (s ^ (1 << (e - 1)));
    }

    public static int createSet(int n) {
        // Precondition: 1 <= n <= 32
        // Postrelation: return the set that contains all integers ranging from 1 to n-1
        return (1 << (n - 1)) - 1;
    }

    public static void printSet(int s) {
        // Postcondition: print all elements of s
        int i = 1;
        while (s != 0) {
            if (s % 2 != 0) System.out.print(" " + i);
            s /= 2;
            i++;
        }
    }

    /*private static int nextRand(int n) {
        // Postcondition: return an integer value in [0,n-1], according to a pseudo-random sequence
        int i = 16807 * (iseed % 127773) - 2836 * (iseed / 127773);
        if (i > 0) iseed = i;
        else iseed = 2147483647 + i;
        return iseed % n;
    }*/

    /*public static int[][] createCost(int n) {
        // return a symmetrical cost matrix such that, for each i,j in [0,n-1], cost[i][j] = cost of arc (i,j)
        int[] x = new int[n];
        int[] y = new int[n];
        int max = 1000;
        int[][] cost = new int[n][n];

        for (int i = 0; i < n; i++) {
            x[i] = nextRand(max);
            y[i] = nextRand(max);
        }

        for (int i = 0; i < n; i++) {
            cost[i][i] = max * max;
            for (int j = i + 1; j < n; j++) {
                cost[i][j] = (int) Math.sqrt((x[i] - x[j]) * (x[i] - x[j]) + (y[i] - y[j]) * (y[i] - y[j]));
                cost[j][i] = cost[i][j];
            }
        }
        return cost;
    }*/

/*    private static int computeD(int i, int s, int n, int[][] cost, int[][] mem) {
        // Preconditions: isIn(i,s) = false and isIn(0,s) = false
        // Postrelation: return the cost of the smallest path that starts from i, visits each vertex of s exactly once, and ends on 0
        if (mem[i][s] > 0) {
            return mem[i][s];
        }

        if (isEmpty(s)) {
            mem[i][0] = cost[i][0];
            return cost[i][0];
        }

        int min = Integer.MAX_VALUE;
        for (int j = 1; j < n; j++) {
            if (isIn(j, s)) {
                int d = computeD(j, removeElement(s, j), n, cost, mem);
                if (cost[i][j] + d < min) min = cost[i][j] + d;
            }
        }

        mem[i][s] = min;
        return min;
    }*/
    public Chemin getCheminBy(List<Chemin> chemins, int debut, int fin) {
        for (Chemin chemin : chemins) {
            if (chemin.getDebut().getPos() == debut && chemin.getFin().getPos() == fin) {
                return chemin;
            }
        }
        // Retourner null si aucun chemin correspondant n'est trouvé
        return null;
    }
    public List<Chemin> bestCheminGlobal(List<Chemin> chemins, Graph g, List<Integer> bestSol) {
        List<Chemin> bestChemin = new ArrayList<>();

        // Récupérer la HashMap associant les positions aux indices
        Map<Integer, Integer> positionToIndex = g.getPositionToIndexMap();
        System.out.println("Clés de la HashMap : " + positionToIndex.keySet());

        for (int i = 0; i < bestSol.size() - 1; i++) {
            int debutPosition = bestSol.get(i);
            int finPosition = bestSol.get(i+1);
            Integer cleTrouvee = null;
            Integer cleTrouvee2 = null;
            for (Map.Entry<Integer, Integer> entry : positionToIndex.entrySet()) {
                if (entry.getValue().equals(debutPosition)) {
                    cleTrouvee = entry.getKey();
                    // On a trouvé la clé, on peut sortir de la boucle
                }else if (entry.getValue().equals(finPosition)) {
                    cleTrouvee2 = entry.getKey();
                    // On a trouvé la clé, on peut sortir de la boucle
                }
                if(cleTrouvee!=null && cleTrouvee2!=null)break;
            }

            // Utiliser la HashMap pour obtenir l'indice associé à la position dans bestSol
            Integer debutIndex = cleTrouvee;
            Integer finIndex = cleTrouvee2;

            Chemin chemin = getCheminBy(chemins, debutIndex, finIndex);
            bestChemin.add(chemin);

        }

        return bestChemin;
    }
    public List<Integer> findOptimalPath(int start, int n, Graph g, double[][] mem) {
        List<Integer> optimalPath = new ArrayList<>();
        reconstructPath(start, createSet(n), n, g, mem, optimalPath);
        optimalPath.add(0,start);
        return optimalPath;
    }

    private void reconstructPath(int i, int s, int n, Graph g, double[][] mem, List<Integer> path) {
        if (isEmpty(s)) {
            path.add(0, i);
            return;
        }

        for (int j = 1; j < n; j++) {
            if (isIn(j, s)) {
                double remainingCost = mem[i][s] - g.getCost(i, j);
                if (Double.compare(remainingCost, computeD(j, removeElement(s, j), n, g, mem)) == 0) {
                    path.add(0, i);
                    reconstructPath(j, removeElement(s, j), n, g, mem, path);
                    break;
                }
            }
        }
    }

    public static double computeD(int i, int s, int n, Graph g, double[][] mem) {
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
                double d = computeD(j, removeElement(s, j), n, g, mem);
                if (g.getCost(i, j) + d < min) min = g.getCost(i, j) + d;
            }
        }

        mem[i][s] = min;
        return min;
    }



    /*public static double computeD_ite(int s, int n, Graph g, double[][] mem) {
        for (int e = 0; e <= s; e++) {
            for (int i = 0; i < n; i++) {
                if (e == 0) {
                    mem[i][0] = g.getCost(i,0);
                } else {
                    mem[i][e] = Double.MAX_VALUE;
                    for (int j = 1; j < n; j++) {
                    printSet(e);
                    System.out.println("," + i + " , " + j);
                        if (isIn(j, e)) {
                            if (g.getCost(i,j) + mem[j][e - j] < mem[i][e]) {
                                mem[i][e] = g.getCost(i,j) + mem[j][e - j];
                            }
                        }
                    }
                }


            }
        }
        for (int i = 0; i < 16 ; i++){
            printSet(i);
            System.out.println(" : " + mem[0][i]);
        }
        return mem[0][createSet(n)];
    }*/






}
