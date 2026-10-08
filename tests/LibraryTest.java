class LibraryTest {
    static void eq(long expected, long actual) {
        if (expected != actual) throw new AssertionError(expected + " != " + actual);
    }

    public static void main(String[] args) {
        DSU dsu = new DSU(4);
        eq(4, dsu.groups());
        if (!dsu.merge(0, 1) || dsu.merge(1, 0)) throw new AssertionError();
        if (!dsu.same(0, 1) || dsu.same(0, 2)) throw new AssertionError();
        eq(2, dsu.size(1));
        eq(3, dsu.groups());

        FenwickTree fw = new FenwickTree(4);
        fw.add(0, 5);
        fw.add(2, -2);
        eq(3, fw.sum(4));
        eq(-2, fw.sum(1, 3));
        eq(0, fw.sum(0));

        SegTree seg = new SegTree(new long[]{1, 2, 3, 4});
        eq(5, seg.prod(1, 3));
        seg.set(2, 10);
        eq(17, seg.allProd());
        eq(0, seg.prod(2, 2));
        eq(10, seg.get(2));

        int[][] g = {{1, 2}, {0, 3}, {0}, {1}, {}};
        int[] bfs = BFS.dist(g, 0);
        eq(2, bfs[3]);
        eq(-1, bfs[4]);

        Dijkstra.Edge[][] wg = {
            {new Dijkstra.Edge(1, 5), new Dijkstra.Edge(2, 2)},
            {new Dijkstra.Edge(3, 1)},
            {new Dijkstra.Edge(1, 1)},
            {},
            {}
        };
        long[] dist = Dijkstra.dist(wg, 0);
        eq(3, dist[1]);
        eq(4, dist[3]);
        eq(Dijkstra.INF, dist[4]);

        eq(1024, ModMath.pow(2, 10, 1000000007L));
        eq(4, ModMath.inv(2, 7));
        eq(0, ModMath.pow(0, 0, 1));
        System.out.println("All tests passed");
    }
}
