import java.util.*;

public class Graph {
    private HashMap<String, List<Edge>> adjacencyList = new HashMap<>();

    public static class Edge {
        String destination;
        double weight;
        public Edge(String destination, double weight) {
            this.destination = destination;
            this.weight = weight;
        }
    }

    public void addWaypoint(String name) { adjacencyList.putIfAbsent(name, new ArrayList<>()); }

    public void addEdge(String from, String to, double weight) {
        adjacencyList.get(from).add(new Edge(to, weight));
        adjacencyList.get(to).add(new Edge(from, weight));
    }

    public List<Edge> getNeighbors(String node) { return adjacencyList.getOrDefault(node, new ArrayList<>()); }
    public Set<String> getAllWaypoints() { return adjacencyList.keySet(); }
}