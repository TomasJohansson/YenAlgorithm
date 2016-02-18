package PriceDijkstra.Model;

/**
 * Created by Tkaewkunha on 2/17/16.
 */
public class ResultPathly {
    public String passed;
    public String transRid;
    public String transType;
    public Float weight;

    public ResultPathly(String passed, String transRid, String transType, Float weight) {
        this.passed = passed;
        this.transRid = transRid;
        this.transType = transType;
        this.weight = weight;
    }

    public String getPassed() {
        return passed;
    }

    public void setPassed(String passed) {
        this.passed = passed;
    }

    public String getTransRid() {
        return transRid;
    }

    public void setTransRid(String transRid) {
        this.transRid = transRid;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "ResultPathly{" +
                "passed='" + passed + '\'' +
                ", transRid='" + transRid + '\'' +
                ", transType='" + transType + '\'' +
                ", weight=" + weight +
                '}' +"\n";
    }
}
