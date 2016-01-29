import com.orientechnologies.orient.core.command.OBasicCommandContext;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import function.PathlyDijkstra;
import function.PathlyYen;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Tkaewkunha on 1/22/16.
 */
public class Yes {

    public static  void main(String arg[]){
        String path = "remote:128.199.166.185/Demo2";
        OrientGraph graph = new OrientGraph(path);
        OrientGraphFactory factory = new OrientGraphFactory(path);
        ODatabaseRecordThreadLocal.INSTANCE.set(factory.getDatabase());
        PathlyYen yen = new PathlyYen();
        yen.excute("#12:6","#12:11",3,"'distance'","both");
    }

}






