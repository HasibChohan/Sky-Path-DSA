import java.util.*;

public class MissionScheduler {
    private PriorityQueue<DeliveryMission> scheduleQueue =
            new PriorityQueue<>(Comparator.comparingInt(DeliveryMission::getPriorityLevel));

    public void addMission(DeliveryMission m)  { scheduleQueue.add(m); }
    public DeliveryMission getNextMission()    { return scheduleQueue.poll(); }
    public boolean isEmpty()                   { return scheduleQueue.isEmpty(); }
}