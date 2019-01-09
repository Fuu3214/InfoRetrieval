public class Tuple implements Comparable<Tuple> {
    String s;
    double w;
    public Tuple(String s, double w) {
        this.s = s;
        this.w = w;
    }

    public String getS() { return s; }

    public double getW() { return w; }

    public String toString() {return s + " " + w;}
    @Override
    public int compareTo(Tuple o) {
        if (this.w > o.w) return -1;
        if (this.w < o.w) return 1;
        return 0;
    }
}
