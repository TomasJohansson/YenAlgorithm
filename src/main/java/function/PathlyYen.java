package function;

import com.orientechnologies.orient.core.command.OBasicCommandContext;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import model.DijkstraResult;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Tkaewkunha on 1/24/16.
 */
public class PathlyYen {
    public PathlyDijkstra pathlyDij;
    //list A & B for keeping K Shortest Path answer and Spur Path
    public List<LinkedList<OrientVertex>> listA, listB;
    public List<DijkstraResult> listBB;
    public OrientVertex spurNode;
    public ArrayList<OrientVertex> rootPath, pPath;
    public float[] costColect;



    //Constructor 1
    public PathlyYen() {
        pathlyDij = new PathlyDijkstra();
        listA = new ArrayList<LinkedList<OrientVertex>>();
        listB = new ArrayList<LinkedList<OrientVertex>>();
        rootPath = new ArrayList<OrientVertex>();
        pPath = new ArrayList<OrientVertex>();
        listBB = new ArrayList<DijkstraResult>();

//        listBCost = new ArrayList<int>();
//        listACost = new ArrayList<int>();

    }

    /**
     * Yen's algorithm computes single-source K-shortest loopless paths for a graph with non-negative edge cost.
     *
     * @param source      start vertex of path
     * @param destination destination vertex of path
     * @param K           number of shortest path
     */
    public void excute(String source, String destination, Integer K) {

        DijkstraResult dijkstraResult;
        float[] rootPathCost;
        //Step 0: First  Call dijkstra
        dijkstraResult = pathlyDij.executePathlyDij(null, null, null, new Object[]{source, destination, "'distance'", "out"},
                new OBasicCommandContext(), null, new String[]{null});
        rootPathCost = dijkstraResult.getRootPathCost();
        System.out.println("<------- 1st Shortest Path");
        printDijkstraResult(dijkstraResult);
        listA.add(dijkstraResult.getShortestPath());

        for (int round = 1; round <= K; round++) {
            System.out.println(" ##########################################################");
            System.out.println(" ------- Round :" + round + " finding all the deviations(potential KSP)");
            System.out.println(" ##########################################################");

            int listAIden = listA.size() - 1;

            for (int i = 0; i < listA.get(listAIden).size() - 1; i++) {
                System.out.println("\n\n>------ Find Potential :" + i + " ------<");

                spurNode = listA.get(listAIden).get(i);
                System.out.println("--- spurNode : " + spurNode.getIdentity().toString());

                // root path = first node to i
                rootPath.clear();
                if (i > 0) {
                    for (int j = 0; j < i; j++) {
                        rootPath.add(listA.get(listAIden).get(j));
                    }
                }
                System.out.print("--- rootPath :");
                printList(rootPath);
                System.out.println("--- Root Path Cost :" + rootPathCost[i]);

                String firstIngnore = spurNode.getIdentity().toString();

                List<String> secondeIgnoreList = new ArrayList<String>();
                System.out.print("--- KSP use in " + round +" : ");
                printList(listA.get(listAIden));
                System.out.print("\n--- Now ListA ---");
                printLisOfLis(listA);

                // TODO: The way to ignore the links that are part of the previous shortest paths which share the same root path.
                secondeIgnoreList.add(listA.get(listAIden).get(i + 1).getIdentity().toString());
                /////////////////// Not done yet.
                addSecondFromListA(secondeIgnoreList);
                String[] secondeIgnore = secondeIgnoreList.toArray(new String[secondeIgnoreList.size()]);
                System.out.print("--- SecondIgnoreArray  --- ");
                printListString(secondeIgnore);

                // Calculate the spur path from the spur node to the destination.
                System.out.print("--- Parameters for dijkstra -- sorce :" + spurNode.getIdentity().toString()+
                ",dest :" + destination + ",rootIgnorepath :" + firstIngnore + ",ignorepath : ");printListString(secondeIgnore);

                // Need to collect cost collection
                dijkstraResult = pathlyDij.executePathlyDij(null, null, null, new Object[]{spurNode.getIdentity().toString(), destination, "'distance'", "out"},
                        new OBasicCommandContext(), firstIngnore, secondeIgnore);


                System.out.print("--- new SpurPath : ");
                printDijkstraResult(dijkstraResult);

                // Entire path is made up of the root path and spur path.
                LinkedList<OrientVertex> totalPath = new LinkedList<OrientVertex>();

                //Distance between last of rootPath and first of spur path
                float connectDistance = 0;
                if(rootPath.size() != 0) {
                    connectDistance = pathlyDij.getDistance(rootPath.get(rootPath.size() - 1), dijkstraResult.getShortestPath().getFirst());
                }

                System.out.print("--- Distance of connection = "+connectDistance+"\n");
                totalPath.addAll(rootPath);
                totalPath.addAll(dijkstraResult.getShortestPath());

                System.out.print("--- Potential KSP (total path) : ");
                printList(totalPath);
                float totalCost = rootPathCost[i] + dijkstraResult.getTotalCost();
                System.out.println(" (" + totalCost + ")");

                // TODO: Add the potential k-shortest path to the ListB.
                // note. use "Set" because each element can only exists once in a Set.
                listBB.add(new DijkstraResult(totalPath,totalCost));

            }

            // TODO: Sort the potential k-shortest paths by cost. (Sort ListB)

            // TODO: Add the lowest cost path becomes the k-shortest path. (Add to ListA)

//            listA.add(listB.get(1));
            System.out.print("><><><><><>< listA -- ");
            printLisOfLis(listA);

//            listB.remove(1);
            System.out.print("\n><><><><><>< listB -- ");
            printLisOfLis(listB);

        }

    }

    public void addSecondFromListA(List<String> second) {
        if (listA.size() > 1) {

            String firstIgnore = second.get(0);
            System.out.println("\n--- Inside AddSecondIngore : ");
            for (LinkedList<OrientVertex> als : listA) {
                System.out.println("--- first Ingore : " + firstIgnore);
                for (OrientVertex a : als) {
                    if(firstIgnore.equals(a.getIdentity().toString()) && als.indexOf(a) < als.size() - 2 ){
                        System.out.println(" --- In Alist  : " + a.getIdentity().toString());
                        String nextIgnore = als.get(als.indexOf(a) + 1).getIdentity().toString();
                        System.out.println("--- NextIngore : " + nextIgnore);
                        second.add(nextIgnore);
                    }
                }
            }
        }
        System.out.println("");
    }

    public void printList(LinkedList<OrientVertex> ls) {
        if(ls == null){
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
    public void printDijkstraResult(DijkstraResult result){
        LinkedList<OrientVertex> shotestPath  = result.getShortestPath();
        float totalDistance = result.getTotalCost();
        printList(shotestPath);
        System.out.print("(" + totalDistance + ")");
        printArray(result.getRootPathCost());

    }
    public void printArray(float[] fArr){
        System.out.print("{ ");
        for (float f:fArr){
            System.out.print(f + " , ");
        }
        System.out.println("}");

    }

}
