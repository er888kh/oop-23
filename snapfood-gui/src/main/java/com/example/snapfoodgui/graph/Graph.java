package com.example.snapfoodgui.graph;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class Graph {
    public String name;
    public ArrayList<Node> nodes;

    public Graph(String name, String dataPath) throws FileNotFoundException {
        this.name = name;
        var inp = new FileReader(dataPath);
        var sc = new Scanner(inp);
        int n, m;
        n = sc.nextInt();
        m = sc.nextInt();
        this.nodes = new ArrayList<>();
        nodes.add(new Node(0));
        for(int i = 1; i <= n; i++) {
            nodes.add(new Node(i));
        }
        for(int i = 0; i < m; i++) {
            int u, v, w;
            u = sc.nextInt();
            v = sc.nextInt();
            w = sc.nextInt();
            nodes.get(u).adj.add(new Edge(u, v, w));
            nodes.get(v).adj.add(new Edge(v, u, w));
        }
    }

    public ArrayList<Edge> SSSP(Node start, Node end, EdgeConstraint cnst) {
        PriorityQueue<NodeWrapper> pq = new PriorityQueue<>(new NodeWrapper(start, 0));
        boolean vis[] = new boolean[nodes.size()];
        double dist[] = new double[nodes.size()];
        Edge parent[] = new Edge[nodes.size()];
        Arrays.fill(vis, false);
        Arrays.fill(dist, Double.MAX_VALUE / 3.0);
        dist[start.id] = 0;
        while(pq.size() > 0 && !vis[end.id]) {
            NodeWrapper nw = pq.poll();
            if(vis[nw.id]) {
                continue;
            }
            vis[nw.id] = true;
            for(Edge e:nw.adj) {
                if(!cnst.check(e) || vis[e.v]) {
                    continue;
                }
                double dr = nw.cost + e.w;
                if(dr < dist[e.v]) {
                    dist[e.v] = dr;
                    parent[e.v] = e;
                    pq.add(new NodeWrapper(nodes.get(e.v), dr));
                }
            }
        }
        if(vis[end.id] == false) {
            return null;
        }
        ArrayList<Edge> result = new ArrayList<>();
        Edge curr = parent[end.id];
        while(curr != null) {
            result.add(curr);
            curr = parent[curr.u];
        }
        return result;
    }

    public ArrayList<NodeWrapper> RestaurantsNearMe(Node source, EdgeConstraint cnst, int count) {
        PriorityQueue<NodeWrapper> pq = new PriorityQueue<>(new NodeWrapper(source, 0));
        boolean vis[] = new boolean[nodes.size()];
        double dist[] = new double[nodes.size()];
        Edge parent[] = new Edge[nodes.size()];
        Arrays.fill(vis, false);
        Arrays.fill(dist, Double.MAX_VALUE / 3.0);
        dist[source.id] = 0;
        var result = new ArrayList<NodeWrapper>();
        while(pq.size() > 0 && result.size() < count) {
            NodeWrapper nw = pq.poll();
            if(vis[nw.id]) {
                continue;
            }
            if(nw.type.equals("R")) { // A new restaurant found
                result.add(nw);
            }
            vis[nw.id] = true;
            for(Edge e:nw.adj) {
                if(!cnst.check(e) || vis[e.v]) {
                    continue;
                }
                double dr = nw.cost + e.w;
                if(dr < dist[e.v]) {
                    dist[e.v] = dr;
                    parent[e.v] = e;
                    pq.add(new NodeWrapper(nodes.get(e.v), dr));
                }
            }
        }
        return result;
    }
}

