package fr.insalyonif.hubert.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DijkstraTest {

    public ArrayList<Intersection> deliveryRequest = new ArrayList<>();

    protected CityMap cityMap;
    
    private Dijkstra dijkstra;

    private Intersection inter1;
    private Intersection inter2;
    private Intersection inter3;

    @BeforeEach
    void setUp() {
        cityMap = new CityMap();
        try {
            cityMap.loadFromXML("src/main/resources/fr/insalyonif/hubert/fichiersXML2022/smallMap.xml");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        inter1 = cityMap.getIntersections().get(0);
        inter2 = cityMap.getIntersections().get(10);
        inter3 = cityMap.getIntersections().get(20);

        dijkstra = new Dijkstra(0,cityMap);

        dijkstra.deliveryRequest.add(inter1);
        dijkstra.deliveryRequest.add(inter2);
        dijkstra.deliveryRequest.add(inter3);
    }


    @Test
    void testHasGrayNode() {
        dijkstra.colors = new String[]{"white", "gray", "black"};
        assertTrue(dijkstra.hasGrayNode(), "Devrait retourner vrai quand il y a un nœud gris");

        dijkstra.colors = new String[]{"white", "white", "black"};
        assertFalse(dijkstra.hasGrayNode(), "Devrait retourner faux quand il n'y a pas de nœud gris");
    }

    @Test
    void testMinGrayNode() {
        dijkstra.colors = new String[]{"gray", "white", "gray"};
        dijkstra.distance = new double[]{5.0, 10.0, 3.0};
        Intersection expected = cityMap.findIntersectionByPos(2); // Assurez-vous que cela correspond à l'intersection attendue
        assertEquals(expected, dijkstra.minGrayNode(), "Doit retourner le nœud gris avec la plus petite distance");
    }

    @Test
    void testGetChemins() {
        ArrayList<Chemin> expected = dijkstra.getChemins();
        assertNotNull(expected, "La liste des chemins ne doit pas être null");
    }

    @Test
    void testGetDeliveryRequest() {
        List<Intersection> expected = dijkstra.getDeliveryRequest();
        assertNotNull(expected, "La liste des demandes de livraison ne doit pas être null");
        assertTrue(expected.containsAll(deliveryRequest), "La liste des demandes de livraison doit contenir tous les éléments de deliveryRequest");
    }

    @Test
    void testSelectNode() {
        RoadSegment roadSegment = new RoadSegment(inter1, inter2, "Route 66",1.0);
        Intersection result = dijkstra.selectNode(roadSegment);
        assertEquals(inter2, result, "selectNode doit retourner la destination du RoadSegment");
    }

    @Test
    void testCreateChemin() {
        int[] pi = new int[]{0, 1, 2}; // Un exemple de tableau de prédécesseurs
        double cout = 10.0;
        Chemin result = dijkstra.createChemin(inter1, inter3, pi, cout);

        assertEquals(inter1, result.getDebut(), "Le début du chemin doit être inter1");
        assertEquals(inter3, result.getFin(), "La fin du chemin doit être inter3");
        assertEquals(cout, result.getCout(), 0.0001, "Le coût du chemin doit être correct");
        assertArrayEquals(pi, result.getPi(), "Le tableau de prédécesseurs doit correspondre");
    }

    @Test
    void testAddDeliveryRequest() {
        Intersection newDelivery = new Intersection(3, 1.0, 123, 10); // Créez une nouvelle intersection de livraison
        dijkstra.addDeliveryRequest(newDelivery);
        assertTrue(dijkstra.getDeliveryRequest().contains(newDelivery), "La nouvelle intersection de livraison doit être ajoutée à la liste");
    }

}