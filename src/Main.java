import java.util.*;

public class Main {

    static Graph airspaceGraph = new Graph();
    static DroneRegistry droneRegistry = new DroneRegistry();
    static DeliveryQueue pendingOrders = new DeliveryQueue();
    static MissionScheduler scheduler = new MissionScheduler();
    static DeliveryHistoryBST history = new DeliveryHistoryBST();
    static ArrayList<DeliveryMission> activeMissions = new ArrayList<>();
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("   Welcome to SkyPath - Drone Navigation System  ");
        setupAirspace();
        int choice;
        do {
            printMenu();
            choice = readInt("Enter your choice: ");
            try {
                switch (choice) {
                    case 1:  registerDroneMenu(); break;
                    case 2:  droneRegistry.displayAllDrones(); break;
                    case 3:  createMissionMenu(); break;
                    case 4:  pendingOrders.displayPendingOrders(); break;
                    case 5:  assignMissionMenu(); break;
                    case 6:  simulateDeliveryMenu(); break;
                    case 7:  simulateObstacleMenu(); break;
                    case 8:  completeMissionMenu(); break;
                    case 9:  searchCompletedMissionMenu(); break;
                    case 10: history.displayInorder(); break;
                    case 11: viewDroneRegistryMenu(); break;
                    case 12: System.out.println("\nExiting SkyPath. Goodbye!"); break;
                    default: System.out.println("Invalid choice. Enter 1 to 12.");
                }
            } catch (Exception e) { System.out.println("Error: " + e.getMessage()); }
        } while (choice != 12);
        sc.close();
    }

    private static void setupAirspace() {
        String[] waypoints = {"Warehouse","CityCenter","Hospital","Mall","Airport","University","Park","Stadium"};
        for (String w : waypoints) airspaceGraph.addWaypoint(w);
        airspaceGraph.addEdge("Warehouse","CityCenter",5);  airspaceGraph.addEdge("Warehouse","Airport",8);
        airspaceGraph.addEdge("CityCenter","Hospital",3);   airspaceGraph.addEdge("CityCenter","Mall",4);
        airspaceGraph.addEdge("CityCenter","University",6); airspaceGraph.addEdge("Hospital","Park",2);
        airspaceGraph.addEdge("Mall","Stadium",5);          airspaceGraph.addEdge("Airport","University",7);
        airspaceGraph.addEdge("University","Park",3);       airspaceGraph.addEdge("Park","Stadium",4);
        airspaceGraph.addEdge("Stadium","Warehouse",10);    airspaceGraph.addEdge("Mall","Hospital",6);
        airspaceGraph.addEdge("Airport","Stadium",9);
        System.out.println("\nAirspace map loaded.\nAvailable waypoints: " + getWaypointList());
    }

    private static String getWaypointList() {
        List<String> list = new ArrayList<>(airspaceGraph.getAllWaypoints());
        Collections.sort(list);
        return String.join(", ", list);
    }

    private static void printMenu() {
        System.out.println("\n ------SkyPath MENU ------");
        System.out.println("1.  Register Drone");          System.out.println("2.  View All Drones");
        System.out.println("3.  Create Delivery Mission"); System.out.println("4.  View Pending Orders");
        System.out.println("5.  Assign Mission");          System.out.println("6.  Simulate Drone Delivery");
        System.out.println("7.  Simulate Obstacle");       System.out.println("8.  Complete Mission");
        System.out.println("9.  Search Completed Mission (BST)");
        System.out.println("10. Display Delivery History (BST Inorder)");
        System.out.println("11. View Drone Registry");     System.out.println("12. Exit");
    }

    private static void registerDroneMenu() {
        System.out.println("\n--- Register New Drone ---");
        System.out.print("Enter Drone ID: ");
        String id = sc.next();
        if (droneRegistry.containsDrone(id)) { System.out.println("Drone " + id + " already exists!"); return; }
        double battery = readDouble("Enter Battery Level (0-100): ");
        if (battery < 0 || battery > 100) { System.out.println("Battery must be between 0 and 100."); return; }
        System.out.println("Available waypoints: " + getWaypointList());
        System.out.print("Enter Current Location: ");
        String location = sc.next();
        if (!airspaceGraph.getAllWaypoints().contains(location)) { System.out.println("Waypoint '" + location + "' not found. Registration cancelled."); return; }
        double capacity = readDouble("Enter Payload Capacity (kg): ");
        if (capacity <= 0) { System.out.println("Capacity must be greater than 0."); return; }
        droneRegistry.registerDrone(new Drone(id, battery, location, capacity));
        System.out.println("Drone " + id + " registered successfully!");
    }

    private static void createMissionMenu() {
        System.out.println("\n--- Create Delivery Mission ---");
        int missionId = readInt("Enter Mission ID (e.g. 201): ");
        for (DeliveryMission m : pendingOrders.getAll())
            if (m.getMissionId() == missionId) { System.out.println("Mission ID " + missionId + " already exists in pending queue."); return; }
        for (DeliveryMission m : activeMissions)
            if (m.getMissionId() == missionId) { System.out.println("Mission ID " + missionId + " already exists in active missions."); return; }
        if (history.search(missionId) != null) { System.out.println("Mission ID " + missionId + " already exists in history."); return; }
        System.out.println("Available waypoints: " + getWaypointList());
        System.out.print("Enter Source Waypoint: ");
        String source = sc.next();
        if (!airspaceGraph.getAllWaypoints().contains(source)) { System.out.println("Source waypoint '" + source + "' not found."); return; }
        System.out.print("Enter Destination Waypoint: ");
        String destination = sc.next();
        if (!airspaceGraph.getAllWaypoints().contains(destination)) { System.out.println("Destination waypoint '" + destination + "' not found."); return; }
        if (source.equals(destination)) { System.out.println("Source and destination cannot be the same."); return; }
        double weight = readDouble("Enter Package Weight (kg): ");
        if (weight <= 0) { System.out.println("Weight must be greater than 0."); return; }
        int priority = readInt("Enter Priority (1=Emergency, 2=Normal, 3=Low): ");
        if (priority < 1 || priority > 3) { System.out.println("Priority must be 1, 2, or 3."); return; }
        pendingOrders.enqueue(new DeliveryMission(missionId, source, destination, weight, priority));
        System.out.println("Mission M" + missionId + " created and added to pending queue.");
    }

    private static void assignMissionMenu() {
        System.out.println("\n--- Assign Mission ---");
        if (pendingOrders.isEmpty()) { System.out.println("No pending orders to assign."); return; }
        if (droneRegistry.totalDrones() == 0) { System.out.println("No drones registered. Please register a drone first (Option 1)."); return; }
        while (!pendingOrders.isEmpty()) scheduler.addMission(pendingOrders.dequeue());
        DeliveryMission mission = scheduler.getNextMission();
        System.out.println("\nMost urgent mission selected:\n" + mission);
        Drone drone = droneRegistry.findAvailableDrone(mission.getPackageWeight());
        if (drone == null) {
            System.out.println("\nNo available drone can carry " + mission.getPackageWeight() + "kg right now.");
            System.out.println("Mission M" + mission.getMissionId() + " returned to scheduler.");
            scheduler.addMission(mission);
            while (!scheduler.isEmpty()) pendingOrders.enqueue(scheduler.getNextMission());
            return;
        }
        DijkstraAlgorithm dijkstra = new DijkstraAlgorithm();
        DijkstraAlgorithm.Result pickupRoute   = dijkstra.findShortestPath(airspaceGraph, drone.getCurrentLocation(), mission.getSource(), null);
        DijkstraAlgorithm.Result deliveryRoute = dijkstra.findShortestPath(airspaceGraph, mission.getSource(), mission.getDestination(), null);
        if (deliveryRoute.totalDistance == -1) {
            System.out.println("No route found between " + mission.getSource() + " and " + mission.getDestination() + ". Mission cancelled.");
            while (!scheduler.isEmpty()) pendingOrders.enqueue(scheduler.getNextMission());
            return;
        }
        mission.setRoute(deliveryRoute.path);
        mission.setTotalDistance(deliveryRoute.totalDistance);
        mission.setAssignedDroneId(drone.getDroneId());
        mission.setStatus("ASSIGNED");
        drone.setStatus("BUSY");
        activeMissions.add(mission);
        System.out.println("\nDrone " + drone.getDroneId() + " assigned to Mission M" + mission.getMissionId());
        if (pickupRoute.totalDistance > 0)
            System.out.println("Step 1 - Drone flies to pickup: " + pickupRoute.path + " (" + pickupRoute.totalDistance + " km)");
        else
            System.out.println("Step 1 - Drone is already at pickup point: " + mission.getSource());
        System.out.println("Step 2 - Delivery route        : " + deliveryRoute.path + " (" + deliveryRoute.totalDistance + " km)");
        while (!scheduler.isEmpty()) pendingOrders.enqueue(scheduler.getNextMission());
    }

    private static void simulateDeliveryMenu() {
        System.out.println("\n--- Simulate Drone Delivery ---");
        List<DeliveryMission> assignedList = filterActive("ASSIGNED");
        if (assignedList.isEmpty()) { System.out.println("No ASSIGNED missions to simulate. Please assign a mission first (Option 5)."); return; }
        DeliveryMission mission = pickFromList(assignedList);
        if (mission == null) return;
        Drone drone = droneRegistry.getDrone(mission.getAssignedDroneId());
        if (drone == null) { System.out.println("Drone not found."); return; }
        System.out.println("\nDrone " + drone.getDroneId() + " is flying along the route...");
        for (String wp : mission.getRoute()) System.out.println("   -> Arrived at: " + wp);
        drone.consumeBattery(mission.getTotalDistance());
        drone.setCurrentLocation(mission.getDestination());
        mission.setStatus("IN_PROGRESS");
        System.out.println("\nDrone " + drone.getDroneId() + " has reached " + mission.getDestination() + ".");
        System.out.printf("Remaining battery: %.1f%%\n", drone.getBatteryLevel());
        if (drone.getBatteryLevel() < 20) { drone.setStatus("LOW_BATTERY"); System.out.println("WARNING: Drone " + drone.getDroneId() + " battery is critically low!"); }
    }

    private static void simulateObstacleMenu() {
        System.out.println("\n--- Simulate Obstacle / No-Fly Zone ---");
        List<DeliveryMission> eligible = filterActive(null);
        if (eligible.isEmpty()) { System.out.println("No active missions available."); return; }
        DeliveryMission mission = pickFromList(eligible);
        if (mission == null) return;
        if (mission.getRoute() == null || mission.getRoute().size() < 2) { System.out.println("This mission has no route set yet. Assign it first (Option 5)."); return; }
        System.out.println("Current route: " + mission.getRoute());
        System.out.print("Enter the waypoint where obstacle is detected: ");
        String obstacle = sc.next();
        if (!mission.getRoute().contains(obstacle)) { System.out.println("Waypoint '" + obstacle + "' is not on the current route."); return; }
        if (obstacle.equals(mission.getRoute().get(0))) { System.out.println("Cannot block the starting waypoint."); return; }
        List<String> newRoute = new RouteBacktracker().handleObstacle(airspaceGraph, mission.getRoute(), obstacle, mission.getDestination());
        if (newRoute != null && !newRoute.isEmpty()) { mission.setRoute(newRoute); System.out.println("Mission M" + mission.getMissionId() + " route updated to: " + newRoute); }
        else System.out.println("Could not find an alternate route. Mission route unchanged.");
    }

    private static void completeMissionMenu() {
        System.out.println("\n--- Complete Mission ---");
        List<DeliveryMission> completable = filterActive(null);
        if (completable.isEmpty()) { System.out.println("No active missions to complete."); return; }
        DeliveryMission mission = pickFromList(completable);
        if (mission == null) return;
        mission.setStatus("COMPLETED");
        Drone drone = droneRegistry.getDrone(mission.getAssignedDroneId());
        if (drone != null && !drone.getStatus().equals("LOW_BATTERY")) drone.setStatus("AVAILABLE");
        history.insert(mission);
        activeMissions.remove(mission);
        System.out.println("Mission M" + mission.getMissionId() + " marked as COMPLETED and saved to history.");
    }

    private static void searchCompletedMissionMenu() {
        System.out.println("\n--- Search Completed Mission (BST) ---");
        int missionId = readInt("Enter Mission ID to search: ");
        DeliveryMission mission = history.search(missionId);
        if (mission == null) System.out.println("Mission M" + missionId + " not found in completed history.");
        else { System.out.println("Mission found:"); System.out.println(mission); }
    }

    private static void viewDroneRegistryMenu() {
        System.out.println("\n--- Drone Registry Overview ---");
        System.out.println("Total drones registered: " + droneRegistry.totalDrones());
        droneRegistry.displayAllDrones();
    }

    private static List<DeliveryMission> filterActive(String statusFilter) {
        List<DeliveryMission> result = new ArrayList<>();
        for (DeliveryMission m : activeMissions)
            if (statusFilter == null || m.getStatus().equals(statusFilter)) result.add(m);
        return result;
    }

    private static DeliveryMission pickFromList(List<DeliveryMission> list) {
        if (list.size() == 1) { System.out.println("Auto-selected: " + list.get(0)); return list.get(0); }
        System.out.println("Select a mission:");
        for (int i = 0; i < list.size(); i++) System.out.println((i + 1) + ". " + list.get(i));
        int choice = readInt("Enter choice number: ");
        if (choice < 1 || choice > list.size()) { System.out.println("Invalid choice."); return null; }
        return list.get(choice - 1);
    }

    private static int readInt(String msg) {
        while (true) {
            try { System.out.print(msg); return Integer.parseInt(sc.next().trim()); }
            catch (NumberFormatException e) { System.out.println("Please enter a whole number."); }
        }
    }

    private static double readDouble(String msg) {
        while (true) {
            try { System.out.print(msg); return Double.parseDouble(sc.next().trim()); }
            catch (NumberFormatException e) { System.out.println("Please enter a valid number."); }
        }
    }
}