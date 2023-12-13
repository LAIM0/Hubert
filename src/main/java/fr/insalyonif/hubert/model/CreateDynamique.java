package fr.insalyonif.hubert.model;

import java.util.*;

import static java.lang.Math.*;

public class CreateDynamique implements Dynamique {
    private double[][] mat;

    public CreateDynamique(Graph graph) {
        mat = new double[graph.getNbVertices()][graph.getNbVertices()];
    }

    private static boolean isIn(int e, int s) {
        // Précondition : 1 <= e <= 32
        // Post-relation : retourne vrai si e appartient à s
        return ((s & (1 << (e - 1))) != 0);
    }

    private static boolean isEmpty(int s) {
        // Post-relation : retourne vrai si s est vide
        return (s == 0);
    }

    private static int addElement(int s, int e) {
        // Précondition : 1 <= e <= 32
        // Post-relation : retourne l'ensemble s U {e}
        return (s | (1 << (e - 1)));
    }

    private static int removeElement(int s, int e) {
        // Précondition : 1 <= e <= 32
        // Post-relation : retourne l'ensemble s \ {e}
        return (s ^ (1 << (e - 1)));
    }

    public int createSet(int n) {
        // Précondition : 1 <= n <= 32
        // Post-relation : retourne l'ensemble contenant tous les entiers de 1 à n-1
        return (1 << (n - 1)) - 1;
    }

    public void printSet(int s) {
        // Post-condition : affiche tous les éléments de s
        int i = 1;
        while (s != 0) {
            if (s % 2 != 0) System.out.print(" " + i);
            s /= 2;
            i++;
        }
    }

    public Chemin getCheminBy(List<Chemin> chemins, int debut, int fin) {
        for (Chemin chemin : chemins) {
            if (chemin.getDebut().getPos() == debut && chemin.getFin().getPos() == fin) {
                return chemin;
            }
        }
        // Retourne null si aucun chemin correspondant n'est trouvé
        return null;
    }

    public List<Chemin> bestCheminGlobal(List<Chemin> chemins, Graph g, List<Integer> bestSol) {
        List<Chemin> bestChemin = new ArrayList<>();

        // Récupérer la HashMap associant les positions aux indices
        Map<Integer, Integer> positionToIndex = g.getPositionToIndexMap();
        System.out.println("Clés de la HashMap : " + positionToIndex.keySet());

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

    public List<Integer> adaptivePath(int start, int n, Graph g, double[][] mem) {
        List<Integer> optimalPath = new ArrayList<>();
        adaptiveCreatePath(start, createSet(n), n, g, mem, optimalPath);
        return optimalPath;
    }

    private void adaptiveCreatePath(int i, int s, int n, Graph g, double[][] mem, List<Integer> path) {
        System.out.println(path);
        if (isEmpty(s)) {
            System.out.println(i);
            path.add(0,i);
            return;
        }

        for (int j = 1; j < n; j++) {
            if (isIn(j, s)) {
                double remainingCost = round((mem[i][s] - g.getCost(i, j)) * 1e7) / 1e7;
                double futureCost = round((adaptiveDynamic(j, removeElement(s, j), n, g, mem)) * 1e7) / 1e7;
                if (abs(remainingCost - futureCost) < 0.000000001) {
                    if (i == 0){
                        System.out.println(remainingCost);
                        System.out.println(futureCost);
                    }
                    path.add(0,i);
                    adaptiveCreatePath(j, removeElement(s, j), n, g, mem, path);
                    break;
                }
            }
        }
    }

    public double adaptiveDynamic(int debut, int s, int n, Graph g, double[][] mem) {
        if (mem[debut][s] > 0) {
            return mem[debut][s];
        }

        if (isEmpty(s)) {
            mem[debut][n] = g.getCost(debut, n + 1);
            return g.getCost(debut, n + 1);
        }

        double min = Double.MAX_VALUE;
        for (int j = 1; j < n ; j++) {
            if (isIn(j, s)) {
                double d = adaptiveDynamic(j, removeElement(s, j), n, g, mem);
                if (g.getCost(debut, j) + d < min) min = g.getCost(debut, j) + d;
            }
        }

        mem[debut][s] = min;
        return min;
    }
}
