import java.util.*;

public class DroneRegistry {
    private HashMap<String, Drone> droneMap = new HashMap<>();

    public void registerDrone(Drone drone)  { droneMap.put(drone.getDroneId(), drone); }
    public Drone getDrone(String id)        { return droneMap.get(id); }
    public boolean containsDrone(String id) { return droneMap.containsKey(id); }
    public int totalDrones()               { return droneMap.size(); }
    public Collection<Drone> getAllDrones() { return droneMap.values(); }

    public Drone findAvailableDrone(double packageWeight) {
        for (Drone d : droneMap.values())
            if (d.getStatus().equals("AVAILABLE") && d.getPayloadCapacity() >= packageWeight && d.getBatteryLevel() > 20)
                return d;
        return null;
    }
}