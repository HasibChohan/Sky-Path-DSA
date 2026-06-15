import java.util.*;

public class RouteBacktracker {

    public List<String> handleObstacle(Graph graph, List<String> originalRoute, String obstacleNode, String destination) {
        Stack<String> visitedStack = new Stack<>();
        List<String> travelledPath = new ArrayList<>();

        for (String waypoint : originalRoute) {
            if (waypoint.equals(obstacleNode)) {
                if (visitedStack.isEmpty()) return new ArrayList<>();
                String prev = visitedStack.pop();
                Set<String> blocked = new HashSet<>();
                blocked.add(obstacleNode);
                DijkstraAlgorithm.Result result = new DijkstraAlgorithm().findShortestPath(graph, prev, destination, blocked);
                if (result.totalDistance == -1) return new ArrayList<>();
                travelledPath.addAll(result.path.subList(1, result.path.size()));
                return travelledPath;
            }
            visitedStack.push(waypoint);
            travelledPath.add(waypoint);
        }
        return travelledPath;
    }
}