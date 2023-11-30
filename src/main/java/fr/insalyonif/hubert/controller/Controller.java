package fr.insalyonif.hubert.controller;
import fr.insalyonif.hubert.views.DeliveryIHMController;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import fr.insalyonif.hubert.model.*;

public class Controller {
    private CityMap cityMap;
    private Dijkstra dij;

    private DijkstraInverse dijInv;
    private int sizeGraph;

    private ArrayList<DeliveryRequest> listeDelivery;

    public CityMap getCityMap() {
        return cityMap;
    }

    public void setCityMap(CityMap cityMap) {
        this.cityMap = cityMap;
    }

    public Dijkstra getDij() {
        return dij;
    }

    public void setDij(Dijkstra dij) {
        this.dij = dij;
    }

    public DijkstraInverse getDijInv() {
        return dijInv;
    }

    public void setDijInv(DijkstraInverse dijInv) {
        this.dijInv = dijInv;
    }

    public int getSizeGraph() {
        return sizeGraph;
    }

    public void setSizeGraph(int sizeGraph) {
        this.sizeGraph = sizeGraph;
    }

    public ArrayList<DeliveryRequest> getListeDelivery() {
        return listeDelivery;
    }

    public void setListeDelivery(ArrayList<DeliveryRequest> listeDelivery) {
        this.listeDelivery = listeDelivery;
    }

    public Controller() {
        cityMap = new CityMap();
        listeDelivery = new ArrayList<>();

        try {
            String xmlMap = "src/main/resources/fr/insalyonif/hubert/fichiersXML2022/mediumMap.xml";
            cityMap.loadFromXML(xmlMap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        sizeGraph = cityMap.getIntersections().size(); // Mettez la taille correcte de votre graphe
        dij = new Dijkstra(sizeGraph, cityMap);
        dijInv = new DijkstraInverse(sizeGraph,cityMap);
    }

    public boolean newDeliveryPoint(DeliveryIHMController deliveryIHM) {
        Intersection intersectionPlusProche = trouverIntersectionPlusProche(deliveryIHM.getLatDouble(), deliveryIHM.getLngDouble(), cityMap.getIntersections());

        // Afficher les résultats
        System.out.println("Coordonnées de l'emplacement donné : " + deliveryIHM.getLatDouble() + ", " + deliveryIHM.getLngDouble());
        System.out.println("Intersection la plus proche : " + intersectionPlusProche.getLatitude() + ", " + intersectionPlusProche.getLongitude());


        boolean b1 = dij.runDijkstra(intersectionPlusProche, sizeGraph);
        boolean b2 = dijInv.runDijkstra(intersectionPlusProche, sizeGraph);
        //Si un des deux false alors pop up BOOL1 && BOOL2
        if (b1 && b2) {
            DeliveryRequest deli = new DeliveryRequest((intersectionPlusProche));
            listeDelivery.add(deli);
            Graph g = new CompleteGraph(dij.getChemins(), listeDelivery, cityMap);


            TSP tsp = new TSP1();
            tsp.searchSolution(20000, g);
            System.out.print("Solution of cost " + tsp.getSolutionCost());
            for (int i = 0; i < listeDelivery.size(); i++)
                System.out.print(tsp.getSolution(i) + " ");
            System.out.println("0");
            List<Chemin> bestChemin = tsp.bestCheminGlobal(dij.getChemins());

            System.out.println("Meilleur chemin global :");
            for (Chemin chemin : bestChemin) {
                System.out.println(chemin);
                //System.out.println("Départ : " + chemin.getDebut() + " -> Arrivée : " + chemin.getFin()+ " | Coût : " + chemin.getCout());
            }

            cityMap.setChemins(bestChemin);
            MAJDeliveryPointList();
            return true;
        }else{
            return false;
        }
    }

    private void MAJDeliveryPointList(){
        List<Chemin> chemins =cityMap.getChemins();
        Map<Intersection, Integer> intersectionIndexMap = new HashMap<>();
        for (int i = 0; i < chemins.size(); i++) {
            Chemin chemin = chemins.get(i);
            intersectionIndexMap.put(chemin.getFin(), i);
        }

        // Trier la liste de points de livraison en fonction de l'ordre des intersections dans les chemins
        listeDelivery.sort((dp1, dp2) -> {
            int index1 = intersectionIndexMap.get(dp1.getDeliveryLocation());
            int index2 = intersectionIndexMap.get(dp2.getDeliveryLocation());
            return Integer.compare(index1, index2);
        });
    }

    public static Intersection trouverIntersectionPlusProche(double lat, double lng, List<Intersection> intersections) {
        if (intersections == null || intersections.isEmpty()) {
            return null; // La liste d'intersections est vide
        }

        Intersection intersectionPlusProche = intersections.get(0);
        double distanceMin = distance(lat, lng, intersectionPlusProche.getLatitude(), intersectionPlusProche.getLongitude());

        for (Intersection intersection : intersections) {
            double distanceActuelle = distance(lat, lng, intersection.getLatitude(), intersection.getLongitude());
            if (distanceActuelle < distanceMin) {
                distanceMin = distanceActuelle;
                intersectionPlusProche = intersection;
            }
        }

        return intersectionPlusProche;
    }

    private static double distance(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371; // Rayon de la Terre en kilomètres

        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return earthRadius * c; // Distance en kilomètres
    }

}
