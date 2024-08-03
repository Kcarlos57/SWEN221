//=============================================================================
//   TODO   Finding Components
//   Finds all the strongly connected subgraphs in the graph
//   Labels each stop with the number of the subgraph it is in and
//   sets the subGraphCount of the graph to the number of subgraphs.
//   Uses Kosaraju's_algorithm   (see lecture slides, based on
//   https://en.wikipedia.org/wiki/Kosaraju%27s_algorithm)
//=============================================================================

import java.util.*;

public class Components {

    // Use Kosaraju's algorithm.
    // In the forward search, record which nodes are visited with a visited set.
    // In the backward search, use the setSubGraphId and getSubGraphID methods
    // on the stop to record the component (and whether the node has been visited
    // during the backward search).
    // Alternatively, during the backward pass, you could use a Map<Stop,Stop>
    // to record the "root" node of each component, following the original version
    // of Kosaraju's algorithm, but this is unnecessarily complex.


    public static void findComponents(Graph graph) {
        System.out.println("findComponents called");
        graph.resetSubGraphIds();

        // Perform the first DFS and add nodes to the stack.
        Stack<Stop> stack = new Stack<>();
        Set<Stop> visited = new HashSet<>();
        for (Stop stop : graph.getStops()) {
            if (!visited.contains(stop)) {
                dfs1(stop, visited, stack);
            }
        }

        // Reverse the directions of the edges.
        graph.reverseEdges();

        // Perform the second DFS and identify each strongly connected component.
        int subGraphCount = 0;
        visited.clear();
        while (!stack.isEmpty()) {
            Stop stop = stack.pop();
            if (!visited.contains(stop)) {
                subGraphCount++;
                dfs2(stop, visited, subGraphCount);
            }
        }
        graph.setSubGraphCount(subGraphCount);
    }


    private static void dfs1(Stop stop, Set<Stop> visited, Stack<Stop> stack) {
        visited.add(stop);
        for (Edge edge : stop.getForwardEdges()) {
            Stop toStop = edge.toStop();
            if (!visited.contains(toStop)) {
                dfs1(toStop, visited, stack);
            }
        }
        stack.push(stop);
    }

    private static void dfs2(Stop stop, Set<Stop> visited, int subGraphId) {
        visited.add(stop);
        stop.setSubGraphId(subGraphId);
        for (Edge edge : stop.getBackwardEdges()) {
            Stop fromStop = edge.fromStop();
            if (!visited.contains(fromStop)) {
                dfs2(fromStop, visited, subGraphId);
            }
        }
    }



//    public static void findComponents(Graph graph) {
//        System.out.println("findComponents called");
//        graph.resetSubGraphIds();
//
//        // Perform a depth-first search on the graph to record the finishing times
//        // of each node
//        Stack<Stop> stack = new Stack<>();
//        Set<Stop> visited = new HashSet<>();
//        for (Stop stop : graph.getStops()) {
//            if (!visited.contains(stop)) {
//                depthFirstSearch(graph, stop, visited, stack);
//            }
//        }
//
//        // Perform a depth-first search on the graph in reverse order of the finishing times
//        // to find the strongly connected components
//        Set<Stop> component = new HashSet<>();
//        int subGraphCount = 0;
//        while (!stack.isEmpty()) {
//            Stop stop = stack.pop();
//            if (stop.getSubGraphId() == -1) {
//                depthFirstSearchReverse(graph, stop, subGraphCount, component);
//                subGraphCount++;
//            }
//        }
//
//        graph.setSubGraphCount(subGraphCount);
//    }
//
//    private static void depthFirstSearch(Graph graph, Stop stop, Set<Stop> visited, Stack<Stop> stack) {
//        visited.add(stop);
//        for (Stop neighbor : stop.getNeighbours()) {
//            if (!visited.contains(neighbor)) {
//                depthFirstSearch(graph, neighbor, visited, stack);
//            }
//        }
//        stack.push(stop);
//    }
//
//    private static void depthFirstSearchReverse(Graph graph, Stop stop, int subGraphId, Set<Stop> component) {
//        stop.setSubGraphId(subGraphId);
//        component.add(stop);
//        for (Edge edge : stop.getBackwardEdges()) {
//            Stop neighbor = edge.getFromStop();
//            if (neighbor.getSubGraphId() == -1) {
//                depthFirstSearchReverse(graph, neighbor, subGraphId, component);
//            }
//        }
//        for (Edge edge : stop.getForwardEdges()) {
//            Stop neighbor = edge.getToStop();
//            if (neighbor.getSubGraphId() == -1) {
//                depthFirstSearchReverse(graph, neighbor, subGraphId, component);
//            }
//        }
//    }

}
