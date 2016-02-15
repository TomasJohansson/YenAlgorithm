package PriceDijkstra;

import PriceDijkstra.DijkstraExcl;
import com.orientechnologies.orient.client.remote.OServerAdmin;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

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
            if(serverAdmin.existsDatabase()){  // il db esiste
                //connessione a db
                OrientGraph g = new OrientGraph("remote:128.199.166.185/"+nomeDb);
                DijkstraExcl d = new DijkstraExcl(g, "Path");
                Set<String> excludeTrans =new HashSet<String>();
                //------------------------------------------------
                Vertex start = g.getVertex("#12:8");
                Vertex end = g.getVertex("#12:10");
//                excludeTrans.add("#14:6");

                Direction direction = Direction.OUT;
//                d.getPath(start,end,direction,ex);
                System.out.println(d.getPath(start,end,direction,excludeTrans));
//                System.out.println(d.getPathString(start,end,direction,ex));
//                System.out.println(d.getWeight(start,end,direction,ex));
                //------------------------------------------------
                //chiude db
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






