package PriceDijkstra.Model;

/**
 * Created by Tkaewkunha on 2/12/16.
 */
public class TransFixedInfo {
    String passed ;
    Float fare;

    public TransFixedInfo(String passed, Float fare) {
        this.passed = passed;
        this.fare = fare;
    }

    @Override
    public int hashCode() {
        int hashcode = 0;
        hashcode = passed.hashCode();
        return hashcode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TransFixedInfo) {
            TransFixedInfo pp = (TransFixedInfo) obj;
            return (pp.passed.equals(this.passed));
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "Passed :" + passed +", fare :" + fare;
    }

    public String getPassed() {
        return passed;
    }

    public void setPassed(String passed) {
        this.passed = passed;
    }

    public Float getFare() {
        return fare;
    }

    public void setFare(Float fare) {
        this.fare = fare;
    }
}
