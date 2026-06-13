import java.util.*;

public class DeliveryQueue {
    private Queue<DeliveryMission> queue = new LinkedList<>();

    public void enqueue(DeliveryMission m) { queue.add(m); }
    public DeliveryMission dequeue()       { return queue.poll(); }
    public boolean isEmpty()               { return queue.isEmpty(); }
    public int size()                      { return queue.size(); }
    public List<DeliveryMission> getAll()  { return new ArrayList<>(queue); }

    public void displayPendingOrders() {
        if (queue.isEmpty()) { System.out.println("\nNo pending orders."); return; }
        System.out.println("\n--- Pending Delivery Orders (FIFO Queue) ---");
        int i = 1;
        for (DeliveryMission m : queue) System.out.println(i++ + ". " + m);
    }
}