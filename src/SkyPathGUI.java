import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class SkyPathGUI extends JFrame {

    static final Color BG        = new Color(13, 17, 30);
    static final Color PANEL     = new Color(22, 30, 50);
    static final Color CARD      = new Color(30, 41, 68);
    static final Color BORDER    = new Color(37, 99, 235);
    static final Color ACCENT    = new Color(56, 189, 248);
    static final Color TEXT      = new Color(226, 232, 240);
    static final Color MUTED     = new Color(100, 116, 139);
    static final Color GREEN     = new Color(34, 197, 94);
    static final Color RED       = new Color(239, 68, 68);
    static final Color ORANGE    = new Color(251, 146, 60);
    static final Color PURPLE    = new Color(167, 139, 250);
    static final Color YELLOW    = new Color(250, 204, 21);

    Graph airspaceGraph           = new Graph();
    DroneRegistry droneRegistry   = new DroneRegistry();
    DeliveryQueue pendingOrders   = new DeliveryQueue();
    MissionScheduler scheduler    = new MissionScheduler();
    DeliveryHistoryBST history    = new DeliveryHistoryBST();
    List<DeliveryMission> active  = new ArrayList<>();

    JTextArea logArea;
    JTabbedPane tabs;

    DefaultTableModel droneModel, pendingModel, activeModel, historyModel;

    public SkyPathGUI() {
        setupAirspace();
        buildUI();
    }

    private void setupAirspace() {
        String[] wps = {"Warehouse","CityCenter","Hospital","Mall","Airport","University","Park","Stadium"};
        for (String w : wps) airspaceGraph.addWaypoint(w);
        airspaceGraph.addEdge("Warehouse","CityCenter",5);   airspaceGraph.addEdge("Warehouse","Airport",8);
        airspaceGraph.addEdge("CityCenter","Hospital",3);    airspaceGraph.addEdge("CityCenter","Mall",4);
        airspaceGraph.addEdge("CityCenter","University",6);  airspaceGraph.addEdge("Hospital","Park",2);
        airspaceGraph.addEdge("Mall","Stadium",5);           airspaceGraph.addEdge("Mall","Hospital",6);
        airspaceGraph.addEdge("Airport","University",7);     airspaceGraph.addEdge("Airport","Stadium",9);
        airspaceGraph.addEdge("University","Park",3);        airspaceGraph.addEdge("Park","Stadium",4);
        airspaceGraph.addEdge("Stadium","Warehouse",10);
    }

    private String[] waypointArray() {
        List<String> list = new ArrayList<>(airspaceGraph.getAllWaypoints());
        Collections.sort(list);
        return list.toArray(new String[0]);
    }

    private void buildUI() {
        setTitle("SkyPath — Drone Navigation System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 780);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout(0, 0));

        add(buildHeader(), BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildLeftPanel(), buildRightPanel());
        split.setDividerLocation(260);
        split.setDividerSize(3);
        split.setBorder(null);
        split.setBackground(BG);
        add(split, BorderLayout.CENTER);

        add(buildLogPanel(), BorderLayout.SOUTH);

        log("✈  SkyPath initialized. Airspace loaded with 8 waypoints.");
        setVisible(true);
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(PANEL);
        p.setBorder(new MatteBorder(0, 0, 2, 0, BORDER));

        JLabel title = new JLabel("  ✈  SkyPath  —  Drone Navigation System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(ACCENT);
        title.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 0));

        JLabel sub = new JLabel("Dark Theme  |  Java Swing  |  DSA Project  ");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(MUTED);

        p.add(title, BorderLayout.WEST);
        p.add(sub, BorderLayout.EAST);
        return p;
    }

    private JPanel buildLeftPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(PANEL);
        p.setBorder(new MatteBorder(0, 0, 0, 1, BORDER));

        p.add(sideLabel("DRONE MANAGEMENT"));
        p.add(sideBtn("➕  Register Drone",     GREEN,   this::registerDrone));
        p.add(sideBtn("📋  View All Drones",     ACCENT,  this::showDroneTab));

        p.add(sideLabel("MISSION CONTROL"));
        p.add(sideBtn("🚀  Create Mission",      PURPLE,  this::createMission));
        p.add(sideBtn("📦  Pending Orders",      YELLOW,  this::showPendingTab));
        p.add(sideBtn("🎯  Assign Mission",      ACCENT,  this::assignMission));

        p.add(sideLabel("SIMULATION"));
        p.add(sideBtn("▶   Simulate Delivery",  GREEN,   this::simulateDelivery));
        p.add(sideBtn("⚠   Simulate Obstacle",  ORANGE,  this::simulateObstacle));
        p.add(sideBtn("✅  Complete Mission",    GREEN,   this::completeMission));

        p.add(sideLabel("HISTORY"));
        p.add(sideBtn("🔍  Search Mission",      PURPLE,  this::searchMission));
        p.add(sideBtn("📜  Delivery History",    ACCENT,  this::showHistoryTab));

        p.add(Box.createVerticalGlue());
        return p;
    }

    private JLabel sideLabel(String text) {
        JLabel l = new JLabel("  " + text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 10));
        l.setForeground(MUTED);
        l.setBorder(BorderFactory.createEmptyBorder(14, 0, 4, 0));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        return l;
    }

    private JButton sideBtn(String text, Color accent, Runnable action) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        b.setForeground(TEXT);
        b.setBackground(PANEL);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 10));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(new Color(37, 51, 85)); b.setForeground(accent); }
            public void mouseExited(MouseEvent e)  { b.setBackground(PANEL); b.setForeground(TEXT); }
        });
        b.addActionListener(e -> action.run());
        return b;
    }
    private JTabbedPane buildRightPanel() {
        tabs = new JTabbedPane();
        tabs.setBackground(BG);
        tabs.setForeground(TEXT);
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 12));
        UIManager.put("TabbedPane.selected", CARD);
        UIManager.put("TabbedPane.contentAreaColor", CARD);

        tabs.addTab("🚁 Drones",   buildDroneTable());
        tabs.addTab("📦 Pending",  buildPendingTable());
        tabs.addTab("⚡ Active",   buildActiveTable());
        tabs.addTab("📜 History",  buildHistoryTable());

        return tabs;
    }

    private JScrollPane buildDroneTable() {
        droneModel = new DefaultTableModel(new String[]{"Drone ID","Battery %","Location","Capacity (kg)","Status"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        return styledTable(droneModel);
    }

    private JScrollPane buildPendingTable() {
        pendingModel = new DefaultTableModel(new String[]{"Mission ID","Source","Destination","Weight (kg)","Priority","Status"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        return styledTable(pendingModel);
    }

    private JScrollPane buildActiveTable() {
        activeModel = new DefaultTableModel(new String[]{"Mission ID","Source","Destination","Drone","Distance (km)","Status"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        return styledTable(activeModel);
    }

    private JScrollPane buildHistoryTable() {
        historyModel = new DefaultTableModel(new String[]{"Mission ID","Source","Destination","Drone","Distance (km)","Status"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        return styledTable(historyModel);
    }

    private JScrollPane styledTable(DefaultTableModel model) {
        JTable table = new JTable(model) {
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                c.setBackground(row % 2 == 0 ? CARD : new Color(26, 36, 60));
                c.setForeground(TEXT);
                if (isRowSelected(row)) c.setBackground(new Color(37, 99, 235, 120));
                return c;
            }
        };
        table.setBackground(CARD);
        table.setForeground(TEXT);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(32);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.getTableHeader().setBackground(new Color(15, 23, 42));
        table.getTableHeader().setForeground(ACCENT);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBorder(new MatteBorder(0, 0, 2, 0, BORDER));
        table.setSelectionBackground(new Color(37, 99, 235, 80));

        JScrollPane sp = new JScrollPane(table);
        sp.setBackground(BG);
        sp.getViewport().setBackground(CARD);
        sp.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        return sp;
    }

    private JPanel buildLogPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(10, 14, 26));
        p.setBorder(new MatteBorder(2, 0, 0, 0, BORDER));
        p.setPreferredSize(new Dimension(0, 180));

        JLabel lbl = new JLabel("  📡  System Log");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(ACCENT);
        lbl.setBorder(BorderFactory.createEmptyBorder(6, 4, 4, 0));
        lbl.setBackground(new Color(10, 14, 26));
        lbl.setOpaque(true);

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setBackground(new Color(10, 14, 26));
        logArea.setForeground(new Color(134, 239, 172));
        logArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        logArea.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        logArea.setCaretColor(GREEN);

        JScrollPane sp = new JScrollPane(logArea);
        sp.setBorder(null);
        sp.setBackground(new Color(10, 14, 26));

        JButton clearBtn = new JButton("Clear");
        clearBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        clearBtn.setForeground(MUTED);
        clearBtn.setBackground(new Color(10, 14, 26));
        clearBtn.setBorderPainted(false);
        clearBtn.setFocusPainted(false);
        clearBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        clearBtn.addActionListener(e -> logArea.setText(""));

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(new Color(10, 14, 26));
        top.add(lbl, BorderLayout.WEST);
        top.add(clearBtn, BorderLayout.EAST);

        p.add(top, BorderLayout.NORTH);
        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    private void log(String msg) {
        logArea.append("[SkyPath]  " + msg + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    private void registerDrone() {
        JPanel form = darkForm();
        JTextField idField  = darkField(); JTextField batField = darkField();
        JComboBox<String> locBox = darkCombo(waypointArray());
        JTextField capField = darkField();

        form.add(darkLabel("Drone ID:"));      form.add(idField);
        form.add(darkLabel("Battery (0-100):")); form.add(batField);
        form.add(darkLabel("Location:"));       form.add(locBox);
        form.add(darkLabel("Capacity (kg):"));  form.add(capField);

        int res = showDarkDialog(form, "Register New Drone", "Register");
        if (res != JOptionPane.OK_OPTION) return;

        String id = idField.getText().trim();
        if (id.isEmpty()) { err("Drone ID cannot be empty."); return; }
        if (droneRegistry.containsDrone(id)) { err("Drone " + id + " already exists!"); return; }

        double bat, cap;
        try { bat = Double.parseDouble(batField.getText().trim()); } catch (Exception e) { err("Invalid battery."); return; }
        if (bat < 0 || bat > 100) { err("Battery must be 0-100."); return; }
        try { cap = Double.parseDouble(capField.getText().trim()); } catch (Exception e) { err("Invalid capacity."); return; }
        if (cap <= 0) { err("Capacity must be > 0."); return; }

        String loc = (String) locBox.getSelectedItem();
        droneRegistry.registerDrone(new Drone(id, bat, loc, cap));
        log("✅  Drone " + id + " registered at " + loc + " | Battery: " + bat + "% | Capacity: " + cap + "kg");
        refreshDroneTable();
    }

    private void createMission() {
        JPanel form = darkForm();
        JTextField idField  = darkField();
        JComboBox<String> srcBox  = darkCombo(waypointArray());
        JComboBox<String> dstBox  = darkCombo(waypointArray());
        JTextField wtField  = darkField();
        String[] pris = {"1 - EMERGENCY", "2 - NORMAL", "3 - LOW"};
        JComboBox<String> priBox = darkCombo(pris);

        form.add(darkLabel("Mission ID:"));   form.add(idField);
        form.add(darkLabel("Source:"));        form.add(srcBox);
        form.add(darkLabel("Destination:"));   form.add(dstBox);
        form.add(darkLabel("Weight (kg):"));   form.add(wtField);
        form.add(darkLabel("Priority:"));      form.add(priBox);

        int res = showDarkDialog(form, "Create Delivery Mission", "Create");
        if (res != JOptionPane.OK_OPTION) return;

        int mId;
        try { mId = Integer.parseInt(idField.getText().trim()); } catch (Exception e) { err("Invalid Mission ID."); return; }

        for (DeliveryMission m : pendingOrders.getAll()) if (m.getMissionId() == mId) { err("Mission ID " + mId + " already in pending."); return; }
        for (DeliveryMission m : active) if (m.getMissionId() == mId) { err("Mission ID " + mId + " already active."); return; }
        if (history.search(mId) != null) { err("Mission ID " + mId + " already in history."); return; }

        String src = (String) srcBox.getSelectedItem();
        String dst = (String) dstBox.getSelectedItem();
        if (src.equals(dst)) { err("Source and destination cannot be same."); return; }

        double wt;
        try { wt = Double.parseDouble(wtField.getText().trim()); } catch (Exception e) { err("Invalid weight."); return; }
        if (wt <= 0) { err("Weight must be > 0."); return; }

        int pri = priBox.getSelectedIndex() + 1;
        pendingOrders.enqueue(new DeliveryMission(mId, src, dst, wt, pri));
        log("🚀  Mission M" + mId + " created: " + src + " → " + dst + " | " + wt + "kg | Priority " + pri);
        refreshPendingTable();
    }

    private void assignMission() {
        if (pendingOrders.isEmpty()) { err("No pending missions."); return; }
        if (droneRegistry.getAllDrones().isEmpty()) { err("No drones registered."); return; }

        while (!pendingOrders.isEmpty()) scheduler.addMission(pendingOrders.dequeue());
        DeliveryMission mission = scheduler.getNextMission();

        Drone drone = droneRegistry.findAvailableDrone(mission.getPackageWeight());
        if (drone == null) {
            log("⚠  No available drone for " + mission.getPackageWeight() + "kg. Mission M" + mission.getMissionId() + " re-queued.");
            scheduler.addMission(mission);
            while (!scheduler.isEmpty()) pendingOrders.enqueue(scheduler.getNextMission());
            refreshPendingTable();
            return;
        }

        DijkstraAlgorithm dijk = new DijkstraAlgorithm();
        DijkstraAlgorithm.Result delivery = dijk.findShortestPath(airspaceGraph, mission.getSource(), mission.getDestination(), null);
        DijkstraAlgorithm.Result pickup   = dijk.findShortestPath(airspaceGraph, drone.getCurrentLocation(), mission.getSource(), null);

        if (delivery.totalDistance == -1) {
            log("❌  No route from " + mission.getSource() + " to " + mission.getDestination() + ". Mission cancelled.");
            while (!scheduler.isEmpty()) pendingOrders.enqueue(scheduler.getNextMission());
            refreshPendingTable(); return;
        }

        mission.setRoute(delivery.path);
        mission.setTotalDistance(delivery.totalDistance);
        mission.setAssignedDroneId(drone.getDroneId());
        mission.setStatus("ASSIGNED");
        drone.setStatus("BUSY");
        active.add(mission);

        log("🎯  Drone " + drone.getDroneId() + " → Mission M" + mission.getMissionId());
        if (pickup.totalDistance > 0)
            log("    Pickup route: " + pickup.path + " (" + pickup.totalDistance + " km)");
        log("    Delivery route: " + delivery.path + " (" + delivery.totalDistance + " km)");

        while (!scheduler.isEmpty()) pendingOrders.enqueue(scheduler.getNextMission());
        refreshAll();
        tabs.setSelectedIndex(2);
    }
    private void simulateDelivery() {
        List<DeliveryMission> assigned = filterActive("ASSIGNED");
        if (assigned.isEmpty()) { err("No ASSIGNED missions. Assign a mission first."); return; }

        DeliveryMission mission = pickMission(assigned, "Simulate Delivery");
        if (mission == null) return;

        Drone drone = droneRegistry.getDrone(mission.getAssignedDroneId());
        if (drone == null) { err("Drone not found."); return; }

        StringBuilder sb = new StringBuilder();
        sb.append("Drone ").append(drone.getDroneId()).append(" flying:\n\n");
        for (String wp : mission.getRoute()) sb.append("  →  ").append(wp).append("\n");

        drone.consumeBattery(mission.getTotalDistance());
        drone.setCurrentLocation(mission.getDestination());
        mission.setStatus("IN_PROGRESS");

        sb.append("\n✅  Reached: ").append(mission.getDestination());
        sb.append("\n🔋  Battery remaining: ").append(String.format("%.1f", drone.getBatteryLevel())).append("%");
        if (drone.getBatteryLevel() < 20) {
            drone.setStatus("LOW_BATTERY");
            sb.append("\n\n⚠  WARNING: Battery critically low!");
        }

        log("▶  Delivery simulated for Mission M" + mission.getMissionId() + " | Battery left: " + String.format("%.1f", drone.getBatteryLevel()) + "%");
        showInfoDialog("Simulation Complete", sb.toString());
        refreshAll();
    }
    private void simulateObstacle() {
        if (active.isEmpty()) { err("No active missions."); return; }
        DeliveryMission mission = pickMission(active, "Simulate Obstacle");
        if (mission == null) return;
        if (mission.getRoute() == null || mission.getRoute().size() < 2) { err("Mission has no route yet."); return; }

        List<String> routeOptions = new ArrayList<>(mission.getRoute());
        routeOptions.remove(0);
        if (routeOptions.isEmpty()) { err("No blockable waypoints on route."); return; }

        JComboBox<String> obsBox = darkCombo(routeOptions.toArray(new String[0]));
        JPanel form = darkForm();
        form.add(darkLabel("Current Route: " + mission.getRoute()));
        form.add(new JLabel());
        form.add(darkLabel("Block Waypoint:"));
        form.add(obsBox);

        int res = showDarkDialog(form, "Simulate Obstacle — M" + mission.getMissionId(), "Apply Obstacle");
        if (res != JOptionPane.OK_OPTION) return;

        String obstacle = (String) obsBox.getSelectedItem();
        List<String> newRoute = new RouteBacktracker().handleObstacle(airspaceGraph, mission.getRoute(), obstacle, mission.getDestination());

        if (newRoute == null || newRoute.isEmpty()) {
            log("❌  No alternate route around " + obstacle + ". Route unchanged.");
            err("No alternate route found around " + obstacle + ".");
        } else {
            mission.setRoute(newRoute);
            log("🔁  Obstacle at " + obstacle + " | New route for M" + mission.getMissionId() + ": " + newRoute);
            showInfoDialog("Rerouted!", "Obstacle: " + obstacle + "\nNew Route: " + newRoute);
            refreshAll();
        }
    }

    private void completeMission() {
        if (active.isEmpty()) { err("No active missions to complete."); return; }
        DeliveryMission mission = pickMission(active, "Complete Mission");
        if (mission == null) return;

        mission.setStatus("COMPLETED");
        Drone drone = droneRegistry.getDrone(mission.getAssignedDroneId());
        if (drone != null && !drone.getStatus().equals("LOW_BATTERY")) drone.setStatus("AVAILABLE");
        history.insert(mission);
        active.remove(mission);

        log("✅  Mission M" + mission.getMissionId() + " COMPLETED and saved to history.");
        refreshAll();
        tabs.setSelectedIndex(3);
    }

    private void searchMission() {
        String input = JOptionPane.showInputDialog(this,
                styledMsg("Enter Mission ID to search:"), "Search BST", JOptionPane.PLAIN_MESSAGE);
        if (input == null || input.trim().isEmpty()) return;
        int id;
        try { id = Integer.parseInt(input.trim()); } catch (Exception e) { err("Invalid ID."); return; }
        DeliveryMission m = history.search(id);
        if (m == null) { showInfoDialog("Not Found", "Mission M" + id + " not found in completed history."); }
        else { showInfoDialog("Mission Found ✅", m.toString()); }
    }
    private void showDroneTab()   { refreshDroneTable();   tabs.setSelectedIndex(0); }
    private void showPendingTab() { refreshPendingTable(); tabs.setSelectedIndex(1); }
    private void showHistoryTab() { refreshHistoryTable(); tabs.setSelectedIndex(3); }

    private void refreshDroneTable() {
        droneModel.setRowCount(0);
        for (Drone d : droneRegistry.getAllDrones())
            droneModel.addRow(new Object[]{
                    d.getDroneId(),
                    String.format("%.1f", d.getBatteryLevel()),
                    d.getCurrentLocation(),
                    d.getPayloadCapacity(),
                    d.getStatus()
            });
    }

    private void refreshPendingTable() {
        pendingModel.setRowCount(0);
        for (DeliveryMission m : pendingOrders.getAll())
            pendingModel.addRow(new Object[]{
                    "M" + m.getMissionId(), m.getSource(), m.getDestination(),
                    m.getPackageWeight(), m.getPriorityText(), m.getStatus()
            });
    }

    private void refreshActiveTable() {
        activeModel.setRowCount(0);
        for (DeliveryMission m : active)
            activeModel.addRow(new Object[]{
                    "M" + m.getMissionId(), m.getSource(), m.getDestination(),
                    m.getAssignedDroneId() == null ? "-" : m.getAssignedDroneId(),
                    m.getTotalDistance(), m.getStatus()
            });
    }

    private void refreshHistoryTable() {
        historyModel.setRowCount(0);
        for (DeliveryMission m : history.inorderList())
            historyModel.addRow(new Object[]{
                    "M" + m.getMissionId(), m.getSource(), m.getDestination(),
                    m.getAssignedDroneId() == null ? "-" : m.getAssignedDroneId(),
                    m.getTotalDistance(), m.getStatus()
            });
    }

    private void refreshAll() {
        refreshDroneTable(); refreshPendingTable(); refreshActiveTable(); refreshHistoryTable();
    }
    
    private JPanel darkForm() {
        JPanel p = new JPanel(new GridLayout(0, 2, 10, 10));
        p.setBackground(CARD);
        p.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        return p;
    }

    private JLabel darkLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(ACCENT);
        return l;
    }

    private JTextField darkField() {
        JTextField f = new JTextField();
        f.setBackground(new Color(15, 23, 42));
        f.setForeground(TEXT);
        f.setCaretColor(ACCENT);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        return f;
    }

    private JComboBox<String> darkCombo(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setBackground(new Color(15, 23, 42));
        cb.setForeground(TEXT);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cb.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        return cb;
    }

    private int showDarkDialog(JPanel form, String title, String okLabel) {
        UIManager.put("OptionPane.background", CARD);
        UIManager.put("Panel.background", CARD);
        UIManager.put("OptionPane.messageForeground", TEXT);
        return JOptionPane.showConfirmDialog(this, form, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    }

    private void showInfoDialog(String title, String msg) {
        JTextArea area = new JTextArea(msg);
        area.setEditable(false);
        area.setBackground(new Color(10, 14, 26));
        area.setForeground(new Color(134, 239, 172));
        area.setFont(new Font("Courier New", Font.PLAIN, 13));
        area.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        JScrollPane sp = new JScrollPane(area);
        sp.setPreferredSize(new Dimension(480, 220));
        sp.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        JOptionPane.showMessageDialog(this, sp, title, JOptionPane.PLAIN_MESSAGE);
    }

    private void err(String msg) {
        log("❌  ERROR: " + msg);
        JOptionPane.showMessageDialog(this, styledMsg(msg), "Error", JOptionPane.ERROR_MESSAGE);
    }

    private String styledMsg(String msg) { return msg; }

    private List<DeliveryMission> filterActive(String status) {
        List<DeliveryMission> r = new ArrayList<>();
        for (DeliveryMission m : active)
            if (status == null || m.getStatus().equals(status)) r.add(m);
        return r;
    }

    private DeliveryMission pickMission(List<DeliveryMission> list, String title) {
        if (list.size() == 1) return list.get(0);
        String[] options = list.stream().map(m -> "M" + m.getMissionId() + " | " + m.getSource() + " → " + m.getDestination() + " | " + m.getStatus()).toArray(String[]::new);
        JComboBox<String> cb = darkCombo(options);
        JPanel p = darkForm();
        p.add(darkLabel("Select Mission:")); p.add(cb);
        int res = showDarkDialog(p, title, "Select");
        if (res != JOptionPane.OK_OPTION) return null;
        return list.get(cb.getSelectedIndex());
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(SkyPathGUI::new);
    }
}