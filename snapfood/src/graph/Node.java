package graph;

import java.util.ArrayList;
import java.util.Collection;

public class Node {
    public int id;
    // Use "R" for restaurant
    public String type;
    public ArrayList<Edge> adj;
    public Object auxData;

    public Node(int id) {
        this.id = id;
        this.adj = new ArrayList<>();
        this.auxData = null;
        this.type = null;
    }

    public Node(int id, Collection<Edge> adj) {
        this(id);
        this.adj.addAll(adj);
    }

    public Node(int id, String type) {
        this(id);
        this.type = type;
    }

    public Node(Node from) {
        this.id = from.id;
        this.type = from.type;
        this.adj = from.adj;
        this.auxData = from.auxData;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(this.id);
    }
}
