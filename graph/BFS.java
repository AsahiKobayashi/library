/** 重みなしグラフの幅優先探索。 */
class BFS {
    /**
     * 始点から各頂点までの最短距離を求める。
     * @param g 隣接リスト。g[v]はvから移動できる頂点一覧
     * @param start 始点
     * @return 各頂点までの辺数。到達不能なら-1
     */
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
