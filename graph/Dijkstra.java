/** 非負辺重みグラフのダイクストラ法。 */
class Dijkstra {
    /** 到達不能を表す距離の上限。 */
    static final long INF = Long.MAX_VALUE / 4;

    /** 行き先と辺の重みを持つ有向辺。 */
    static class Edge {
        final int to;
        final long cost;

        /** @param to 行き先 @param cost 非負の辺重み */
        Edge(int to, long cost) { this.to = to; this.cost = cost; }
    }

    /**
     * 始点から各頂点までの最短距離を求める。
     * @param g 隣接リスト。辺重みは非負、計算する距離はINF未満を想定
     * @param start 始点
     * @return 最短距離。到達不能、または距離がINF以上ならINF
     */
    static long[] dist(Edge[][] g, int start) {
        long[] d = new long[g.length];
        java.util.Arrays.fill(d, INF);
        java.util.PriorityQueue<long[]> pq =
            new java.util.PriorityQueue<>(java.util.Comparator.comparingLong(a -> a[0]));
        d[start] = 0;
        pq.add(new long[]{0, start});
        while (!pq.isEmpty()) {
            long[] cur = pq.poll();
            long cost = cur[0];
            int v = (int) cur[1];
            if (cost != d[v]) continue;
            for (Edge e : g[v]) {
                if (e.cost < 0) throw new IllegalArgumentException("negative edge");
                if (cost > INF - e.cost) continue;
                long nd = cost + e.cost;
                if (nd >= d[e.to]) continue;
                d[e.to] = nd;
                pq.add(new long[]{nd, e.to});
            }
        }
        return d;
    }
}
