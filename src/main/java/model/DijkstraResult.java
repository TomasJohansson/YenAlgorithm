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

    @Override
    public int compareTo(DijkstraResult dijkstraResult) {
        //Return 1 = add at Last;
        //Return -1 = add (Last-1);
        //Retun 0 no add;
        if(totalCost > dijkstraResult.totalCost){
           return  1;
        }else if(totalCost < dijkstraResult.totalCost){
            return -1;
        }else{
            if(this.getShortestPath().size() > dijkstraResult.getShortestPath().size()){
                return  1;
            }else if(this.getShortestPath().size() < dijkstraResult.getShortestPath().size()){
                return -1;
            }else{
                if(this.comparePath(dijkstraResult)){
                    return 0;
                }else{
                    return 1;
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
