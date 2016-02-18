package PriceDijkstra; /**
 * Created by Tkaewkunha on 2/4/16.
 */
import java.util.*;

import PriceDijkstra.Model.*;
import com.orientechnologies.orient.core.db.record.OTrackedList;
import com.orientechnologies.orient.core.iterator.OEmptyIterator;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientElement;
import com.tinkerpop.blueprints.impls.orient.OrientElementIterable;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

public class DijkstraExcl {

    private Iterator<Vertex> vertici;
    private OrientGraph g;          //grafh DB
    private Set<String> visited;          //visited rids
    private Set<String> toVisits;          //to visit rids
    private Map<String, Float> VertexWeight;          //VertexWeight(i)     < @rid, weight_to_get_to_@rid >
    private Map<String, String> childAndParent;          //childAndParent(i)     < @rid, previous_node_in_the_shortest_path >
    private String eClass;     //edge class to use
    private List<WeightInfo> weightInfoList;
    private Vertex startV;
    private Map<String ,HashSet<TransInfo>> transUnfixedPassed;
    private Map<String ,HashSet<TransInfo>> transFixedPassed;
    private Map<String ,Map<String ,String>> tranFixedFromTo;
    private Map<String ,Map<String ,String>> transUnfixedFromto;
    private List<Ignore> ignorels ;

    //

    public DijkstraExcl(OrientGraph g, String eClass,Iterable<Vertex> vertices) {
        this.g = g;
        this.eClass = eClass;
        visited = new HashSet<String>();
        toVisits = new HashSet<String>();
        VertexWeight = new HashMap<String, Float>();
        childAndParent = new HashMap<String, String>();
        weightInfoList = new ArrayList<WeightInfo>();
        transUnfixedPassed = new HashMap<String ,HashSet<TransInfo>>();
        transFixedPassed = new HashMap<String ,HashSet<TransInfo>>();
//        transFixedPassed = new HashMap<String ,HashSet<TransFixedInfo>>();

        tranFixedFromTo = new HashMap<String, Map<String, String>>();
        transUnfixedFromto = new HashMap<String, Map<String, String>>();
        ignorels = new ArrayList<Ignore>();
        this.vertici = vertices.iterator();

    }
    private void findPath(Vertex startV, Vertex endV, Direction dir) {

        //init
        visited.clear(); //visited rids
        toVisits.clear();
        VertexWeight.clear();
        childAndParent.clear();
        weightInfoList.clear();
        transUnfixedPassed.clear();
//        transFixedPassed.clear();
        this.startV = startV;
        tranFixedFromTo.clear();
        transUnfixedFromto.clear();
        System.out.print("\n ---------------------  Start Step1  --------------------- \n");

        //step1
//        Iterator<Vertex> vertici = g.getVertices().iterator();
        while (vertici.hasNext()) {
            Vertex ver = vertici.next();
            VertexWeight.put(ver.getId().toString(), Float.MAX_VALUE);
            toVisits.add(ver.getId().toString());
        }
        VertexWeight.put(startV.getId().toString(), 0f);        //VertexWeight(startV) = 0

//        System.out.println("--- VertexWeight Set StartV to 0");
//        printMap(VertexWeight);

        childAndParent.put(startV.getId().toString(), null);     //childAndParent(startV) = null
        //เพิ่มสายรถ ขอ  step 1;
        toVisits.remove(startV.getId().toString());        //startV visited => removed from toVisits
        visited.add(startV.getId().toString());           //and added in visited


        Iterator<Vertex> neighbors = startV.getVertices(dir, eClass).iterator();

        while (neighbors.hasNext()) {
            Vertex vicino = neighbors.next();
            WeightInfo initWeightInfo = calculateWeight(startV.getId().toString(),vicino.getId().toString(),dir );
            VertexWeight.put(vicino.getId().toString(), initWeightInfo.getWeight());     //VertexWeight(i) = VertexWeight(startV, i)
            childAndParent.put(vicino.getId().toString(), startV.getId().toString());
            weightInfoList.add(initWeightInfo);
        }
        System.out.println("VertexWeight :");
        printMap2(VertexWeight);
        System.out.println("Children And parent :");
        printMap2(childAndParent);
        System.out.println("weightInfoList :");
        printWeightInfoList(weightInfoList);
        System.out.println("transFixedPassed :");

        System.out.println("transUnfixedPassed :");

        System.out.print("\n ======================= End Step1 ======================= ");
        System.out.print("\n ---------------------  Start Step2  --------------------- \n");

        //step2

        Boolean continuos = false;
        Iterator<String> itrToVisits = toVisits.iterator();
        while (itrToVisits.hasNext()) {
            String strToVisit = itrToVisits.next();
            if (VertexWeight.get(strToVisit) != Float.MAX_VALUE) {
                System.out.println("\n--- toVisit : " + strToVisit);
                continuos = true;
                break;
            }
        }
        while (continuos) {
            String newVisited = startV.getId().toString();
            Float wieght = Float.MAX_VALUE;
            System.out.println("\n\n<><><><>find new Visited<><><><>");
            System.out.println("toVisit :");
            printSetString(toVisits);
            System.out.println("visited :");
            printSetString(visited);

            System.out.println("find NewVisite From Lowest Weight :");

            itrToVisits = toVisits.iterator();
            while (itrToVisits.hasNext()) {
                String nextToVisit = itrToVisits.next();
                //Find smallest weight
                if (VertexWeight.get(nextToVisit) <= wieght) {
//                    System.out.println("--- Weight : " + wieght +" - vs - \nVertexWeight.get(nextToVisit): " + VertexWeight.get(nextToVisit));
                    wieght = VertexWeight.get(nextToVisit);
                    System.out.println(" ------------- Set new Visited  : " + nextToVisit );
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

            System.out.print("\n==========================  Start Step3  =========================\n \n");
            //step3
            System.out.println("Visited New : " + newVisited);

            neighbors = g.getVertex(newVisited).getVertices(dir, eClass).iterator();

            while (neighbors.hasNext()) {
                Vertex vertexNeighbor = neighbors.next();
                String neighbor = vertexNeighbor.getId().toString();
                System.out.println("Neighbor of the Visited : " + neighbor);

                WeightInfo newVisitToNeighborWeightInfo = calculateWeight(newVisited,neighbor,dir);
                Float newVisitToNeigborWeight = newVisitToNeighborWeightInfo.getWeight();
                System.out.println("VertexWeight.get(neighbor)  : " + VertexWeight.get(neighbor) + "\n");
                System.out.println("VertexWeight.get(newVisited)  : " + VertexWeight.get(newVisited) + "+");
                System.out.println("newVisitToNeigborWeight  : " + newVisitToNeigborWeight);


                if ((toVisits.contains(neighbor)) && (VertexWeight.get(neighbor) > (VertexWeight.get(newVisited) + newVisitToNeigborWeight))) {
                    System.out.println("  *--- Inside LongCondition  :");

                    System.out.println("  --- newVisitedToNeighbor : " + newVisitToNeigborWeight);
                    System.out.println(" --- VertexWeight  :");
                    printMap2(VertexWeight);
                    System.out.println(" --- Children And parent  :");
                    printMap2(childAndParent);

                    if (newVisitToNeigborWeight == Integer.MAX_VALUE) {
                        VertexWeight.put(neighbor, Float.MAX_VALUE);
                    } else {
                        System.out.println("\n  *** VertexWeight.Put key neighbor :"+neighbor+" " +
                                ", value  VertexWeight.get(newVisited) + newVisitedToNeighbor : "
                                + (VertexWeight.get(newVisited) + newVisitToNeigborWeight) + "\n");

                        VertexWeight.put(neighbor, (VertexWeight.get(newVisited) + newVisitToNeigborWeight));
                    }
                    //Important!!!
                    System.out.println("\n  *** childAndParent.put child :" + newVisitToNeighborWeightInfo.getEnd()+" parent : " + newVisitToNeighborWeightInfo.getStart() + "\n");

                    childAndParent.put(newVisitToNeighborWeightInfo.getEnd(), newVisitToNeighborWeightInfo.getStart());
                    //Add edge's rid and Transportation rid here
                    weightInfoList.add(newVisitToNeighborWeightInfo);
                    System.out.println("VertexWeight  :");
                    printMap2(VertexWeight);
                    System.out.println("Children And parent  :");
                    printMap2(childAndParent);
                    System.out.println("weightInfoList :");
                    printWeightInfoList(weightInfoList);
                }

            }

            //shall we continue?
            continuos = false;
            System.out.println("\n --- countinuos : " + continuos);
            itrToVisits = toVisits.iterator();
            while (itrToVisits.hasNext()) {
                String toVisit = itrToVisits.next();
                if (VertexWeight.get(toVisit) != Float.MAX_VALUE) {
                    System.out.println("tovisit : " + toVisit + "VertexWeight.get(toVisit) : " + VertexWeight.get(toVisit));
                    continuos = true;

                }
            }
        }
    }
    private WeightInfo calculateWeight(String start, String end, Direction dir) {
        WeightInfo newNeigborWieghtInfo = new WeightInfo();
        newNeigborWieghtInfo.setWeight(Float.MAX_VALUE);
        newNeigborWieghtInfo.setStart(start);
        newNeigborWieghtInfo.setEnd(end);
        newNeigborWieghtInfo.setTotalWeight(Float.MAX_VALUE);

        String endId = end;
        end = "v(Station)[" + end + "]";

        System.out.println("\n### --- calculateWeigh2() --- ###");
        System.out.println("Parent : " + start);
        System.out.println("Children : " + end);
        System.out.println("Trans ignore : ");

        Vertex parentVertex = g.getVertex(start);
        Iterator<Edge> parentEdge = parentVertex.getEdges(dir, eClass).iterator();
        Set<Edge> excludeIgnoredEdge = new HashSet<Edge>();
        while (parentEdge.hasNext()) {
            Edge e = parentEdge.next();
            if ((e.getProperty("out").toString().equals(end) || e.getProperty("in").toString().equals(end))) {
                excludeIgnoredEdge.add(e);
            }
        }

        Iterator<Edge> useEdges = excludeIgnoredEdge.iterator();
        WeightInfo bestWieghtInfoEachEdge;
        while (useEdges.hasNext()) {
            Edge edge = useEdges.next();
            //Prop wieght must be price , Find best price from edge @rid and compare all Transportation's price
            //Find Previous direction
            // Then should make ignoring some Transportation here if have to.
            bestWieghtInfoEachEdge = getBestPrice(edge,start,endId);
            if (bestWieghtInfoEachEdge.getWeight() < newNeigborWieghtInfo.getWeight()) {
                newNeigborWieghtInfo = bestWieghtInfoEachEdge;
                System.out.println(" *** Change Weight to: " + newNeigborWieghtInfo.weight + "---\n");
            }

        }
        //before return the weight have to transportation RID and Path RID

        System.out.println("//End of calculateWieght :" + newNeigborWieghtInfo.weight +" info :" +newNeigborWieghtInfo);
        return newNeigborWieghtInfo;
    }
    public WeightInfo getBestPrice(Edge edge, String start, String end){
        WeightInfo currentWeightInfo = new WeightInfo();
        currentWeightInfo.setStart(start);
        currentWeightInfo.setEnd(end);
        currentWeightInfo.setWeight(Float.MAX_VALUE);

        System.out.println("--- getBestPrice");
//        System.out.println("edge :"+ edge.getId().toString());
        System.out.println("Start :" + start);
        System.out.println("End :" + end);

        OrientElementIterable trans = edge.getProperty("transportations");
        Iterator<OrientElement> transI = trans.iterator();
        Set<OrientElement> excludeIgnoredStran = new HashSet<OrientElement>();

        //Here to Ignore
        while(transI.hasNext()){
            OrientElement tr = transI.next();
            if(!checkIgnore(start,end,tr.getId().toString())){
                excludeIgnoredStran.add(tr);
            }else{
                System.out.println("Ignored :" +"start " + start + "end " + end + "transRid :" + tr.getId());
            }
//            excludeIgnoredStran.add(tr);

        }

        System.out.println("Excluded IgnoreTrans : "+excludeIgnoredStran);
        Iterator<OrientElement> useTran = excludeIgnoredStran.iterator();

        while(useTran.hasNext()){
            OrientElement currentTran = useTran.next();
            System.out.println("Using Trans -- " + currentTran.getIdentity().toString());
            WeightInfo prevWi = getPreviousWeightInfo(start);
            if(prevWi == null){ //In this case when used by step 1
                System.out.println("*** Step Init");

                Float price = getInitPrice(currentTran,start,end);
                if(price < currentWeightInfo.getWeight()) {
                    currentWeightInfo.setWeight(price);
                    currentWeightInfo.setTotalWeight(price);
                    currentWeightInfo.setTransRid(currentTran.getId().toString());
                }
            }else{
                System.out.println("*** Does Not Step Init");
                //หา Rid ขอ Path ล่าสุดออกมา เพื่อเช็คว่าเป็นรถสายเดียวกันหรือไม่
                WeightInfo newWeightInfo = getStepPrice(prevWi,currentTran,start,end);
                if(newWeightInfo.getWeight() < currentWeightInfo.getWeight()){
                    currentWeightInfo = newWeightInfo;
                    currentWeightInfo.setTransRid(newWeightInfo.getTransRid());
                }

            }
        }

        System.out.println("Current WeightInfo before return :" + currentWeightInfo);
        System.out.println("");
        return currentWeightInfo;
    }

    private boolean checkIgnore(String start, String end, String transRid) {
//        System.out.println("--- checkIgnore");
//        System.out.println("start:" + start);
//        System.out.println("end:" + end);
//        System.out.println("transRid:" + transRid);


        for (Ignore ig:ignorels){
//            System.out.println("ig:" + ig);
            if( ig.getTransRid().equals(transRid)
                    && ig.getStart().equals(start)
                    && ig.getEnd().equals(end)){
//                System.out.println("ig : " + ig);
//                System.out.println("return True:");
                return true;
            }
        }
//        System.out.println("return False:");

        return false;
    }

    private WeightInfo getStepPrice(WeightInfo prevWeightInfo , OrientElement currentTran, String start, String end){
        System.out.println("--- getStepPrice");
        System.out.println("CurrentTran :" + currentTran.getId().toString());

        WeightInfo wi = new WeightInfo();
        wi.setTransRid(currentTran.getId().toString());
        wi.setWeight(Float.MAX_VALUE);
        wi.setTotalWeight(Float.MAX_VALUE);
        wi.setStart(start);
        wi.setEnd(end);
        OTrackedList priceRateOTlist = currentTran.getProperty("priceRate");

        if(prevWeightInfo.getTransRid().equals(currentTran.getId().toString())){
            System.out.println(" *** This is the same Trans");
            if(currentTran.getProperty("priceType").toString().equals("unfixed")){
                System.out.println("unfixed");
                //for explain result
                addTranUnfixedFixedFromTo(currentTran.getId().toString(),start,end);
                Iterator<ODocument> priceRateI = priceRateOTlist.iterator();

                List<ODocument> listPriceRate = new ArrayList<ODocument>();
                priceRateI.forEachRemaining(listPriceRate::add);
                for(ODocument eachPriceRate:listPriceRate){
                    ODocument startDoc = eachPriceRate.field("start");
                    ODocument endDoc = eachPriceRate.field("end");
                    String from = startDoc.getIdentity().toString();
                    String to = endDoc.getIdentity().toString();
                    if(start.equals(from) && end.equals(to)){
                        Float fare = Float.parseFloat(eachPriceRate.field("fare").toString());
                        if(fare < wi.getWeight()){
                            wi.setWeight(fare);
                            wi.setTotalWeight(fare+prevWeightInfo.getTotalWeight());
                            wi.setTransRid(currentTran.getId().toString());
                        }
                        System.out.println("prev :" + prevWeightInfo);
                        Float prevToEnd = getPrevToEndPrice(listPriceRate,prevWeightInfo.getStart(),end);
//                        หา จาก prevWeightInfo.getStart() to end;
                        Float prevToEndFareAndTotalOfFromWeight = getFareFromTransUnfixedPassed(currentTran.getId().toString(),prevWeightInfo.getStart(),end);
                        Float subWeight = prevToEndFareAndTotalOfFromWeight - prevWeightInfo.getTotalWeight();
                        if(subWeight < wi.getWeight()){
                            wi.setWeight(subWeight);
                            wi.setTotalWeight(subWeight+prevWeightInfo.getTotalWeight());
                            wi.setTransRid(currentTran.getId().toString());

                        }
                    }
                }

            }else{
                System.out.println("fixed");
                //for explain result
                addTranFixedFromTo(currentTran.getId().toString(),start,end);
                //Just Add addTransFixedPassed
                Iterator<ODocument> priceRateI = priceRateOTlist.iterator();
                while(priceRateI.hasNext()){
                    ODocument doc = priceRateI.next();
                    Float fare = Float.parseFloat(doc.field("fare").toString());

                    addTransFixedPassed(currentTran.getId().toString(),start,end,fare);
                    //
//                    addTransFixedPassed(currentTran.getId().toString(),start,fare);
//                    addTransFixedPassed(currentTran.getId().toString(),end,fare);

                }

                wi.setWeight(0f);
                wi.setTotalWeight(prevWeightInfo.getTotalWeight());
                wi.setTransRid(currentTran.getId().toString());

            }
        }else{
            System.out.println(" *** No. This is not the same Trans");
            System.out.println("VertexWeight :");
            printMap2(VertexWeight);
            System.out.println("Children And parent :");
            printMap2(childAndParent);
            if(currentTran.getProperty("priceType").toString().equals("unfixed")){
                System.out.println("unfixed");
                //for explain result
                addTranUnfixedFixedFromTo(currentTran.getId().toString(),start,end);
                Iterator<ODocument> priceRateI = priceRateOTlist.iterator();
                //Add to passed
                if(!transUnfixedPassed.containsKey(currentTran.getId().toString())){
                    addTransUnfixedPassed(currentTran.getId().toString(),priceRateI);
                }
                System.out.println("TransUnFixedPassed of " +currentTran.getId().toString() + transUnfixedPassed.get(currentTran.getId().toString()));
                WeightInfo betterWeightInfo = compairUnfixedTransToChild(currentTran.getId().toString(),start,end);
                if(betterWeightInfo.getWeight() < wi.getWeight()){
                    wi = betterWeightInfo;
                    wi.setTransRid(currentTran.getId().toString());
                    System.out.println(" --- getStepPrice wi change before return :" + wi);
                }
                //Check From Children adn parent

            }else{
                System.out.println("fixed");
                //for explian result
                addTranFixedFromTo(currentTran.getId().toString(),start,end);
                Iterator<ODocument> priceRateI = priceRateOTlist.iterator();
                while(priceRateI.hasNext()){
                    ODocument doc = priceRateI.next();
                    Float fare = Float.parseFloat(doc.field("fare").toString());

                    addTransFixedPassed(currentTran.getId().toString(),start,end,fare);

//                    addTransFixedPassed(currentTran.getId().toString(),start,fare);
//                    addTransFixedPassed(currentTran.getId().toString(),end,fare);
                }

                System.out.println("TransFixedPassed of " +currentTran.getId().toString() + transFixedPassed.get(currentTran.getId().toString()));
                //Check from Childred and Parent
                WeightInfo betterWeightInfo = compairFixedTransToChild(currentTran.getId().toString(),start,end);
                if(betterWeightInfo.getWeight() < wi.getWeight()){
                    wi = betterWeightInfo;
                    wi.setTransRid(currentTran.getId().toString());
                    System.out.println(" --- getStepPrice wi change before return :" + wi);
                }
            }

        }
        System.out.println("--- getStepPrice wi return :" + wi);
        return wi;
    }
    private Float getFareFromTransUnfixedPassed(String transRid,String from,String to){
        System.out.println("--- getFareFromTransUnfixedPassed" );

        HashSet<TransInfo> transInfos = transUnfixedPassed.get(transRid);
        for(TransInfo transInfo : transInfos){
            if(transInfo.getFrom().equals(from)&& transInfo.getTo().equals(to)){
                System.out.println("From :" + from +",to:" + to+",fare :" + transInfo.getFare());
                return transInfo.getFare() + VertexWeight.get(from);
            }
        }
        return Float.MAX_VALUE;
    }
    private WeightInfo compairUnfixedTransToChild(String transRid,String currentVisited,String neighbor){
        System.out.println(" --- compairUnfixedTransToChild");
        WeightInfo wi = new WeightInfo();
        wi.setTransRid(transRid);
        wi.setEnd(neighbor);
        wi.setWeight(Float.MAX_VALUE);
        wi.setTotalWeight(Float.MAX_VALUE);

        System.out.println("transUnfixedPassed of " +transRid + transUnfixedPassed.get(transRid));

        HashSet<TransInfo> transUnFixedInfos = transUnfixedPassed.get(transRid);
        Set<String> allChild = childAndParent.keySet();

        System.out.println("transUnFixedInfos :" + transUnFixedInfos);
        System.out.println("allChild :" + allChild);

        for (TransInfo fromToFare : transUnFixedInfos){
            //มีปลายทางเป็นตัวที่กำลังหา (Neighbor)
            if(fromToFare.getTo().equals(neighbor)){
                //ต้นทางเป็น Child ตัวใดก็ได้
                if(allChild.contains(fromToFare.getFrom())){
                    Float totalWeight = VertexWeight.get(fromToFare.getFrom()) + fromToFare.getFare();
                   // คัดเลือก Weight จากต้นทางไปถึงปลายทางที่น้อยที่สุด
                    if(totalWeight < wi.getTotalWeight()){
                        wi.setTotalWeight(totalWeight);
                        wi.setStart(fromToFare.getFrom());
                        System.out.println(" *** Change TotalWeight :" + wi.getTotalWeight() + ",start(Child):" + wi.getStart() + ",end :" + wi.getEnd());
                    }
                }
            }
        }
        wi.setWeight(wi.getTotalWeight() - VertexWeight.get(currentVisited));
        System.out.println(" --- compairUnfixedTransToChild retrun" + wi);

        return wi;
    }
    private WeightInfo compairFixedTransToChild(String transRid,String currentVisited,String neighbor){
        System.out.println(" --- CompairFixedTransToChild");
        WeightInfo wi = new WeightInfo();
        wi.setTransRid(transRid);
        wi.setEnd(neighbor);
        wi.setWeight(Float.MAX_VALUE);
        wi.setTotalWeight(Float.MAX_VALUE);

        HashSet<TransInfo> transFixedInfos = transFixedPassed.get(transRid);
        Set<String> allChild = childAndParent.keySet();
        System.out.println("transFixedPassed of " +transRid + transFixedInfos);
        System.out.println("allChild :" + allChild);
        for (TransInfo fromToFare : transFixedInfos){
            //มีปลายทางเป็นตัวที่กำลังหา (Neighbor)
            if(fromToFare.getTo().equals(neighbor)){
                //ต้นทางเป็น Child ตัวใดก็ได้
                if(allChild.contains(fromToFare.getFrom())){
                    Float totalWeight = VertexWeight.get(fromToFare.getFrom()) + fromToFare.getFare();
                    // คัดเลือก Weight จากต้นทางไปถึงปลายทางที่น้อยที่สุด
                    if(totalWeight < wi.getTotalWeight()){
                        wi.setTotalWeight(totalWeight);
                        wi.setStart(fromToFare.getFrom());
                        System.out.println(" *** Change TotalWeight :" + wi.getTotalWeight() + ",start(Child):" + wi.getStart() + ",end :" + wi.getEnd());
                    }
                }
            }
        }
        //แล้วหา weight ระหว่าง Visited->neightbor ที่จะนำไปเทียบ โดยการนำ  Weight ของ Neighbor ที่ดีที่สุดนั้น ลบกับ Weight ของ currentVisited
        wi.setWeight(wi.getTotalWeight() - VertexWeight.get(currentVisited));
        //อาจจะออกมาเป็นค่าติดลบ หรือค่าที่น้อยมากๆ ได้ ไม่ต้องตกใจ เพราะจะนำเอาไปเทียบในเงื่อนไขต่อไป
        return wi;
    }

    private WeightInfo getPreviousWeightInfo(String visited){
        System.out.println("--- getPreviousWeightInfo visited:" + visited);
        String parent, end;
        List<String> path = new ArrayList<String>();
        List<String> result = new ArrayList<String>();
        WeightInfo prevWeightInfo = new WeightInfo();
        end = visited;
        //for test
        if(end.equals(startV.getId().toString())){
            return null;
        }
        path.add(visited);
        while (!end.equals(startV.getId().toString())) {
            System.out.println("startV : " + startV.getId().toString());
            System.out.println("childAndParent : " + childAndParent);
            parent = childAndParent.get(end);
            System.out.println("End : " + end);
            System.out.println("Parent : " + parent);
            if (parent == null) {
                return null;
            }
            path.add(parent);
            end = parent;
            System.out.println("New End : " + end);
        }
        for (int a = 0, b = path.size() - 1; a < path.size(); a++, b--) {
            result.add(a, path.get(b));
        }
        String prevVisited = result.get(result.size()-2);
        System.out.println("result : " + result);

        prevWeightInfo = getWeightInfo(prevVisited,visited);
        System.out.println("transRid : " + prevWeightInfo.getTransRid()+", start :"+prevWeightInfo.getStart() + ",end :" + prevWeightInfo.end + ", weight :" + prevWeightInfo.getWeight());

        return prevWeightInfo;
    }
    private Float getPrevToEndPrice(List<ODocument> listPriceRate,String prev,String end){
        for(ODocument eachPriceRate:listPriceRate) {
            ODocument startDoc = eachPriceRate.field("start");
            ODocument endDoc = eachPriceRate.field("end");
            String from = startDoc.getIdentity().toString();
            String to = endDoc.getIdentity().toString();
            if(prev.equals(from) && end.equals(to)){
                return Float.parseFloat(eachPriceRate.field("fare").toString());
            }
        }
        return Float.MAX_VALUE;
    }
    private Float getInitPrice(OrientElement currentTran, String start, String end){
//        start = "12:0";
//        end = "12:2";
        System.out.println("--- getInitPrice ");
        System.out.println("currentTran :" + currentTran.getId().toString());


        Float price = Float.MAX_VALUE;
        OTrackedList priceRate = currentTran.getProperty("priceRate");
        System.out.println("PriceRate :" + priceRate);

        if(currentTran.getProperty("priceType").toString().equals("unfixed")){
            //add to passed tran
            System.out.println("unfixed");
            //for explain result
            addTranUnfixedFixedFromTo(currentTran.getId().toString(),start,end);

            Iterator<ODocument> priceRateI = priceRate.iterator();
            if(!transUnfixedPassed.containsKey(currentTran.getId().toString())){
                addTransUnfixedPassed(currentTran.getId().toString(),priceRateI);
            }
//            System.out.println("start :" +start +", end" + end);
            HashSet<TransInfo> transUnFixedInfos = transUnfixedPassed.get(currentTran.getId().toString());
//            System.out.println("transUnFixedInfos :" + transUnFixedInfos);
            for(TransInfo tranif:transUnFixedInfos) {
                if (start.equals(tranif.getFrom()) && end.equals(tranif.getTo()) && tranif.getFare() < price) {
                    price = tranif.getFare();
                }

            }

        }else{
            System.out.println("fixed");
            //for explain result
            addTranFixedFromTo(currentTran.getId().toString(),start,end);
            Iterator<ODocument> priceRateI = priceRate.iterator();
            while(priceRateI.hasNext()){
                ODocument doc = priceRateI.next();
                Float fare = Float.parseFloat(doc.field("fare").toString());
                //
                addTransFixedPassed(currentTran.getId().toString(),start,end,fare);
                //add to passed trans , need to add Start
//                addTransFixedPassed(currentTran.getId().toString(),start,fare);
//                addTransFixedPassed(currentTran.getId().toString(),end,fare);
                if(fare < price){
                    price = fare;
                }
            }

        }
//        System.out.println("price return :" + price);

        return price;
    }
    private void addTranFixedFromTo(String transRid,String from,String to){
        if(!tranFixedFromTo.containsKey(transRid)){
            Map<String ,String > fromto = new HashMap<String, String>();
            fromto.put(from,to);
            tranFixedFromTo.put(transRid,fromto);
        }else{
            Map<String ,String > oldFromto = tranFixedFromTo.get(transRid);
            oldFromto.put(from,to);
            tranFixedFromTo.put(transRid,oldFromto);
        }
    }

    private void addTranUnfixedFixedFromTo(String transRid,String from,String to){
        if(!transUnfixedFromto.containsKey(transRid)){
            Map<String ,String > fromto = new HashMap<String, String>();
            fromto.put(from,to);
            transUnfixedFromto.put(transRid,fromto);
        }else{
            Map<String ,String > oldFromto = transUnfixedFromto.get(transRid);
            oldFromto.put(from,to);
            transUnfixedFromto.put(transRid,oldFromto);
        }

    }
    private void addTransFixedPassed(String transRid, String start, String end, Float fare){
        System.out.println("--- addTransFixedPassed:" + transRid);
//        System.out.println("start :"+start+"End:" + end);

        if(!transFixedPassed.containsKey(transRid)){
            HashSet<TransInfo> transFixedInfos = new HashSet<TransInfo>();
            transFixedInfos.add(new TransInfo(start,end,fare));
//            transFixedPassed.put(transRid,transFixedInfos);
            transFixedPassed.put(transRid, ignoreTran(transFixedInfos,transRid));

        }else{
            HashSet<TransInfo> transFixedInfos = transFixedPassed.get(transRid);
            TransInfo lastInfo = new TransInfo(start,end,fare);
//            System.out.println("before add more:" +transFixedInfos);
            HashSet<TransInfo> transFixedInfotemp = new HashSet<TransInfo>();
            for (TransInfo transInfo : transFixedInfos){
                System.out.println("transInfo:" +transInfo);
                TransInfo info = new TransInfo(transInfo.getFrom(),lastInfo.getFrom(),fare);
                transFixedInfotemp.add(info);
                info = new TransInfo(transInfo.getFrom(),lastInfo.getTo(),fare);
                transFixedInfotemp.add(info);
                info = new TransInfo(transInfo.getTo(),lastInfo.getFrom(),fare);
                transFixedInfotemp.add(info);
                info = new TransInfo(transInfo.getTo(),lastInfo.getTo(),fare);
                transFixedInfotemp.add(info);
            }
            System.out.println("before add last:" +transFixedInfos);
            transFixedInfos.addAll(transFixedInfotemp);
            transFixedInfos.add(lastInfo);
            System.out.println("after add more:" +transFixedInfos);
//            transFixedPassed.put(transRid,transFixedInfos);
            transFixedPassed.put(transRid, ignoreTran(transFixedInfos,transRid));

        }
        System.out.println("transFixedPassed:" +transRid+ transFixedPassed.get(transRid));
    }

    private  void addTransUnfixedPassed(String transRid,Iterator<ODocument> priceRateI){
        HashSet<TransInfo> transInfos = new HashSet<TransInfo>();
        while (priceRateI.hasNext()){
            ODocument doc = priceRateI.next();
            ODocument startDoc = doc.field("start");
            ODocument endDoc = doc.field("end");
            String from = startDoc.getIdentity().toString();
            String to = endDoc.getIdentity().toString();
            Float fare = Float.parseFloat(doc.field("fare").toString());
            transInfos.add(new TransInfo(from,to,fare));
        }

        transUnfixedPassed.put(transRid, ignoreTran(transInfos,transRid));

    }
    private HashSet<TransInfo> ignoreTran(HashSet<TransInfo> trnsinfo, String transRid){
        System.out.println("--- ignoreTran :" + transRid);
        HashSet<TransInfo> transInfos = new HashSet<TransInfo>();
//        System.out.println("trnsinfo  :"+ trnsinfo );

        for(Ignore ig :ignorels){
            if(ig.getTransRid().equals(transRid)){
//                System.out.println("same Trans :" + ig);
                for(TransInfo transUnIf :trnsinfo){
                    if(!transUnIf.getFrom().equals(ig.getStart())){
                        transInfos.add(transUnIf);
                    }
                }
                System.out.println("transInfos Return (ignored) :"+ transInfos );
                return transInfos;
            }
        }
        System.out.println("transInfos Return (noignored) :"+ trnsinfo );
        return  trnsinfo;
    }

    private WeightInfo getWeightInfo(String start,String end){
        System.out.println("--- getWeightInfo");
        System.out.println("weightInfoList :" + weightInfoList);
        System.out.println("start :" + start +",end :" + end );
        for(WeightInfo wi : weightInfoList){
            if(wi.getStart().equals(start) && wi.getEnd().equals(end)){
                return wi;
            }
        }
        return null;
    }
    public List<ResultPathly> getPath(Vertex startV, Vertex endV, Direction dir,ArrayList<Ignore> ignorels) {
        this.ignorels.clear();
        this.ignorels = ignorels;

        String parent, end;
        List<Vertex> result = new ArrayList<Vertex>();
        List<WeightInfo> resultDetail = new ArrayList<WeightInfo>();
        List<Vertex> path = new ArrayList<Vertex>();

        findPath(startV, endV, dir);

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
//            System.out.println("Parent : " + parent);
//            System.out.println("End : " + end);

            if (parent == null) {
                return null;
            }

            path.add(g.getVertex(parent));
            end = parent;
//            System.out.println(" --- ");
//            System.out.println("End : " + end);

        }
        System.out.println(path);
        for (int a = 0, b = path.size() - 1; a < path.size(); a++, b--) {
            result.add(a, path.get(b));
        }
        for(int i = 0;i<result.size() -1;i++){
            resultDetail.add(getWeightInfo(result.get(i).getId().toString(),result.get(i+1).getId().toString()));
        }
        System.out.println("ignorels :");
        System.out.println(ignorels);
        System.out.println("result : ");
        System.out.println(result);
        System.out.println("result Detail: ");
        System.out.println(resultDetail);
        if(resultDetail.get(resultDetail.size() -1).getTotalWeight() >= Float.MAX_VALUE){
            return null;
        }
        List<ResultPathly> resultPathly = explainPath(resultDetail);

        return resultPathly;
    }
    private List<ResultPathly> explainPath(List<WeightInfo> result){
        System.out.println("--- Explain Path");
        List<ResultPathly> explainResult = new ArrayList<ResultPathly>();
        for(WeightInfo wi:result){
            if(transUnfixedPassed.containsKey(wi.getTransRid())){
                /// Unfixed
                Map<String,String> fromto = transUnfixedFromto.get(wi.getTransRid());
//                System.out.println("tranRid:"+wi.getTransRid());
                String start = wi.getStart();
                if(explainResult.size() <= 0){
//                    System.out.println("ตัวเริ่ม");
                    explainResult.add(new ResultPathly(start,wi.getTransRid(),"unfixed",0f));
                }

                ResultPathly resultStart = getLastWiByTransRid(explainResult,wi.getTransRid());
//                System.out.println("stationStart :" + resultStart.getPassed());
                while(!start.equals(wi.getEnd())){
                    String end = fromto.get(start);
                    //
                    HashSet<TransInfo> transUnFixedInfos = transUnfixedPassed.get(wi.getTransRid());
                    Float fare = null;
                    for(TransInfo transInfo :transUnFixedInfos){
                        if(transInfo.getFrom().equals(resultStart.getPassed()) && transInfo.getTo().equals(end)){
                            fare = transInfo.getFare();
                        }
                    }
                    explainResult.add(new ResultPathly(end,wi.getTransRid(),"unfixed",
                            resultStart.getWeight() +fare ));

                    start = end;
                }

            }else{
//                System.out.println("Fixed:");
//                System.out.println("tranRid:"+wi.getTransRid());
                //Fixed
                Map<String,String> fromto = tranFixedFromTo.get(wi.getTransRid());
                //Add to Result
//                System.out.println("fromto :");
                System.out.println(fromto);

                String start = wi.getStart();
                HashSet<TransInfo> transFixedInfoset = transFixedPassed.get(wi.getTransRid());
                Iterator<TransInfo> transFixedInfoIterator = transFixedInfoset.iterator();
                TransInfo tranFixedinfo2 = transFixedInfoIterator.next();

                //ตัวเริ่มต้น
                if(explainResult.size() <= 0){
//                    System.out.println("ตัวเริ่ม");
                    explainResult.add(new ResultPathly(start,wi.getTransRid(),"fixed",tranFixedinfo2.getFare()));
                }
//                System.out.println("start :");
//                System.out.println(start);
                while(!start.equals(wi.getEnd())){
                    String end = fromto.get(start);
//                    System.out.println("explainedResult :");
//                    System.out.println(explainResult);
                    //ถ้าเป็นตัวเดียวกันใส่ 0
                    if(explainResult.get(explainResult.size()-1).getTransRid().equals(wi.getTransRid())){
//                        System.out.println("ตัวเดียวกัน");
                        explainResult.add(new ResultPathly(end,wi.getTransRid(),"fixed",
                                0f + explainResult.get(explainResult.size()-1).getWeight()));

                    }else{
//                        System.out.println("ไม่ใชตัวเดียวกัน");
                        explainResult.add(new ResultPathly(end,wi.getTransRid(),"fixed",
                                explainResult.get(explainResult.size()-1).getWeight() + tranFixedinfo2.getFare()));
                    }

                    start = end;
                }
                //set ExplainResult ตะวแรกให้ weight เป็น 0
            }

        }
        explainResult.get(0).setWeight(0f);
        return explainResult;
    }
    private ResultPathly getLastWiByTransRid(List<ResultPathly> listResult,String transRid){
//        System.out.println("--- getLastWiByTransRid ");

        for(int i = 0;i<listResult.size();i++){
//            System.out.println("--- listResult(i) :" + listResult.get(i));

            if(listResult.get(i).getTransRid().equals(transRid)){
               if(i==0){
                   return listResult.get(i);
               }else{
                   return listResult.get(i-1);
               }
           }
       }
        return listResult.get(listResult.size() - 1);
    }
    private void printMap2(Map map) {
        System.out.println(map);
    }
    private void printWeightInfoList(List<WeightInfo> ls){
        for(WeightInfo wi : ls){
            printWeightInfo(wi);
        }
        System.out.println("");

    }
    private void printWeightInfo(WeightInfo wi){
        System.out.print(" trans rid :" + wi.getTransRid());
        System.out.print(" start :" + wi.getStart());
        System.out.print(" end :" + wi.getEnd());
        System.out.print(" weight :" + wi.getWeight().toString());
        System.out.println("");
    }
    private void printSetString(Set<String> stringSet){
        for(String string:stringSet){
            System.out.print(string+  " ,");
        }
        System.out.println();
    }
}
