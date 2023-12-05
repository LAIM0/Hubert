package fr.insalyonif.hubert.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import  fr.insalyonif.hubert.model.*;

public class CompleteGraph implements Graph {
	private static final int MAX_COST = 40;
	private static final int MIN_COST = 10;
	int nbVertices;
	double[][] cost;

	public Map<Integer, Integer> positionToIndex;

	/**
	 * Create a complete directed graph such that each edge has a weight within [MIN_COST,MAX_COST]
	 * @param chemins
	 */
	public CompleteGraph(List<Chemin> chemins, ArrayList<DeliveryRequest> intersections,CityMap cityMap){
		this.nbVertices = intersections.size()+1 ;
		System.out.println("nb vertice "+ nbVertices);

		cost = new double[nbVertices][nbVertices];
		for (int i = 0; i < nbVertices; i++) {
			for (int j = 0; j < nbVertices; j++) {
				cost[i][j] = Double.POSITIVE_INFINITY;
			}
		}
		System.out.println("Cost size "+ cost[0].length);

		System.out.println("chemins size "+ chemins.size());


		positionToIndex = new HashMap<>();
		positionToIndex.put(cityMap.getWareHouseLocation().getPos(),0);
		for (int i = 1; i < nbVertices; i++) {
			positionToIndex.put(intersections.get(i-1).getDeliveryLocation().getPos(), i);
		}

		for (Map.Entry<Integer, Integer> entry : positionToIndex.entrySet()) {
				System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());

		}
		// Remplir la matrice de coût avec les valeurs correctes
		for (Chemin chemin : chemins) {
			System.out.println(chemin);
			System.out.println(chemin.getDebut().getPos());
			System.out.println(positionToIndex.get(chemin.getFin().getPos()));
			int debutIndex = positionToIndex.get(chemin.getDebut().getPos());
			int finIndex = positionToIndex.get(chemin.getFin().getPos());
			double cout = chemin.getCout();

			cost[finIndex][debutIndex] = cout;

		}

		//System.out.println("Clés de la HashMap : " + positionToIndex.keySet());


			System.out.println("Tableau des coûts :");
			for (int i = 0; i < cost.length; i++) {
				for (int j = 0; j < cost[i].length; j++) {
					System.out.print(cost[i][j] + "\t");
				}
				System.out.println(); // Passer à la ligne après chaque ligne du tableau
			}

	}

	@Override
	public int getNbVertices() {
		return nbVertices;
	}

	@Override
	public double getCost(int i, int j) {
		if (i<0 || i>=nbVertices || j<0 || j>=nbVertices)
			return -1;
		return cost[i][j];
	}

	@Override
	public boolean isArc(int i, int j) {
		if (i<0 || i>=nbVertices || j<0 || j>=nbVertices)
			return false;
		return i != j;
	}

	public Map<Integer, Integer> getPositionToIndexMap() {
		return positionToIndex;
	}

}
