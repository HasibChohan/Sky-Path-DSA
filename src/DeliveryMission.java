import java.util.*;

public class DeliveryMission {
    private int missionId, priorityLevel;
    private String source, destination, status, assignedDroneId;
    private double packageWeight, totalDistance;
    private List<String> route;

    public DeliveryMission(int missionId, String source, String destination, double packageWeight, int priorityLevel) {
        this.missionId = missionId; this.source = source; this.destination = destination;
        this.packageWeight = packageWeight; this.priorityLevel = priorityLevel;
        this.status = "PENDING"; this.assignedDroneId = null;
        this.route = new ArrayList<>(); this.totalDistance = 0;
    }

    public int    getMissionId()                 { return missionId; }
    public String getSource()                    { return source; }
    public String getDestination()               { return destination; }
    public double getPackageWeight()             { return packageWeight; }
    public int    getPriorityLevel()             { return priorityLevel; }
    public String getStatus()                    { return status; }
    public void   setStatus(String s)            { this.status = s; }
    public String getAssignedDroneId()           { return assignedDroneId; }
    public void   setAssignedDroneId(String id)  { this.assignedDroneId = id; }
    public List<String> getRoute()               { return route; }
    public void   setRoute(List<String> r)       { this.route = r; }
    public double getTotalDistance()             { return totalDistance; }
    public void   setTotalDistance(double d)     { this.totalDistance = d; }

    public String getPriorityText() {
        if (priorityLevel == 1) return "EMERGENCY";
        if (priorityLevel == 2) return "NORMAL";
        return "LOW";
    }

    @Override
    public String toString() {
        return "Mission M" + missionId +
                " | " + source + " -> " + destination +
                " | Weight: " + packageWeight + "kg" +
                " | Priority: " + getPriorityText() +
                " | Status: " + status +
                " | Drone: " + (assignedDroneId == null ? "Not Assigned" : assignedDroneId) +
                " | Distance: " + totalDistance + "km";
    }
}