package fr.insalyonif.hubert.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static fr.insalyonif.hubert.model.Dynamique.*;

public class RunDynamique {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);


        /*System.out.print("Number of vertices: ");
        int n = scanner.nextInt();

        if (n <= 0 || n > 32) {
            System.out.println("The number of vertices must be an integer value in [1,32].");
            return;
        }*/

        ArrayList<Intersection> intersections = new ArrayList<>();
        Intersection intersection0 = new Intersection(45.7597, 4.8422, 1, 9);
        Intersection intersection1 = new Intersection(45.7583, 4.8415, 2, 50);
        Intersection intersection2 = new Intersection(45.7570, 4.8400, 3, 3);
        Intersection intersection3 = new Intersection(45.7570, 4.8400, 4, 4);
        Intersection intersection4 = new Intersection(45.7570, 4.8400, 5, 7);
        intersections.add(intersection0);
        intersections.add(intersection1);
        intersections.add(intersection2);
        intersections.add(intersection3);
        intersections.add(intersection4);




// Création de la liste de Chemin
        List<Chemin> chemins = new ArrayList<>();

// Ajout de 4 chemins à la liste
        chemins.add(new Chemin(intersection0, intersection2, new int[]{1, 2, 3, 4, 5}, 2));
        chemins.add(new Chemin(intersection2, intersection0, new int[]{2, 3, 4, 5, 1}, 7)); // Coût différent
        chemins.add(new Chemin(intersection0, intersection3, new int[]{2, 3, 4, 5, 1}, 3));
        chemins.add(new Chemin(intersection3, intersection0, new int[]{4, 5, 1, 2, 3}, 6)); // Coût différent
        chemins.add(new Chemin(intersection0, intersection4, new int[]{3, 4, 5, 1, 2}, 5));
        chemins.add(new Chemin(intersection4, intersection0, new int[]{5, 1, 2, 3, 4}, 1)); // Coût différent
        chemins.add(new Chemin(intersection0, intersection1, new int[]{4, 5, 1, 2, 3}, 4));
        chemins.add(new Chemin(intersection1, intersection0, new int[]{1, 2, 3, 4, 5}, 9)); // Coût différent

        chemins.add(new Chemin(intersection2, intersection3, new int[]{5, 4, 3, 2, 1}, 1));
        chemins.add(new Chemin(intersection3, intersection2, new int[]{2, 1, 5, 4, 3}, 3)); // Coût différent
        chemins.add(new Chemin(intersection2, intersection4, new int[]{4, 3, 2, 1, 5}, 6));
        chemins.add(new Chemin(intersection4, intersection2, new int[]{1, 2, 3, 4, 5}, 8)); // Coût différent
        chemins.add(new Chemin(intersection2, intersection1, new int[]{3, 2, 1, 5, 4}, 2));
        chemins.add(new Chemin(intersection1, intersection2, new int[]{1, 5, 4, 3, 2}, 7)); // Coût différent

        chemins.add(new Chemin(intersection3, intersection4, new int[]{2, 1, 5, 4, 3}, 3));
        chemins.add(new Chemin(intersection4, intersection3, new int[]{5, 1, 2, 3, 4}, 4)); // Coût différent
        chemins.add(new Chemin(intersection3, intersection1, new int[]{1, 5, 4, 3, 2}, 7));
        chemins.add(new Chemin(intersection1, intersection3, new int[]{4, 3, 2, 1, 5}, 1)); // Coût différent

        chemins.add(new Chemin(intersection4, intersection1, new int[]{5, 1, 2, 3, 4}, 4));
        chemins.add(new Chemin(intersection1, intersection4, new int[]{3, 2, 1, 5, 4}, 5));

        // Création du graphe complet
        Graph g = new CompleteGraph(chemins,intersections);

        Dynamique dynamique = new Dynamique(g);

        int n = g.getNbVertices();
        int s = dynamique.createSet(n); // s contains all integer values ranging between 1 and n-1

        double[][] memD = new double[n][s + 1];
        for (int i = 0; i < n; i++) {
            Arrays.fill(memD[i], 0);
        }

//        long startTime = System.currentTimeMillis();

        double d = computeD(0, s, n, g, memD);
        List<Integer> optimalPath = dynamique.findOptimalPath(0, n, g, memD);
//        double d = computeD_ite(s, n, g, memD);
//        long endTime = System.currentTimeMillis();
//        float duration = (float) (endTime - startTime) / 1000;

        System.out.printf("Length of the smallest hamiltonian circuit = %f\n", d);
        System.out.printf("Optimal Hamiltonian Circuit Path: %s\n", optimalPath);
        List<Chemin> bestChemin = dynamique.bestCheminGlobal(chemins,g,optimalPath);
        System.out.println("Meilleur chemin global :");
        for (Chemin chemin : bestChemin) {
            System.out.println(chemin);
        }
    }
}

