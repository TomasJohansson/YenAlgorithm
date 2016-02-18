package TimeYen;

import TimeYen.TimeDijkstra.TimeDijkStra;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Tkaewkunha on 2/18/16.
 */
public class TimeYenTester {
    public static void main(String arg[]) {
        System.out.println("Tester");
        String nomeDb = "Demo2";

        OrientGraph g = new OrientGraph("remote:128.199.166.185/"+nomeDb);
        Iterable<Vertex> vertices = g.getVertices();
        Iterator<Vertex> vertexIterator = vertices.iterator();
        Set<String> ignoreCategories = new HashSet<String>();
        ignoreCategories.add("#13:1");
        Set<Vertex> useVertex = new HashSet<Vertex>();

        while (vertexIterator.hasNext()){
            Vertex v = vertexIterator.next();
            OrientVertex ca = v.getProperty("category");
            System.out.println("ca rid:" + ca.getId().toString());
            if(!ignoreCategories.contains(ca.getId().toString())){
                useVertex.add(v);
            }
        }

        TimeDijkStra d = new TimeDijkStra(g, "Path", "time",useVertex);

        Set<String> ex =new HashSet<String>();
        Vertex start = g.getVertex("#12:7");
        Vertex end = g.getVertex("#12:13");
//                ex.add("#13:0");
//                ex.add("#13:8");

        Direction direction = Direction.OUT;
        System.out.println(d.getPath(start,end,direction,ex));
        g.shutdown();
    }
}
