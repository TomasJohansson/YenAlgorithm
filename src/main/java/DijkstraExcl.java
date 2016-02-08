/**
 * Created by Tkaewkunha on 2/4/16.
 */
import java.util.*;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientElement;
import com.tinkerpop.blueprints.impls.orient.OrientElementIterable;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

public class DijkstraExcl {

    private OrientGraph g;          //grafh DB
    private Set<String> visited;          //visited rids
    private Set<String> toVisits;          //to visit rids
    private Map<String, Float> VertexWeight;          //VertexWeight(i)     < @rid, weight_to_get_to_@rid >
    private Map<String, String> childAndParent;          //childAndParent(i)     < @rid, previous_node_in_the_shortest_path >
    private String eClass;     //edge class to use
    private String prop;       //VertexWeight property to use on the edge

    public DijkstraExcl(OrientGraph g, String e, String p) {
        this.g = g;
        this.eClass = e;
        this.prop = p;
        visited = new HashSet<String>();
        toVisits = new HashSet<String>();
        VertexWeight = new HashMap<String, Float>();
        childAndParent = new HashMap<String, String>();
    }
    public void test(String rid){
        Edge e = g.getEdge(rid);
//        System.out.println(e.getProperty("distance").toString());
        System.out.println(e.toString());

        OrientElementIterable or = e.getProperty("transportations");
        Iterator<OrientElement> els = or.iterator();
        while(els.hasNext()){
            OrientElement el = els.next();
            System.out.println(el.getProperty("name").toString());
//            System.out.println(el.getProperty("category").toString());
        }

    }
    private void findPath(Vertex startV, Vertex endV, Direction dir, Set<String> excludeEdgeRids) {

        //init
        visited.clear(); //visited rids
        toVisits.clear();
        VertexWeight.clear();
        childAndParent.clear();

        System.out.print("\n ---------------------  Start Step1  --------------------- \n");

        //step1
        Iterator<Vertex> vertici = g.getVertices().iterator();

        while (vertici.hasNext()) {
            Vertex ver = vertici.next();
            VertexWeight.put(ver.getId().toString(), Float.MAX_VALUE);
            toVisits.add(ver.getId().toString());
        }
        VertexWeight.put(startV.getId().toString(), 0f);        //VertexWeight(startV) = 0

//        System.out.println("--- VertexWeight Set StartV to 0");
//        printMap(VertexWeight);

        childAndParent.put(startV.getId().toString(), null);     //childAndParent(startV) = null
        toVisits.remove(startV.getId().toString());        //startV visited => removed from toVisits
        visited.add(startV.getId().toString());           //and added in visited


        Iterator<Vertex> neighbors = startV.getVertices(dir, eClass).iterator();

        while (neighbors.hasNext()) {
            Vertex vicino = neighbors.next();
            childAndParent.put(vicino.getId().toString(), startV.getId().toString());            //childAndParent(i) = startV
            VertexWeight.put(vicino.getId().toString(), calculateWeight(startV.getId().toString(), vicino.getId().toString(), dir, excludeEdgeRids));     //VertexWeight(i) = VertexWeight(startV, i)
        }

        System.out.print("\n ======================= End Step1 ======================= ");
        System.out.print("\n ---------------------  Start Step2  --------------------- \n");

        //step2
        System.out.println(" --- VertexWeight  :");
        printMap2(VertexWeight);
        System.out.println(" --- Children And parent  :");
        printMap2(childAndParent);

        Boolean continuos = false;
        Iterator<String> itrToVisits = toVisits.iterator();
        while (itrToVisits.hasNext()) {
            String strToVisit = itrToVisits.next();
            if (VertexWeight.get(strToVisit) != Float.MAX_VALUE) {
                System.out.println("\n--- toVisit !- Max value : " + strToVisit);
                continuos = true;
                break;
            }
        }
        while (continuos) {
            String newVisited = startV.getId().toString();
            Float wieght = Float.MAX_VALUE;
            System.out.println("\n\n<><><><>find new Visited<><><><>");
            System.out.println("!--- toVisit :");
            printSetString(toVisits);
            System.out.println("!--- visited :");
            printSetString(visited);

            itrToVisits = toVisits.iterator();
            while (itrToVisits.hasNext()) {
                String nextToVisit = itrToVisits.next();
                //Find smallest weight
                if (VertexWeight.get(nextToVisit) <= wieght) {
//                    System.out.println("--- Weight : " + wieght +" - vs - \nVertexWeight.get(nextToVisit): " + VertexWeight.get(nextToVisit));
                    wieght = VertexWeight.get(nextToVisit);
                    System.out.println("!--- set new Visited  : " + nextToVisit );
                    newVisited = nextToVisit;
                }
            }
            toVisits.remove(newVisited);
            visited.add(newVisited);

            System.out.println("!--- toVisit :");
            printSetString(toVisits);
            System.out.println("!--- visited :");
            printSetString(visited);

            if (toVisits.isEmpty()) {
                break;
            }

            System.out.print("\n---------------------  Start Step3  --------------------- \n");
            //step3
            System.out.println("--- New Visited : " + newVisited);

            neighbors = g.getVertex(newVisited).getVertices(dir, eClass).iterator();

            while (neighbors.hasNext()) {
                Vertex vertexNeighbor = neighbors.next();
                String neighbor = vertexNeighbor.getId().toString();
                System.out.println("\n !!!--- New Visited's Neighbor : " + neighbor);
                System.out.print(" --- VertexWeight  :");
                printMap2(VertexWeight);

                Float newVisitToNeigborWeight = calculateWeight(newVisited, neighbor, dir, excludeEdgeRids);

                System.out.println(" --- New Visited's Neighbor : " + neighbor);
                System.out.println(" --- VertexWeight.get(neighbor)  : " + VertexWeight.get(neighbor) + "\n");
                System.out.println(" --- VertexWeight.get(newVisited)  : " + VertexWeight.get(newVisited) + "+");
                System.out.println(" --- newVisitToNeigborWeight  : " + newVisitToNeigborWeight);

                if ((toVisits.contains(neighbor)) && (VertexWeight.get(neighbor) > (VertexWeight.get(newVisited) + newVisitToNeigborWeight))) {
                    System.out.println("  *--- Inside LongCondition  :");

                    Float newVisitedToNeighbor = calculateWeight(newVisited, neighbor, dir, excludeEdgeRids);
                    System.out.println("  --- newVisitedToNeighbor : " + newVisitedToNeighbor);
                    System.out.println(" --- VertexWeight  :");
                    printMap2(VertexWeight);
                    System.out.println(" --- Children And parent  :");
                    printMap2(childAndParent);

                    if (newVisitedToNeighbor == Integer.MAX_VALUE) {
                        VertexWeight.put(neighbor, Float.MAX_VALUE);
                    } else {
                        System.out.println("\n  *** VertexWeight.Put key neighbor :"+neighbor+" " +
                                ", value  VertexWeight.get(newVisited) + newVisitedToNeighbor : "
                                + (VertexWeight.get(newVisited) + newVisitedToNeighbor) + "\n");

                        VertexWeight.put(neighbor, (VertexWeight.get(newVisited) + newVisitedToNeighbor));
                    }
                    //Important!!!
                    System.out.println("\n  *** childAndParent.put child :" + neighbor+" parent : " + newVisited + "\n");
                    childAndParent.put(neighbor, newVisited);
                    //Add edge's rid and Transportation rid here

                    System.out.println(" --- VertexWeight  :");
                    printMap2(VertexWeight);
                    System.out.println(" --- Children And parent  :");
                    printMap2(childAndParent);
//                    System.out.println("--- toVisit :");
//                    printSetString(toVisits);
//                    System.out.println("--- visited :");
//                    printSetString(visited);
                }

            }

            //shall we continue?
            continuos = false;
            System.out.println("\n --- countinuos : " + continuos);
            itrToVisits = toVisits.iterator();
            while (itrToVisits.hasNext()) {
                String toVisit = itrToVisits.next();
                if (VertexWeight.get(toVisit) != Float.MAX_VALUE) {
                    System.out.println(" --- tovisit : " + toVisit + "VertexWeight.get(toVisit) : " + VertexWeight.get(toVisit));
                    continuos = true;
                }
            }
        }
    }

    private float calculateWeight(String start, String end, Direction dir, Set<String> excl) {        //in case of multiple/duplicate edges return the lightest
        Float initWeight = Float.MAX_VALUE;
        Float propWeight;
        end = "v(Station)[" + end + "]";

        if (excl == null) {
            excl = new HashSet<String>();
        }

        System.out.println("\n### --- calculateWeight() --- ###");
        System.out.println("--- Parent : " + start);
        System.out.println("--- Children : " + end);
        System.out.print("--- ExcludeEdge : ");
        printSetString(excl);

        Vertex parentVertex = g.getVertex(start);
        Iterator<Edge> parentEdge = parentVertex.getEdges(dir, eClass).iterator();
        Set<Edge> excludeIgnoredEdge = new HashSet<Edge>();
        while (parentEdge.hasNext()) {
            Edge e = parentEdge.next();
            if ((e.getProperty("out").toString().equals(end) || e.getProperty("in").toString().equals(end))
                    && !excl.contains(e.getId().toString())) {
                excludeIgnoredEdge.add(e);
            }
        }
        System.out.print("--- UseEdge : ");
        printSetEdge(excludeIgnoredEdge);
        Iterator<Edge> useEdges = excludeIgnoredEdge.iterator();

        Edge bestEdge = null;
        while (useEdges.hasNext()) {
            Edge edge = useEdges.next();
            //Prop wieght must be price , Find best price from edge @rid and compare all Transportation's price
            //Find Previous direction
            // Then should make ignoring some Transportation here if have to.
            propWeight = edge.getProperty(prop);
            if (propWeight < initWeight) {
                initWeight = propWeight;
                bestEdge = edge;
                System.out.println("--- Change Weight : " + initWeight);
                System.out.print(" --- Children And parent  :");
                printMap2(childAndParent);
            }

        }
        //before return the weight have to transportation RID and Path RID
        if(bestEdge != null){
            System.out.println("--- best edge :" + bestEdge.getId().toString());
        }
        System.out.println("//--- End of calculateWieght :" + initWeight);
        return initWeight;
    }


    //public methods

    public List<Vertex> getPath(Vertex startV, Vertex endV, Direction dir, Set<String> exclECl) {
        String parent, end;
        List<Vertex> result = new ArrayList<Vertex>();
        List<Vertex> path = new ArrayList<Vertex>();

        findPath(startV, endV, dir, exclECl);

        System.out.println(" getPath--- VertexWeight  :");
        printMap2(VertexWeight);
        System.out.println(" getPath--- Children And parent  :");
        printMap2(childAndParent);
        System.out.println("getPath--- toVisit :");
        printSetString(toVisits);
        System.out.println("getPath--- visited :");
        printSetString(visited);

        end = endV.getId().toString();
        path.add(endV);
        if (VertexWeight.get(endV.getId().toString()) == Integer.MAX_VALUE) {
            return null;
        }
        while (!end.equals(startV.getId().toString())) {
            parent = childAndParent.get(end);
            System.out.println("new Loop");

            System.out.println("Parent : " + parent);
            System.out.println("End : " + end);

            if (parent == null) {
                return null;
            }

            path.add(g.getVertex(parent));
            end = parent;
            System.out.println(" --- ");
            System.out.println("End : " + end);
            System.out.println(path);

        }
        System.out.println(path);
        for (int a = 0, b = path.size() - 1; a < path.size(); a++, b--) {
            result.add(a, path.get(b));
        }

        return result;
    }

    public List<String> getPathString(Vertex startV, Vertex endV, Direction dir, Set<String> exclECl) {
        List<String> pathS = new ArrayList<String>();
        List<Vertex> path = getPath(startV, endV, dir, exclECl);

        if (path == null) {
            return null;
        }
        for (Vertex v : path) {
            pathS.add(v.getId().toString());
        }
        return pathS;
    }

    public Float getWeight(Vertex startV, Vertex endV, Direction dir, Set<String> exclECl) {
        findPath(startV, endV, dir, exclECl);
        return VertexWeight.get(endV.getId().toString());
    }

    private void printMap(Map map) {

        Set setOfKeys = map.keySet();

        Iterator iterator = setOfKeys.iterator();

        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            String value = map.get(key).toString();
            System.out.println("Key: " + key + ", Value: " + value + ";");
        }
        System.out.println();
    }
    private void printMap2(Map map) {
        System.out.println(map);
    }

    private void printIterEdge(Iterator<Edge> eds){
        while(eds.hasNext()){
            System.out.print(eds.next().getId().toString() + ", ");
        }
        System.out.println();

    }
    private void printSetEdge(Set<Edge> edgeSet){
        for(Edge e:edgeSet){
            System.out.print(e.getId().toString() + " ,");
        }
        System.out.println();
    }
    private void printSetString(Set<String> stringSet){
        for(String string:stringSet){
            System.out.print(string+  " ,");
        }
        System.out.println();
    }
}
