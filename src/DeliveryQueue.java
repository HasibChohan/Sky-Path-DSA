import java.util.*;

public class DeliveryQueue {
    private Queue<DeliveryMission> queue = new LinkedList<>();

    public void enqueue(DeliveryMission m) { queue.add(m); }
    public DeliveryMission dequeue()       { return queue.poll(); }
    public boolean isEmpty()               { return queue.isEmpty(); }
    public List<DeliveryMission> getAll()  { return new ArrayList<>(queue); }
}