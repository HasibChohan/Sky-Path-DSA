import java.util.*;

public class DeliveryHistoryBST {
    private BSTNode root;

    public void insert(DeliveryMission m) { root = ins(root, m); }
    private BSTNode ins(BSTNode n, DeliveryMission m) {
        if (n == null) return new BSTNode(m);
        if (m.getMissionId() < n.mission.getMissionId()) n.left = ins(n.left, m);
        else if (m.getMissionId() > n.mission.getMissionId()) n.right = ins(n.right, m);
        return n;
    }

    public DeliveryMission search(int id) { return srch(root, id); }
    private DeliveryMission srch(BSTNode n, int id) {
        if (n == null) return null;
        if (id == n.mission.getMissionId()) return n.mission;
        return id < n.mission.getMissionId() ? srch(n.left, id) : srch(n.right, id);
    }

    public List<DeliveryMission> inorderList() {
        List<DeliveryMission> list = new ArrayList<>();
        inorder(root, list);
        return list;
    }
    private void inorder(BSTNode n, List<DeliveryMission> list) {
        if (n == null) return;
        inorder(n.left, list);
        list.add(n.mission);
        inorder(n.right, list);
    }
}