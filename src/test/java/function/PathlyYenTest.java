package function;


import com.orientechnologies.orient.core.command.OBasicCommandContext;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import model.DijkstraResult;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class PathlyYenTest {
    private static OrientGraph graph;
    private static OrientVertex v1;
    private static OrientVertex v2;
    private static OrientVertex v3;
    private static OrientVertex v4;
    private static OrientVertex v5;
    private static OrientVertex v6;
    private static PathlyYen pathlyYen;
    private static final double DELTA = 1e-15;

    @BeforeClass
    public static void setUp() throws Exception {
        setUpDatabase();
        pathlyYen = new PathlyYen();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        graph.shutdown();
    }
    private static void setUpDatabase() {
        graph = new OrientGraph("memory:PathlyYenTest");
        graph.createEdgeType("weight");
        v1 = graph.addVertex(null);
        v2 = graph.addVertex(null);
        v3 = graph.addVertex(null);
        v4 = graph.addVertex(null);
        v5 = graph.addVertex(null);
        v6 = graph.addVertex(null);

        v1.setProperty("node_id", "A");
        v2.setProperty("node_id", "B");
        v3.setProperty("node_id", "C");
        v4.setProperty("node_id", "D");
        v5.setProperty("node_id", "E");
        v6.setProperty("node_id", "F");


        Edge e1 = graph.addEdge(null, v1, v2, "weight");
        e1.setProperty("weight", 2.0f);
        Edge e2 = graph.addEdge(null, v1, v3, "weight");
        e2.setProperty("weight", 3.0f);
        Edge e3 = graph.addEdge(null, v2, v3, "weight");
        e3.setProperty("weight", 1.0f);
        Edge e4 = graph.addEdge(null, v2, v5, "weight");
        e4.setProperty("weight", 2.0f);
        Edge e5 = graph.addEdge(null, v2, v4, "weight");
        e5.setProperty("weight", 3.0f);
        Edge e6 = graph.addEdge(null, v3, v5, "weight");
        e6.setProperty("weight", 4.0f);
        Edge e7 = graph.addEdge(null, v4, v6, "weight");
        e7.setProperty("weight", 2.0f);
        Edge e8 = graph.addEdge(null, v5, v4, "weight");
        e8.setProperty("weight", 2.0f);
        Edge e9 = graph.addEdge(null, v5, v6, "weight");
        e9.setProperty("weight", 1.0f);
        graph.commit();
    }

    @Test
    public void testExecute() throws Exception {
        List<DijkstraResult> yenResult =  pathlyYen.excute(v1.getIdentity().toString(),v6.getIdentity().toString(),3,"'weight'","out");
        assertEquals(3, yenResult.size());
        DijkstraResult yen1 = yenResult.get(0);
        assertEquals(4, yen1.getShortestPath().size());
        assertEquals(5, yen1.getTotalCost(), DELTA);
        assertEquals(v1.getIdentity(), yen1.getShortestPath().get(0));
        assertEquals(v2.getIdentity(), yen1.getShortestPath().get(1));
        assertEquals(v5.getIdentity(), yen1.getShortestPath().get(2));
        assertEquals(v6.getIdentity(), yen1.getShortestPath().get(3));

        DijkstraResult yen2 = yenResult.get(1);
        assertEquals(4, yen2.getShortestPath().size());
        assertEquals(7, yen2.getTotalCost(), DELTA);
        assertEquals(v1.getIdentity(), yen2.getShortestPath().get(0));
        assertEquals(v2.getIdentity(), yen2.getShortestPath().get(1));
        assertEquals(v4.getIdentity(), yen2.getShortestPath().get(2));
        assertEquals(v6.getIdentity(), yen2.getShortestPath().get(3));

        DijkstraResult yen3 = yenResult.get(2);
        assertEquals(4, yen3.getShortestPath().size());
        assertEquals(8, yen3.getTotalCost(), DELTA);
        assertEquals(v1.getIdentity(), yen3.getShortestPath().get(0));
        assertEquals(v3.getIdentity(), yen3.getShortestPath().get(1));
        assertEquals(v5.getIdentity(), yen3.getShortestPath().get(2));
        assertEquals(v6.getIdentity(), yen3.getShortestPath().get(3));
    }


}
