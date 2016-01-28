package function;

import com.orientechnologies.orient.core.command.OBasicCommandContext;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.graph.sql.OGraphCommandExecutorSQLFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import model.DijkstraResult;

import java.util.*;

/**
 * Created by Tkaewkunha on 1/24/16.
 */
public class PathlyYen {
    PathlyDijkstra pathlyDij;
    //list A & B for keeping K Shortest Path answer and Spur Path
    List<LinkedList<OrientVertex>> listA;
    List<DijkstraResult> listB;
    List<DijkstraResult> listBB;
    OrientVertex spurNode;
    ArrayList<OrientVertex> rootPath, pPath;
    float[] costColect;
    OrientGraph graph;
    //Constructor 1
    public PathlyYen() {
        pathlyDij = new PathlyDijkstra();
        listA = new ArrayList<>();
        listB = new ArrayList<>();
        rootPath = new ArrayList<>();
        pPath = new ArrayList<>();
        listBB = new ArrayList<>();
    }
    /**
     * Yen's algorithm computes single-source K-shortest loopless paths for a graph with non-negative edge cost.
     *
     * @param source      start vertex of path
     * @param destination destination vertex of path
     * @param K           number of shortest path
     * @param weightParam propertiy "'distance'"
     * @param direction in,out,both "both"
     */
    public void excute(String source, String destination, Integer K,String weightParam,String direction) {
        //Count All vertex
        int numberOfAllVertex = 0;
        int vertexCount = 0;
        String path = "remote:128.199.166.185/Demo2";
        this.graph = new OrientGraph(path);
        String sql = "SELECT COUNT(*) as countall FROM Station";

        List<ODocument> result = null;
        try {
            result =  graph.getRawGraph().query(
                    new OSQLSynchQuery(sql));
        } catch (Exception e) {
            this.graph.rollback();
        } finally {
            this.graph.shutdown();
        }
        for(ODocument doc :result){
            numberOfAllVertex = Integer.parseInt(doc.field("countall").toString());
        }

        System.out.println("--- Count All Vertex :" + numberOfAllVertex);


        DijkstraResult dijkstraResult;
        float[] rootPathCost;
        //Step 0: First  Call dijkstra
        dijkstraResult = pathlyDij.executePathlyDij(null, null, null, new Object[]{source, destination, weightParam, direction},
                new OBasicCommandContext(), null, new String[]{null});
        rootPathCost = dijkstraResult.getRootPathCost();
        System.out.println("<------- 1st Shortest Path");
        printDijkstraResult(dijkstraResult);
        listA.add(dijkstraResult.getShortestPath());

        for (int round = 1; round < K; round++) {
            System.out.println(" ##########################################################");
            System.out.println(" ------- Round :" + round + " finding all the deviations(potential KSP)");
            System.out.println(" ##########################################################");

            int listAIden = listA.size() - 1;

            for (int potentailKSP = 0; potentailKSP < listA.get(listAIden).size() - 1 ;
                 potentailKSP++) {

                System.out.println("\n\n>------ Find Potential :" + potentailKSP + " ------<");

                spurNode = listA.get(listAIden).get(potentailKSP);
                System.out.println("--- spurNode : " + spurNode.getIdentity().toString());

                // root path = first node to i
                rootPath.clear();
                if (potentailKSP > 0) {
                    for (int j = 0; j < potentailKSP; j++) {
                        rootPath.add(listA.get(listAIden).get(j));
                    }
                }
                System.out.print("--- rootPath :");
                printList(rootPath);
                System.out.println("--- Root Path Cost :" + rootPathCost[potentailKSP]);

                String spur = spurNode.getIdentity().toString();
                List<String> ignore =  addIgnoreFromListA(spurNode);
                System.out.print("--- KSP use in " + round + " : ");
                printList(listA.get(listAIden));
                System.out.print("\n--- Now ListA ---");
                printLisOfLis(listA);
                // TODO: The way to ignore the links that are part of the previous shortest paths which share the same root path.
                /////////////////// Not done yet.
                String[] nextIgnore = ignore.toArray(new String[ignore.size()]);

                //Delete Duplicate value in Ignore Array
                Set<String> setIgnore = new HashSet<>(Arrays.asList(nextIgnore));
                nextIgnore = setIgnore.toArray(new String[0]);

                System.out.print("\n--- Ignore Array  --- ");
                printListString(nextIgnore);
                // Calculate the spur path from the spur node to the destination.
                // Need to collect cost collection
                dijkstraResult = pathlyDij.executePathlyDij(null, null, null, new Object[]{spurNode.getIdentity().toString(), destination, weightParam, direction},
                        new OBasicCommandContext(), spur, nextIgnore);
                if(dijkstraResult == null){
                    System.out.println(".....This is the end....");
                    return;
                }
                System.out.print("--- new SpurPath : ");
                printDijkstraResult(dijkstraResult);
                // Entire path is made up of the root path and spur path.
                LinkedList<OrientVertex> totalPath = new LinkedList<OrientVertex>();
                totalPath.addAll(rootPath);
                totalPath.addAll(dijkstraResult.getShortestPath());

                System.out.print("--- Potential KSP (total path) : ");
                printList(totalPath);
                float totalCost = rootPathCost[potentailKSP] + dijkstraResult.getTotalCost();
                System.out.println(" (" + totalCost + ")");

                // TODO: Add the potential k-shortest path to the ListB.
                float[] newRootPathCost = new float[totalPath.size() - 1];
                for (int i = 0; i <= potentailKSP; i++) {
                    newRootPathCost[i] = rootPathCost[i];
                }
                for (int i = potentailKSP; i < totalPath.size() - 1; i++) {
                    newRootPathCost[i] = rootPathCost[potentailKSP] + dijkstraResult.getRootPathCost()[i - potentailKSP];
                }
                System.out.println("*** Print newRootPathCost ***");
                printArray(newRootPathCost);
                // add it to list B
                addDijkstraResultToListB(new DijkstraResult(totalPath, totalCost, newRootPathCost));
                System.out.println("><><><><><>< listB ><><><><><>< ");
                printListDijkstraResult(listB);

            }

            // TODO: Sort the potential k-shortest paths by cost. (Sort ListB)
            // TODO: Add the lowest cost path becomes the k-shortest path. (Add to ListA)
            DijkstraResult lowestCostDijkstra = popLowestCostFromListB();
            listA.add(lowestCostDijkstra.getShortestPath());
            rootPathCost = lowestCostDijkstra.getRootPathCost();
            System.out.print("\n><><><><><>< listA -- ");
            printLisOfLis(listA);
            System.out.println("\n><><><><><>< listB ><><><><><>< ");
            printListDijkstraResult(listB);
            System.out.print("\n");

        }

    }

    private void addDijkstraResultToListB(DijkstraResult newDijkstraResult) {
        if(listB.size() == 0){
            listB.add(newDijkstraResult);
        } else {
            for (int i = 0; i < listB.size(); i++) {
                if (listB.get(i).compareTo(newDijkstraResult) != 0) {
                    if(i == (listB.size()-1)) {
                        listB.add(newDijkstraResult);
                    }
                } else {
                    break;
                }
            }
        }
    }

    private DijkstraResult popLowestCostFromListB() {
        Collections.sort(listB);
        DijkstraResult lowestCost = listB.get(0);
        listB.remove(0);
        return lowestCost;
    }

    public List<String> addIgnoreFromListA(OrientVertex spurNode) {
        String spur = spurNode.getIdentity().toString();
        List<String> ignore = new ArrayList<>();
        for (LinkedList<OrientVertex> als : listA) {
            for (OrientVertex a : als) {
                if(spur.equals(a.getIdentity().toString()) && als.indexOf(a) < als.size() - 1 ){
                    String nextIgnore = als.get(als.indexOf(a) + 1).getIdentity().toString();
                    ignore.add(nextIgnore);
                }
            }
        }
        return ignore;
    }

    public void printList(LinkedList<OrientVertex> ls) {
        if (ls == null) {
            return;
        }
        for (OrientVertex v : ls) {
            System.out.print(v.getIdentity().toString() + ",");
        }
    }

    public void printList(ArrayList<OrientVertex> ls) {
        for (OrientVertex v : ls) {
            System.out.print(v.getIdentity().toString() + ",");
        }
        System.out.println();
    }

    public void printListString(List<String> ls) {
        for (String s : ls) {
            System.out.print(s + ",");
        }
        System.out.println();
    }

    public void printListString(String[] ar) {
        for (String s : ar) {
            System.out.print(s + ",");
        }
        System.out.println();
    }

    public void printLisOfLis(List<LinkedList<OrientVertex>> lisoflis) {
        System.out.print("[");

        for (LinkedList<OrientVertex> ls : lisoflis) {
            System.out.print("{");
            for (OrientVertex v : ls) {
                System.out.print(v.getIdentity().toString() + ", ");
            }
            System.out.print("} , ");

        }
        System.out.print("]");
    }

    public void printDijkstraResult(DijkstraResult result) {
        LinkedList<OrientVertex> shotestPath = result.getShortestPath();
        float totalDistance = result.getTotalCost();
        printList(shotestPath);
        System.out.print("(" + totalDistance + ")");
        printArray(result.getRootPathCost());

    }

    public void printListDijkstraResult(List<DijkstraResult> dijkstraResults) {
        for (int i = 0; i < dijkstraResults.size(); i++) {
            System.out.print("[" + i + "]");
            printDijkstraResult(dijkstraResults.get(i));
        }
    }


    public void printArray(float[] fArr) {
        System.out.print("{ ");
        for (float f : fArr) {
            System.out.print(f + " , ");
        }
        System.out.println("}");

    }

}
