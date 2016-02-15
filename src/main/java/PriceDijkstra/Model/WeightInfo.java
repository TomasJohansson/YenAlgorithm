package PriceDijkstra.Model;

/**
 * Created by Tkaewkunha on 2/8/16.
 */
public class WeightInfo {
    public Float weight;
    public String start;
    public String end;
    public String transRid;
    public Float totalWeight;

    public Float getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(Float totalWeight) {
        this.totalWeight = totalWeight;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getTransRid() {
        return transRid;
    }

    public void setTransRid(String transRid) {
        this.transRid = transRid;
    }

    @Override
    public String toString() {
        return "Trans :" + transRid + ", start :" + start + ", end :" + end + ",weight :"+weight+", totalWeight:" + totalWeight +"\n";
    }
}
