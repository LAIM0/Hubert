package fr.insalyonif.hubert.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public abstract class TemplateTSP implements TSP {
	private Integer[] bestSol;
	protected Graph g;
	private double bestSolCost;
	private int timeLimit;
	private long startTime;

	public void searchSolution(int timeLimit, Graph g){
		if (timeLimit <= 0) return;
		startTime = System.currentTimeMillis();
		this.timeLimit = timeLimit;
		this.g = g;
		bestSol = new Integer[g.getNbVertices()+1];
		Collection<Integer> unvisited = new ArrayList<Integer>(g.getNbVertices()-1);
		for (int i=1; i<g.getNbVertices(); i++) unvisited.add(i);
		Collection<Integer> visited = new ArrayList<Integer>(g.getNbVertices());
		visited.add(0); // The first visited vertex is 0
		bestSolCost = Integer.MAX_VALUE;
		branchAndBound(0, unvisited, visited, 0);
	}

	/*public List<Chemin> bestCheminGlobal(List<Chemin> chemins){
		List<Chemin> bestChemin = new ArrayList<>();
		for(int i=0;i<bestSol.length-1;i++){
			Chemin chemin = getCheminBy(chemins,bestSol[i],bestSol[i+1]);
			bestChemin.add(chemin);
		}

		bestChemin.add(getCheminBy(chemins,bestSol[bestSol.length-1],0));
		return bestChemin;

	}*/

	public List<Chemin> bestCheminGlobal(List<Chemin> chemins) {
		List<Chemin> bestChemin = new ArrayList<>();
		bestSol[g.getNbVertices()]=0;
		// Récupérer la HashMap associant les positions aux indices
		Map<Integer, Integer> positionToIndex = g.getPositionToIndexMap();
		System.out.println("Clés de la HashMap : " + positionToIndex.keySet());

		for (int i = 0; i < bestSol.length - 1; i++) {
			int debutPosition = bestSol[i];
			int finPosition = bestSol[i+1];
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



	public Chemin getCheminBy(List<Chemin> chemins, int debut, int fin) {
		for (Chemin chemin : chemins) {
			if (chemin.getDebut().getPos() == debut && chemin.getFin().getPos() == fin) {
				return chemin;
			}
		}
		// Retourner null si aucun chemin correspondant n'est trouvé
		return null;
	}

	public Integer getSolution(int i){
		if (g != null && i>=0 && i<g.getNbVertices())
			return bestSol[i];
		return -1;
	}

	public double getSolutionCost(){
		if (g != null)
			return bestSolCost;
		return -1;
	}

	/**
	 * Method that must be defined in TemplateTSP subclasses
	 * @param currentVertex
	 * @param unvisited
	 * @return a lower bound of the cost of paths in <code>g</code> starting from <code>currentVertex</code>, visiting
	 * every vertex in <code>unvisited</code> exactly once, and returning back to vertex <code>0</code>.
	 */
	protected abstract int bound(Integer currentVertex, Collection<Integer> unvisited);

	/**
	 * Method that must be defined in TemplateTSP subclasses
	 * @param currentVertex
	 * @param unvisited
	 * @param g
	 * @return an iterator for visiting all vertices in <code>unvisited</code> which are successors of <code>currentVertex</code>
	 */

	public abstract Iterator<Integer> iterator(Integer currentVertex, Collection<Integer> unvisited, Graph g);



	/**
	 * Template method of a branch and bound algorithm for solving the TSP in <code>g</code>.
	 * @param currentVertex the last visited vertex
	 * @param unvisited the set of vertex that have not yet been visited
	 * @param visited the sequence of vertices that have been already visited (including currentVertex)
	 * @param currentCost the cost of the path corresponding to <code>visited</code>
	 */
	private void branchAndBound(int currentVertex, Collection<Integer> unvisited,
			Collection<Integer> visited, double currentCost){


		//System.out.println("best Sol  " + bestSol);

		if (System.currentTimeMillis() - startTime > timeLimit) return;
	    if (unvisited.size() == 0){
	    	if (g.isArc(currentVertex,0)){
	    		if (currentCost+g.getCost(currentVertex,0) < bestSolCost){
	    			visited.toArray(bestSol);
	    			bestSolCost = currentCost+g.getCost(currentVertex,0);
	    		}
	    	}
	    } else if (currentCost+bound(currentVertex,unvisited) < bestSolCost){
	        Iterator<Integer> it = iterator(currentVertex, unvisited, g);
	        while (it.hasNext()){
	        	Integer nextVertex = it.next();
	        	visited.add(nextVertex);
	            unvisited.remove(nextVertex);
	            branchAndBound(nextVertex, unvisited, visited, 
	            		currentCost+g.getCost(currentVertex, nextVertex));
	            visited.remove(nextVertex);
	            unvisited.add(nextVertex);
	        }	    
	    }
	}

}
