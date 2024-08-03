/**
 * Implements the A* search algorithm to find the shortest path
 * in a graph between a start node and a goal node.
 * It returns a Path consisting of a list of Edges that will
 * connect the start node to the goal node.
 */

import java.util.*;


public class AStar {
    private static String timeOrDistance = "distance";    // way of calculating cost: "time" or "distance"
    // find the shortest path between two stops
    public static List<Edge> findShortestPath(Stop start, Stop goal, String timeOrDistance1){
        if (start == null || goal == null) {
            return null;
        }

        System.out.println(start.getId());
        System.out.println(goal.getId());
        timeOrDistance = (timeOrDistance1.equals("time")) ? "time" : "distance";

        Map<Stop, Edge> backPointers = new HashMap<>();
        Map<Stop, Double> gScore = new HashMap<>();
        Map<Stop, Double> fScore = new HashMap<>();
        PriorityQueue<PathItem> queue = new PriorityQueue<>();

        // Initialize start node
        gScore.put(start, 0.0);
        fScore.put(start, heuristic(start, goal));
        queue.offer(new PathItem(start, gScore.get(start), fScore.get(start), null));

        // A* search
        while (!queue.isEmpty()) {
            PathItem current = queue.poll();
            Stop currentStop = current.getStop();
            if (currentStop.equals(goal)) {
                // Goal node found, construct path
                List<Edge> path = new ArrayList<>();
                while (backPointers.containsKey(currentStop)) {
                    Edge edge = backPointers.get(currentStop);
                    path.add(edge);
                    currentStop = edge.fromStop();
                }
                Collections.reverse(path);
                return path;
            }
            for (Edge edge : currentStop.getForwardEdges()) {
                Stop neighbor = edge.toStop();
                double tentativeGScore = gScore.get(currentStop) + edgeCost(edge);

                if (!gScore.containsKey(neighbor) || tentativeGScore < gScore.get(neighbor)) {
                    // This path is better than any previous path to neighbor
                    backPointers.put(neighbor, edge);
                    gScore.put(neighbor, tentativeGScore);
                    double hScore = heuristic(neighbor, goal);
                    fScore.put(neighbor, tentativeGScore + hScore);
                    queue.offer(new PathItem(neighbor, gScore.get(neighbor), fScore.get(neighbor), edge));
                }
            }
        }
        return null;
    }

    public static List<Edge> findShortestPath1(Stop start, Stop goal, String timeOrDistance1) {
        if (start == null || goal == null) {
            return null;
        }

        timeOrDistance = (timeOrDistance1.equals("time")) ? "time" : "distance";

        Map<Stop, Edge> backPointers = new HashMap<>();
        Map<Stop, Double> gScore = new HashMap<>();
        Map<Stop, Double> fScore = new HashMap<>();
        PriorityQueue<PathItem> queue = new PriorityQueue<>();

        // Initialize start node
        gScore.put(start, 0.0);
        fScore.put(start, heuristic(start, goal));
        queue.offer(new PathItem(start, gScore.get(start), fScore.get(start), null));

        // A* search
        while (!queue.isEmpty()) {
            PathItem current = queue.poll();
            Stop currentStop = current.getStop();

            if (currentStop.equals(goal)) {
                // Goal node found, construct path
                List<Edge> path = new ArrayList<>();
                while (backPointers.containsKey(currentStop)) {
                    Edge edge = backPointers.get(currentStop);
                    path.add(edge);
                    currentStop = edge.fromStop();
                }
                Collections.reverse(path);
                return path;
            }

            for (Edge edge : currentStop.getForwardEdges()) {
                Stop neighbor = edge.toStop();
                double tentativeGScore = gScore.get(currentStop) + edgeCost(edge);

                if (!gScore.containsKey(neighbor) || tentativeGScore < gScore.get(neighbor)) {
                    // This path is better than any previous path to neighbor
                    backPointers.put(neighbor, edge);
                    gScore.put(neighbor, tentativeGScore);
                    double hScore = heuristic(neighbor, goal);
                    fScore.put(neighbor, tentativeGScore + hScore);
                    queue.offer(new PathItem(neighbor, gScore.get(neighbor), fScore.get(neighbor), edge));
                }
            }
        }
        return null;   // no path found
    }

    private static List<Edge> reconstructPath(Map<Stop, Stop> cameFrom, Stop current) {
        List<Edge> path = new ArrayList<>();
        while (cameFrom.containsKey(current)) {
            Stop prev = cameFrom.get(current);
            Edge edge = null;
            for (Edge e : prev.getForwardEdges()) {
                if (e.toStop() == current) {
                    edge = e;
                    break;
                }
            }
            if (edge == null) {
                for (Edge e : prev.getBackwardEdges()) {
                    if (e.fromStop() == current) {
                        edge = e;
                        break;
                    }
                }
            }
            if (edge != null) {
                path.add(0, edge);
            }
            current = prev;
        }
        return path;
    }

    /**
     * Return the heuristic estimate of the cost to get from a stop to the goal
     */
    public static double heuristic(Stop current, Stop goal) {
        if (timeOrDistance.equals("distance")) {
            return current.distanceTo(goal);
        } else if (timeOrDistance.equals("time")) {
            return current.distanceTo(goal) / Transport.TRAIN_SPEED_MPS;
        } else {
            return 0;
        }
    }

    /**
     * Return the cost of traversing an edge in the graph
     */
    public static double edgeCost(Edge edge) {
        if (timeOrDistance.equals("distance")) {
            return edge.distance();
        } else if (timeOrDistance.equals("time")) {
            return edge.time();
        } else {
            return 1;
        }
    }


}
