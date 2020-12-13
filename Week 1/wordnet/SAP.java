/* *****************************************************************************
 *  Name: Ihor Nikora
 *  Date: 2020-12-12
 *  Description: Shortest ancestral path. An ancestral path between two vertices
 *  v and w in a digraph is a directed path from v to a common ancestor x,
 *  together with a directed path from w to the same ancestor x. A shortest
 *  ancestral path is an ancestral path of minimum total length. An ancestral
 *  path is a path, but not a directed path.
 **************************************************************************** */

import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class SAP {
    private final Digraph digraph;
    private final HashMap<Entry<Integer, Integer>, Integer> lenCache;
    private final HashMap<Map.Entry<Integer, Integer>, Integer> ansCache;

    public SAP(Digraph G) {
        if (G == null) {
            throw new IllegalArgumentException("called SAP() with null G");
        }

        this.digraph = new Digraph(G);
        this.lenCache = new HashMap<Entry<Integer, Integer>, Integer>();
        this.ansCache = new HashMap<Entry<Integer, Integer>, Integer>();
    }

    public int length(int v, int w) {
        validateVertex(v);
        validateVertex(w);

        Entry<Integer, Integer> key1 = new SimpleEntry<>(v, w);
        if (lenCache.containsKey(key1)) {
            return lenCache.get(key1);
        }

        Entry<Integer, Integer> key2 = new SimpleEntry<>(w, v);
        if (lenCache.containsKey(key2)) {
            return lenCache.get(key2);
        }

        BreadthFirstDirectedPaths vp = new BreadthFirstDirectedPaths(this.digraph, v);
        BreadthFirstDirectedPaths wp = new BreadthFirstDirectedPaths(this.digraph, w);

        Queue<Integer> queue = new Queue<Integer>();
        queue.enqueue(v);
        queue.enqueue(w);

        boolean[] visited = new boolean[this.digraph.V()];
        visited[v] = true;
        visited[w] = true;

        int length = Integer.MAX_VALUE;

        while (!queue.isEmpty()) {
            int cur = queue.dequeue();
            if (wp.hasPathTo(cur) && vp.hasPathTo(cur))
                length = Math.min(length, vp.distTo(cur) + wp.distTo(cur));

            for (int next : this.digraph.adj(cur)) {
                if (!visited[next]) {
                    visited[next] = true;
                    queue.enqueue(next);
                }
            }
        }

        if (length == Integer.MAX_VALUE) {
            lenCache.put(key1, -1);
            lenCache.put(key2, -1);
            return -1;
        }

        lenCache.put(key1, length);
        lenCache.put(key2, length);
        return length;
    }

    public int ancestor(int v, int w) {
        validateVertex(v);
        validateVertex(w);

        Entry<Integer, Integer> key1 = new SimpleEntry<>(v, w);
        if (ansCache.containsKey(key1)) {
            return ansCache.get(key1);
        }

        Entry<Integer, Integer> key2 = new SimpleEntry<>(w, v);
        if (ansCache.containsKey(key2)) {
            return ansCache.get(key2);
        }

        BreadthFirstDirectedPaths vp = new BreadthFirstDirectedPaths(this.digraph, v);
        BreadthFirstDirectedPaths wp = new BreadthFirstDirectedPaths(this.digraph, w);

        Queue<Integer> queue = new Queue<Integer>();
        queue.enqueue(v);
        queue.enqueue(w);

        boolean[] visited = new boolean[this.digraph.V()];
        visited[v] = true;
        visited[w] = true;

        int minpath = Integer.MAX_VALUE;
        int ans = Integer.MAX_VALUE;

        while (!queue.isEmpty()) {
            int cur = queue.dequeue();
            if (wp.hasPathTo(cur) && vp.hasPathTo(cur)) {
                if (minpath > wp.distTo(cur) + vp.distTo(cur)) {
                    minpath = wp.distTo(cur) + vp.distTo(cur);
                    ans = cur;
                }
            }

            for (int next : this.digraph.adj(cur)) {
                if (!visited[next]) {
                    visited[next] = true;
                    queue.enqueue(next);
                }
            }
        }

        if (ans == Integer.MAX_VALUE) {
            ansCache.put(key1, -1);
            ansCache.put(key2, -1);
            return -1;
        }

        ansCache.put(key1, ans);
        ansCache.put(key2, ans);
        return ans;
    }

    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (validateVertices(v) == 0)
            return -1;

        if (validateVertices(w) == 0)
            return -1;

        BreadthFirstDirectedPaths vp = new BreadthFirstDirectedPaths(this.digraph, v);
        BreadthFirstDirectedPaths wp = new BreadthFirstDirectedPaths(this.digraph, w);

        Queue<Integer> queue = new Queue<Integer>();
        boolean[] visited = new boolean[this.digraph.V()];

        for (int v1 : v) {
            queue.enqueue(v1);
            visited[v1] = true;
        }

        for (int w1 : w) {
            queue.enqueue(w1);
            visited[w1] = true;
        }
        int length = Integer.MAX_VALUE;

        while (!queue.isEmpty()) {
            int cur = queue.dequeue();
            if (wp.hasPathTo(cur) && vp.hasPathTo(cur))
                length = Math.min(length, vp.distTo(cur) + wp.distTo(cur));

            for (int next : this.digraph.adj(cur)) {
                if (!visited[next]) {
                    visited[next] = true;
                    queue.enqueue(next);
                }
            }
        }

        if (length == Integer.MAX_VALUE) {
            return -1;
        }

        return length;
    }

    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (validateVertices(v) == 0)
            return -1;

        if (validateVertices(w) == 0)
            return -1;

        BreadthFirstDirectedPaths vp = new BreadthFirstDirectedPaths(this.digraph, v);
        BreadthFirstDirectedPaths wp = new BreadthFirstDirectedPaths(this.digraph, w);

        Queue<Integer> queue = new Queue<Integer>();
        boolean[] visited = new boolean[this.digraph.V()];

        for (int v1 : v) {
            queue.enqueue(v1);
            visited[v1] = true;
        }

        for (int w1 : w) {
            queue.enqueue(w1);
            visited[w1] = true;
        }

        int minpath = Integer.MAX_VALUE;
        int ans = Integer.MAX_VALUE;

        while (!queue.isEmpty()) {
            int cur = queue.dequeue();
            if (wp.hasPathTo(cur) && vp.hasPathTo(cur)) {
                if (minpath > wp.distTo(cur) + vp.distTo(cur)) {
                    minpath = wp.distTo(cur) + vp.distTo(cur);
                    ans = cur;
                }
            }

            for (int next : this.digraph.adj(cur)) {
                if (!visited[next]) {
                    visited[next] = true;
                    queue.enqueue(next);
                }
            }
        }

        if (ans == Integer.MAX_VALUE) {
            return -1;
        }

        return ans;
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        StdOut.println(G.V());
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }

    private void validateVertex(int v) {
        if (v < 0 || v > this.digraph.V())
            throw new IllegalArgumentException("v out of range");
    }

    private int validateVertices(Iterable<Integer> vertices) {
        if (vertices == null)
            throw new IllegalArgumentException("argument is null");

        int count = 0;
        for (Integer v : vertices) {
            if (v == null) {
                throw new IllegalArgumentException("vertex is null");
            }
            count++;
        }

        return count;
    }
}
