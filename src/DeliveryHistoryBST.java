public class DeliveryHistoryBST {
    private BSTNode root;

    public void insert(DeliveryMission mission) { root = insertHelper(root, mission); }

    private BSTNode insertHelper(BSTNode node, DeliveryMission mission) {
        if (node == null) return new BSTNode(mission);
        if (mission.getMissionId() < node.mission.getMissionId()) node.left = insertHelper(node.left, mission);
        else if (mission.getMissionId() > node.mission.getMissionId()) node.right = insertHelper(node.right, mission);
        return node;
    }

    public DeliveryMission search(int missionId) { return searchHelper(root, missionId); }

    private DeliveryMission searchHelper(BSTNode node, int missionId) {
        if (node == null) return null;
        if (missionId == node.mission.getMissionId()) return node.mission;
        if (missionId < node.mission.getMissionId()) return searchHelper(node.left, missionId);
        return searchHelper(node.right, missionId);
    }

    public void displayInorder() {
        if (root == null) { System.out.println("\nNo completed missions yet."); return; }
        System.out.println("\n--- Completed Delivery History (sorted by Mission ID) ---");
        inorderHelper(root);
    }

    private void inorderHelper(BSTNode node) {
        if (node == null) return;
        inorderHelper(node.left);
        System.out.println(node.mission);
        inorderHelper(node.right);
    }
}