package PriceDijkstra.Model;

/**
 * Created by Tkaewkunha on 2/11/16.
 */
public class TransInfo {
    public String from;
    public String to;
    public Float fare;

    public TransInfo(String from, String to, Float fare) {
        this.from = from;
        this.fare = fare;
        this.to = to;
    }

    @Override
    public int hashCode(){
//        System.out.println("In hashcode");
        int hashcode = 0;
        hashcode = from.hashCode();
        hashcode += to.hashCode();
        return hashcode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TransInfo) {
            TransInfo pp = (TransInfo) obj;
            return (pp.from.equals(this.from) && pp.to.equals(this.to));
        } else {
            return false;
        }
    }
    @Override
    public String toString() {
        return "from :" + this.from + ", to :" + this.to + ", fare :" + this.fare;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Float getFare() {
        return fare;
    }

    public void setFare(Float fare) {
        this.fare = fare;
    }
}
