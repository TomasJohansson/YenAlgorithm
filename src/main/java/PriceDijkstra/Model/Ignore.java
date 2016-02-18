package PriceDijkstra.Model;

/**
 * Created by Tkaewkunha on 2/17/16.
 */
public class Ignore {
    private String start;
    private String end;
    private String transRid;

    public Ignore(String start, String end, String transRid) {
        this.start = start;
        this.end = end;
        this.transRid = transRid;
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
        return "Ignore{" +
                "start='" + start + '\'' +
                ", end='" + end + '\'' +
                ", transRid='" + transRid + '\'' +
                '}';
    }
}
