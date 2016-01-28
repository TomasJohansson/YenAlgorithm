package function;

import com.orientechnologies.orient.core.command.OBasicCommandContext;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import model.DijkstraResult;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PathlyDijkstraTest {
    private static OrientGraph graph;
    private static OrientVertex v1;
    private static OrientVertex v2;
    private static OrientVertex v3;
    private static OrientVertex v4;
    private static PathlyDijkstra functionDijkstra;
    private static final double DELTA = 1e-15;

    @BeforeClass
    public static void setUp() throws Exception {
        setUpDatabase();

        functionDijkstra = new PathlyDijkstra();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        graph.shutdown();
    }

    private static void setUpDatabase() {
        graph = new OrientGraph("memory:PathlyDijkstraTest");
        graph.createEdgeType("weight");

        v1 = graph.addVertex(null);
        v2 = graph.addVertex(null);
        v3 = graph.addVertex(null);
        v4 = graph.addVertex(null);

        v1.setProperty("node_id", "A");
        v2.setProperty("node_id", "B");
        v3.setProperty("node_id", "C");
        v4.setProperty("node_id", "D");

        Edge e1 = graph.addEdge(null, v1, v2, "weight");
        e1.setProperty("weight", 1.0f);
        Edge e2 = graph.addEdge(null, v2, v3, "weight");
        e2.setProperty("weight", 1.0f);
        Edge e3 = graph.addEdge(null, v1, v3, "weight");
        e3.setProperty("weight", 100.0f);
        Edge e4 = graph.addEdge(null, v3, v4, "weight");
        e4.setProperty("weight", 1.0f);
        graph.commit();
    }

    @Test
    public void testExecute() throws Exception {
        final DijkstraResult dijkstraResult = functionDijkstra.execute(null, null, null,
                        new Object[]{v1, v4, "'weight'", "OUT", "weight"}, new OBasicCommandContext());

        assertEquals(4, dijkstraResult.getShortestPath().size());
        assertEquals(v1.getIdentity(), dijkstraResult.getShortestPath().get(0));
        assertEquals(v2.getIdentity(), dijkstraResult.getShortestPath().get(1));
        assertEquals(v3.getIdentity(), dijkstraResult.getShortestPath().get(2));
        assertEquals(v4.getIdentity(), dijkstraResult.getShortestPath().get(3));
        assertEquals(3, dijkstraResult.getTotalCost(), DELTA);
    }
}
