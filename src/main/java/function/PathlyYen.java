package function;

import com.orientechnologies.orient.core.Orient;
import com.orientechnologies.orient.core.command.OBasicCommandContext;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Tkaewkunha on 1/24/16.
 */
public class PathlyYen {
    public PathlyDijkstra pathlyDij;
    //list A & B for keeping K Shortest Path answer and Spur Path
    public List<LinkedList<OrientVertex>> listA,listB;
    public OrientVertex spurNode;
    public ArrayList<OrientVertex> rootPath,pPath;
    public List<String> listBCost,listACost;
    //Constructor 1
    public PathlyYen(){
        pathlyDij = new PathlyDijkstra();
        listA = new ArrayList<LinkedList<OrientVertex>>();
        listB = new ArrayList<LinkedList<OrientVertex>>();
        rootPath = new ArrayList<OrientVertex>();
        pPath = new ArrayList<OrientVertex>();
        listBCost = new ArrayList<String>();
        listACost = new ArrayList<String>();

    }
    //Source, Destination and K order
    public void excute(String source,String destination,Integer K){

        LinkedList<OrientVertex> lsDij = new LinkedList<OrientVertex>();
        //Step 0: First  Call dijstra
        lsDij =  pathlyDij.executePathlyDij(null,null,null,new Object[] { source,destination, "'distance'", "out" },
                new OBasicCommandContext(),null, new String[] {null});
        this.printList(lsDij);
        System.out.println("<------- Step :0 ");
        listA.add(lsDij);

        for(int round = 1; round <= K; round++){
            System.out.println("<------- Step :" + round);

            int listAIden = listA.size()-1;

            for(int i = 0;i < listA.get(listAIden).size() - 1 ;i++){
                System.out.println(">------ Round :" + i);

                spurNode = listA.get(listAIden).get(i);
                System.out.println("-- spurNode : " + spurNode.getIdentity().toString());

                rootPath.clear();
                if(i > 0){
                    for(int j = 0;j < i;j++){
                        rootPath.add(listA.get(listAIden).get(j));
                    }
                }
                System.out.print("-- rootPath --");
                printList(rootPath);

                spurNode.getIdentity().toString();
                String firstIngnore = spurNode.getIdentity().toString();

                List<String> secondeIgnoreList = new ArrayList<String>();
                System.out.print("--- ListA ---");
                printLisOfLis(listA);
                System.out.print("--- Inside ListA at index Iden ---");
                printList(listA.get(listAIden));

                secondeIgnoreList.add(listA.get(listAIden).get(i + 1).getIdentity().toString());


                /////////////////// Not done yet.
                addSecondFromListA(secondeIgnoreList);

                String[] secondeIgnore = secondeIgnoreList.toArray(new String[secondeIgnoreList.size()]);
                System.out.print("--- SecondIgnoreArray  --- ");
                printListString(secondeIgnore);

                lsDij =  pathlyDij.executePathlyDij(null,null,null,new Object[] { spurNode.getIdentity().toString(),destination, "'distance'", "out" },
                        new OBasicCommandContext(),firstIngnore, secondeIgnore);
                System.out.print("--- new DijkStra : ");
                printList(lsDij);

                LinkedList<OrientVertex> totalShortestPath = new LinkedList<OrientVertex>();
                totalShortestPath.addAll(rootPath);
                totalShortestPath.addAll(lsDij);

                System.out.print("--- totalShortestPath : ");
                printList(totalShortestPath);

                listB.add(totalShortestPath);

                System.out.print("--- ListB : ");
                printLisOfLis(listB);




//                pPath.addAll(lsDij);
//                System.out.println("-- rootPath --");
//                printList(pPath);

            }
            listA.add(listB.get(1));
            System.out.print("><><><><><>< listA -- ");
            printLisOfLis(listA);

            listB.remove(1);
            System.out.print("><><><><><>< listB -- ");
            printLisOfLis(listB);

        }

    }

    public void addSecondFromListA(List<String> second){
            if(listA.size()>1){

                for (LinkedList<OrientVertex> als:listA){
                    for (OrientVertex a:als){

                    }
                }
            }
    }
    public void printList(LinkedList<OrientVertex> ls){
        for(OrientVertex v:ls){
            System.out.print(v.getIdentity().toString() + ",");
        }
        System.out.println();
    }
    public void printList(ArrayList<OrientVertex> ls){
        for(OrientVertex v:ls){
            System.out.print(v.getIdentity().toString() + ",");
        }
        System.out.println();
    }
    public void printListString(List<String> ls){
        for(String s:ls){
            System.out.print(s + ",");
        }
        System.out.println();
    }
    public void printListString(String[] ar){
        for(String s:ar){
            System.out.print(s + ",");
        }
        System.out.println();
    }
    public void printLisOfLis(List<LinkedList<OrientVertex>> lisoflis){
        System.out.print("[");

        for (LinkedList<OrientVertex> ls: lisoflis){
            System.out.print("{");
            for (OrientVertex v : ls){
                System.out.print(v.getIdentity().toString()+ ", ");
            }
            System.out.print("} , ");

        }
        System.out.println("]");

    }
}
