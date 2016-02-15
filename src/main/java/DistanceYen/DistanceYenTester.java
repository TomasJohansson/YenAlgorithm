package DistanceYen;

import com.orientechnologies.orient.client.remote.OServerAdmin;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import function.PathlyYen;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Tkaewkunha on 1/22/16.
 */
public class DistanceYenTester {

    public static  void main(String arg[]){
        String path = "remote:128.199.166.185/Demo2";
        OrientGraphFactory factory = new OrientGraphFactory(path);
        ODatabaseRecordThreadLocal.INSTANCE.set(factory.getDatabase());
        PathlyYen yen = new PathlyYen();
        yen.excute("#12:7","#12:13",3,"'distance'","out");
    }

}






