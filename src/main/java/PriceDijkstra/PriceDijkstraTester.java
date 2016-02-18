package PriceDijkstra;

import PriceDijkstra.DijkstraExcl;
import PriceDijkstra.Model.Ignore;
import PriceDijkstra.Model.ResultPathly;
import com.orientechnologies.orient.client.remote.OServerAdmin;
import com.orientechnologies.orient.core.iterator.OEmptyIterator;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

import java.io.IOException;
import java.util.*;

/**
 * Created by Tkaewkunha on 1/22/16.
 */
public class PriceDijkstraTester {

    public static  void main(String arg[]){

        String nomeDb = "Demo2";
        try {
            System.out.println("Before connect OServerAdmin");
            OServerAdmin serverAdmin = new OServerAdmin("remote:128.199.166.185/"+nomeDb).connect("admin","iamitboxteam");
            System.out.println("After connect");
            if(serverAdmin.existsDatabase()){  //
                OrientGraph g = new OrientGraph("remote:128.199.166.185/"+nomeDb);

                Set<String> ignoreCategories = new HashSet<String>();
                ignoreCategories.add("#13:1");

                Iterable<Vertex> vertices = g.getVertices();

                Iterator<Vertex> vertexIterator = vertices.iterator();
                Set<Vertex> useVertex = new HashSet<Vertex>();

                while (vertexIterator.hasNext()){
                    Vertex v = vertexIterator.next();
                    OrientVertex ca = v.getProperty("category");
                    System.out.println("ca rid:" + ca.getId().toString());
                    if(!ignoreCategories.contains(ca.getId().toString())){
                        useVertex.add(v);
                    }
                }
                System.out.println("useVertex :" + useVertex);

                DijkstraExcl d = new DijkstraExcl(g, "Path",useVertex);

                Vertex start = g.getVertex("#12:7");
                Vertex end = g.getVertex("#12:13");
                Direction direction = Direction.OUT;
                ArrayList<Ignore> ignorelist = new ArrayList<Ignore>();
//                ignorelist.add(new Ignore("#12:12","#12:13","#14:6"));
                List<ResultPathly> resultDijkstra = d.getPath(start,end,direction,ignorelist);
                if(resultDijkstra != null){
                    System.out.println("resultDijkstra : " + resultDijkstra);
                }else{
                    System.out.println("resultDijkstra : This is the end");
                }
                g.shutdown();
            }
            else{
                System.out.println("Il database '"+ nomeDb + "' non esiste");
            }
            serverAdmin.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}






