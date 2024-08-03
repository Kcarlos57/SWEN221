/**
 * AStar search (and Dijkstra search) uses a priority queue of partial paths
 * that the search is building.<br>
 * Each partial path needs several pieces of information, to specify
 * the path to that point, its cost so far, and its estimated total cost
 */

public class PathItem implements Comparable<PathItem> {
    private final Stop stop;
    private final double gScore;
    private final double fScore;
    private final Edge edge;

    public PathItem(Stop stop, double gScore, double fScore, Edge edge) {
        this.stop = stop;
        this.gScore = gScore;
        this.fScore = fScore;
        this.edge = edge;
    }

    public Stop getStop() {
        return stop;
    }

    public double getGScore() {
        return gScore;
    }

    public double getFScore() {
        return fScore;
    }

    public Edge getEdge() {
        return edge;
    }

    @Override
    public int compareTo(PathItem o) {
        return Double.compare(this.fScore, o.fScore);
    }
}
