import java.util.*;

public class RouteBacktracker {
    private Stack<String> visitedStack = new Stack<>();

    public List<String> handleObstacle(Graph graph, List<String> originalRoute, String obstacleNode, String destination) {
        visitedStack.clear();
        List<String> travelledPath = new ArrayList<>();
        System.out.println("\n--- Route Backtracking ---");

        for (String waypoint : originalRoute) {
            if (waypoint.equals(obstacleNode)) {
                System.out.println("Obstacle detected at: " + waypoint);
                if (visitedStack.isEmpty()) { System.out.println("No previous waypoint to backtrack to."); return new ArrayList<>(); }
                String previousWaypoint = visitedStack.pop();
                System.out.println("Backtracking to: " + previousWaypoint);
                Set<String> blocked = new HashSet<>();
                blocked.add(obstacleNode);
                DijkstraAlgorithm.Result result = new DijkstraAlgorithm().findShortestPath(graph, previousWaypoint, destination, blocked);
                if (result.totalDistance == -1) { System.out.println("No alternate path found. Mission cannot continue."); return new ArrayList<>(); }
                System.out.println("Alternate route found: " + result.path + " (" + result.totalDistance + " km)");
                travelledPath.addAll(result.path.subList(1, result.path.size()));
                return travelledPath;
            }
            visitedStack.push(waypoint);
            travelledPath.add(waypoint);
        }
        System.out.println("No obstacle on this route. It is safe.");
        return travelledPath;
    }
}