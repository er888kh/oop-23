package graph;

public class Edge {
    public float w;
    public int u, v; // It is a directed edge, u -> v
    public Object auxData;

    public Edge(int u, int v) {
        this.u = u;
        this.v = v;
        this.w = 0;
        this.auxData = null;
    }

    public Edge(int u, int v, float w) {
        this(u, v);
        this.w = w;
    }

    public Edge(int u, int v, float w, Object auxData) {
        this(u, v, w);
        this.auxData = auxData;
    }
}
