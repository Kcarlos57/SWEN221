import java.util.*;


/**
 * Graph is the data structure that stores the collection of stops, lines and connections.
 * The Graph constructor is passed a Map of the stops, indexed by stopId and
 * a Map of the Lines, indexed by lineId.
 * The Stops in the map have their id, name and GIS location.
 * The Lines in the map have their id, and lists of the stopIDs and times (in order)
 * <br>
 * To build the actual graph structure, it is necessary to
 * build the list of Edges out of each stop and the list of Edges into each stop
 * Each pair of adjacent stops in a Line is an edge.
 * We also need to create walking edges between every pair of stops in the whole
 * network that are closer than walkingDistance.
 */
public class Graph {

    private Collection<Stop> stops;
    private Collection<Line> lines;
    private Collection<Edge> edges = new HashSet<>();      // edges between Stops
    private Set<Edge> walkingEdges = new HashSet<>();  // declare walkingEdges as a member variable

    private int numComponents = 0;     // number of connected subgraphs (graph components)

    /**
     * Construct a new graph given a collection of stops and a collection of lines.
     */
    public Graph(Collection<Stop> stops, Collection<Line> lines) {
        this.stops = new TreeSet<>(stops);
        this.lines = lines;

        // These are two of the key methods you must complete:
        createAndConnectEdges();
        computeNeighbours();

        // you could uncomment this to help in debugging your code
        //printGraphData();
    }


    /**
     * Print out the lines and stops in the graph to System.out
     */
    public void printGraphData() {
        System.out.println("============================\nLines:");
        for (Line line : lines) {
            System.out.println(line.getId() + "(" + line.getStops().size() + " stops)");
        }
        System.out.println("\n=============================\nStops:");
        for (Stop stop : stops) {
            System.out.println(stop + ((stop.getSubGraphId() < 0) ? "" : " subG:" + stop.getSubGraphId()));
            System.out.println("  " + stop.getForwardEdges().size() + " out edges; " +
                    stop.getBackwardEdges().size() + " in edges; " +
                    stop.getNeighbours().size() + " neighbours");
        }
        System.out.println("===============");
    }


    //============================================
    // Methods to build the graph structure. 
    //============================================

    /**
     * From the loaded Line and Stop information,
     * identify all the edges that connect stops along a Line.
     * - Construct the collection of all Edges in the graph  and
     * - Construct the forward and backward neighbour edges of each Stop.
     */
    private void createAndConnectEdges() {//TODO
        for (Line line : lines) {
            List<Stop> stops = line.getStops();
            for (int i = 0; i < stops.size() - 1; i++) {
                Stop fromStop = stops.get(i);
                Stop toStop = stops.get(i + 1);
                double distance = fromStop.getPoint().distance(toStop.getPoint());
                double speed = Transport.getSpeedMPS(line.getId());
                double time = distance / speed;
                Edge forwardEdge = new Edge(fromStop, toStop, line.getType(), line, time, distance);
                Edge backwardEdge = new Edge(toStop, fromStop, line.getType(), line, time, distance);
                fromStop.addForwardEdge(forwardEdge);
                toStop.addBackwardEdge(forwardEdge);
                edges.add(forwardEdge);
                edges.add(backwardEdge);
            }
        }
    }

    /**
     * Reverse the direction of all edges in the graph.
     */
    public void reverseEdges() {//TODO
        // create a new collection to hold the reversed edges
        Collection<Edge> reversedEdges = new HashSet<Edge>();
        // iterate over the existing edges
        for (Edge edge : edges) {
            // create a new edge with the from and to stops reversed
            Edge reversedEdge = new Edge(edge.toStop(), edge.fromStop(),
                    edge.transpType(), edge.line(),
                    edge.time(), edge.distance());
            // add the reversed edge to the new collection
            reversedEdges.add(reversedEdge);
        }
        // replace the old edges collection with the new one
        edges = reversedEdges;
        System.out.println("edges reversed");
    }



    /**
     * Construct the undirected graph of neighbours for each Stop:
     * For each Stop, construct a set of the stops that are its neighbours
     * from the forward and backward neighbour edges.
     * It may assume that there are no walking edges at this point.
     */
    public void computeNeighbours() {//TODO
        for (Stop stop : stops) {
            Set<Stop> neighbors = new HashSet<>();
            for (Edge edge : stop.getForwardEdges()) {
                neighbors.add(edge.toStop());
            }
            for (Edge edge : stop.getBackwardEdges()) {
                neighbors.add(edge.fromStop());
            }
            for (Stop neighbor: neighbors) {
                stop.addNeighbour(neighbor);
            }
        }
    }

    //=============================================================================
    //    Recompute Walking edges and add to the graph
    //=============================================================================
    //

    /**
     * Reconstruct all the current walking edges in the graph,
     * based on the specified walkingDistance:
     * identify all pairs of stops * that are at most walkingDistance apart,
     * and construct edges (both ways) between the stops
     * add the edges to the forward and backward neighbouars of the Stops
     * add the edges to the walking edges of the graph.
     * Assume that all the previous walking edges have been removed
     */
    public void recomputeWalkingEdges(double walkingDistance) {//TODO
        int walkcount = 0;
        System.out.println("stops for walking test size  = " + stops.size());
        for (Stop stop1 : stops) {
            for (Stop stop2 : stops) {
                if (stop1.equals(stop2)) {
                    continue;
                }
                double distance = stop1.getPoint().distance(stop2.getPoint());
                if (distance <= walkingDistance) {
                    Edge walkingEdge = new Edge(stop1, stop2, Transport.WALKING, null, distance / Transport.WALKING_SPEED_MPS, distance);
                    stop1.addForwardEdge(walkingEdge);
                    stop2.addBackwardEdge(walkingEdge);
                    this.edges.add(walkingEdge);
                    walkcount ++;
                }
            }
        }
        System.out.println("Recompute walking edges found " + walkcount + " walking edges");
    }

    /**
     * Remove all the current walking edges in the graph
     * - from the edges field (the collection of all the edges in the graph)
     * - from the forward and backward neighbours of each Stop.
     * - Resets the number of components back to 0 by
     * calling  resetSubGraphIds()
     */
    public void removeWalkingEdges() {
        resetSubGraphIds();
        for (Stop stop : stops) {
            stop.deleteEdgesOfType(Transport.WALKING);// remove all edges of type walking
        }
        edges.removeIf((Edge e) -> Transport.WALKING.equals(e.transpType()));

    }

    //=============================================================================
    //  Methods to access data from the graph. 
    //=============================================================================

    /**
     * Return a collection of all the stops in the network
     */
    public Collection<Stop> getStops() {
        return Collections.unmodifiableCollection(stops);
    }

    /**
     * Return a collection of all the edges in the network
     */
    public Collection<Edge> getEdges() {
        return Collections.unmodifiableCollection(edges);
    }

    /**
     * Return the first stop that starts with the specified prefix
     * (first by alphabetic order of name)
     */
    public Stop getFirstMatchingStop(String prefix) {
        for (Stop stop : stops) {
            if (stop.getName().startsWith(prefix)) {
                return stop;
            }
        }
        return null;
    }

    /**
     * Return all the stops that start with the specified prefix
     * in alphabetic order.
     */
    public List<Stop> getAllMatchingStops(String prefix) {
        List<Stop> ans = new ArrayList<>();
        for (Stop stop : stops) {
            if (stop.getName().startsWith(prefix)) {
                ans.add(stop);
            }
        }
        return ans;
    }

    public int getSubGraphCount() {
        return numComponents;
    }

    public void setSubGraphCount(int num) {
        numComponents = num;
        if (num == 0) {
            resetSubGraphIds();
        }
    }

    /**
     * reset the subgraph ID of all stops
     */
    public void resetSubGraphIds() {
        for (Stop stop : stops) {
            stop.setSubGraphId(-1);
        }
        numComponents = 0;
    }


}
