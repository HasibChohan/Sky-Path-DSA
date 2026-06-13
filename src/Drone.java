public class Drone {
    private String droneId, currentLocation, status;
    private double batteryLevel, payloadCapacity;

    public Drone(String droneId, double batteryLevel, String currentLocation, double payloadCapacity) {
        this.droneId = droneId; this.batteryLevel = batteryLevel;
        this.currentLocation = currentLocation; this.payloadCapacity = payloadCapacity;
        this.status = "AVAILABLE";
    }

    public String getDroneId()                 { return droneId; }
    public double getBatteryLevel()            { return batteryLevel; }
    public String getCurrentLocation()         { return currentLocation; }
    public void   setCurrentLocation(String l) { this.currentLocation = l; }
    public double getPayloadCapacity()         { return payloadCapacity; }
    public String getStatus()                  { return status; }
    public void   setStatus(String s)          { this.status = s; }

    public void consumeBattery(double distanceKm) {
        batteryLevel = Math.max(0, batteryLevel - distanceKm);
    }

    @Override
    public String toString() {
        return "Drone " + droneId +
                " | Battery: " + String.format("%.1f", batteryLevel) + "%" +
                " | Location: " + currentLocation +
                " | Capacity: " + payloadCapacity + "kg" +
                " | Status: " + status;
    }
}