package graph;

import java.util.Comparator;

public class NodeWrapper extends Node implements Comparator<NodeWrapper> {
    public static final double EPS = 1e-3;
    double cost;

    public NodeWrapper(Node nd, double cst) {
        super(nd);
        this.cost = cst;
    }

    public int compare(NodeWrapper u, NodeWrapper v) {
        if (Math.abs(u.cost - v.cost) < EPS) {
            return Integer.compare(u.id, v.id);
        }
        return Double.compare(u.cost, v.cost);
    }
}
