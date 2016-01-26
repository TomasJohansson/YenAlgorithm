package model;

import com.tinkerpop.blueprints.impls.orient.OrientVertex;

import java.util.LinkedList;

/**
 * Created by Tkaewkunha on 1/25/16.
 */
public class DijkstraResult implements Comparable<DijkstraResult> {
    LinkedList<OrientVertex> shortestPath;
    float totalCost;
    float[] rootPathCost;

    public DijkstraResult(LinkedList<OrientVertex> shortestPath, float totalCost) {
        this.shortestPath = shortestPath;
        this.totalCost = totalCost;
    }
    public DijkstraResult(LinkedList<OrientVertex> shortestPath, float totalCost,float[] rootPathCost) {
        this.shortestPath = shortestPath;
        this.totalCost = totalCost;
        this.rootPathCost = rootPathCost;
    }

    public LinkedList<OrientVertex> getShortestPath() {
        return shortestPath;
    }

    public void setShortestPath(LinkedList<OrientVertex> shortestPath) {
        this.shortestPath = shortestPath;
    }

    public float getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(float totalCost) {
        this.totalCost = totalCost;
    }

    public float[] getRootPathCost() {
        return rootPathCost;
    }

    public void setRootPathCost(float[] rootPathCost) {
        this.rootPathCost = rootPathCost;
    }

    /**
     *
     * @param compareDijkstraResult
     * @return Ascending order,
     * Return 1 = more than compareDijkstraResult
     * Return -1 = less than compareDijkstraResult or All of properties is equal but it's not the same path.
     * Retun  0 = equal compareDijkstraResult;
     */
    @Override
    public int compareTo(DijkstraResult compareDijkstraResult) {
        if(totalCost > compareDijkstraResult.totalCost){
           return  1;
        }else if(totalCost < compareDijkstraResult.totalCost){
            return -1;
        }else{
            if(this.getShortestPath().size() > compareDijkstraResult.getShortestPath().size()){
                return  1;
            }else if(this.getShortestPath().size() < compareDijkstraResult.getShortestPath().size()){
                return -1;
            }else{
                if(this.comparePath(compareDijkstraResult)){
                    return 0;
                }else{
                    return -1;
                }
            }
        }
    }
    public boolean comparePath(DijkstraResult dijkstraResult){
        for (int i = 0 ;i<this.getShortestPath().size();i++){
            if(!(this.getShortestPath().get(i).getIdentity().toString().equals(
                    dijkstraResult.getShortestPath().get(i).getIdentity().toString()))){
                return false;
            }
        }

        return true;
    }
}
