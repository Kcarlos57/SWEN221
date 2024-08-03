import java.util.*;


//=============================================================================
//   TODO   Finding Articulation Points
//   Finds and returns a collection of all the articulation points in the undirected
//   graph, without walking edges
//=============================================================================

public class ArticulationPoints {

    // Use the algorithm from the lectures, but you will need a loop to check through
    // all the Stops in the graph to find any Stops which were not connected to the
    // previous Stops, and apply the lecture slide algorithm starting at each such stop.

    public static Collection<Stop> findArticulationPoints(Graph graph) {
        System.out.println("findArticulationPoints called");

        graph.computeNeighbours();

        Set<Stop> articulationPoints = new HashSet<>();
        int time = 0;

        // Map to keep track of the lowest discovery time for each Stop
        Map<Stop, Integer> lowestDiscoveryTimeMap = new HashMap<>();

        // Set to keep track of visited stops
        Set<Stop> visited = new HashSet<>();

        // Loop through all the stops in the graph
        for (Stop stop : graph.getStops()) {
            if (!visited.contains(stop)) {
                findArticulationPointsHelper(stop, null, visited, articulationPoints, lowestDiscoveryTimeMap, time);
            }
        }

        return articulationPoints;
    }

    private static void findArticulationPointsHelper(Stop currentStop, Stop parentStop, Set<Stop> visited, Set<Stop> articulationPoints, Map<Stop, Integer> lowestDiscoveryTimeMap, int time) {
        visited.add(currentStop);
        lowestDiscoveryTimeMap.put(currentStop, time);
        int lowestDiscoveryTime = time;
        boolean isArticulationPoint = false;
        int childCount = 0;

        for (Stop neighbour : currentStop.getNeighbours()) {
            if (!visited.contains(neighbour)) {
                childCount++;
                time++;
                findArticulationPointsHelper(neighbour, currentStop, visited, articulationPoints, lowestDiscoveryTimeMap, time);

                // update the lowest discovery time of the current stop
                lowestDiscoveryTime = Math.min(lowestDiscoveryTime, lowestDiscoveryTimeMap.get(neighbour));

                // check if the current stop is an articulation point
                    if (lowestDiscoveryTimeMap.get(neighbour) >= lowestDiscoveryTimeMap.get(currentStop)) {
                        isArticulationPoint = true;
                    }
            } else if (neighbour != parentStop) {
                // update the lowest discovery time of the current stop
                lowestDiscoveryTime = Math.min(lowestDiscoveryTime, lowestDiscoveryTimeMap.get(neighbour));
            }
        }

        if ((parentStop == null && childCount > 1) || (parentStop != null && isArticulationPoint)) {
            articulationPoints.add(currentStop);
        }

        // update the lowest discovery time of the current stop
        lowestDiscoveryTimeMap.put(currentStop, lowestDiscoveryTime);
    }


}