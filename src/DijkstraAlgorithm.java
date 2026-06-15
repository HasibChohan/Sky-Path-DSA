import java.util.*;

public class DijkstraAlgorithm {

    private static class NodeDistance implements Comparable<NodeDistance> {
        String node; double distance;
        NodeDistance(String node, double distance) { this.node = node; this.distance = distance; }
        @Override public int compareTo(NodeDistance o) { return Double.compare(this.distance, o.distance); }
    }

    public static class Result {
        public List<String> path;
        public double totalDistance;
        Result(List<String> path, double totalDistance) { this.path = path; this.totalDistance = totalDistance; }
    }

    public Result findShortestPath(Graph graph, String source, String destination, Set<String> blockedNodes) {
        HashMap<String, Double> dist = new HashMap<>();
        HashMap<String, String> prev = new HashMap<>();
        PriorityQueue<NodeDistance> pq = new PriorityQueue<>();
        for (String wp : graph.getAllWaypoints()) dist.put(wp, Double.MAX_VALUE);
        dist.put(source, 0.0);
        pq.add(new NodeDistance(source, 0.0));
        while (!pq.isEmpty()) {
            NodeDistance cur = pq.poll();
            if (blockedNodes != null && blockedNodes.contains(cur.node) && !cur.node.equals(source)) continue;
            if (cur.distance > dist.get(cur.node)) continue;
            for (Graph.Edge edge : graph.getNeighbors(cur.node)) {
                if (blockedNodes != null && blockedNodes.contains(edge.destination)) continue;
                double newDist = dist.get(cur.node) + edge.weight;
                if (newDist < dist.get(edge.destination)) {
                    dist.put(edge.destination, newDist);
                    prev.put(edge.destination, cur.node);
                    pq.add(new NodeDistance(edge.destination, newDist));
                }
            }
        }
        List<String> path = new ArrayList<>();
        if (!destination.equals(source) && !prev.containsKey(destination)) return new Result(path, -1);
        String step = destination;
        while (step != null) { path.add(step); step = prev.get(step); }
        Collections.reverse(path);
        return new Result(path, dist.get(destination));
    }
}