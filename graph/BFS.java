class BFS {
    // g[v] is a list of adjacent vertices
    static int[] dist(int[][] g, int start) {
        int n = g.length;
        int[] d = new int[n], q = new int[n];
        java.util.Arrays.fill(d, -1);
        int head = 0, tail = 0;
        d[start] = 0;
        q[tail++] = start;
        while (head < tail) {
            int v = q[head++];
            for (int to : g[v]) {
                if (d[to] != -1) continue;
                d[to] = d[v] + 1;
                q[tail++] = to;
            }
        }
        return d;
    }
}
