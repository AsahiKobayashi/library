/** ライブラリの基本動作を確認する簡易テスト。 */
class LibraryTest {
    /** 期待値と実際の値が一致することを確認する。 */
    static void eq(Object expected, Object actual) {
        if (!java.util.Objects.equals(expected, actual))
            throw new AssertionError(expected + " != " + actual);
    }

    /** 各データ構造とアルゴリズムの代表的な動作をテストする。 */
    public static void main(String[] args) {
        DSU dsu = new DSU(4);
        eq(4, dsu.groups());
        if (!dsu.merge(0, 1) || dsu.merge(1, 0)) throw new AssertionError();
        if (!dsu.same(0, 1) || dsu.same(0, 2)) throw new AssertionError();
        eq(2, dsu.size(1));
        eq(3, dsu.groups());

        CommutativeGroup<Long> addition = new CommutativeGroup<>() {
            public Long identity() { return 0L; }
            public Long combine(Long a, Long b) { return a + b; }
            public Long inverse(Long a) { return -a; }
        };
        FenwickTree<Long> fw = new FenwickTree<>(4, addition);
        fw.add(0, 5L); fw.add(2, -2L);
        eq(3L, fw.sum(4));
        eq(-2L, fw.sum(1, 3));
        eq(0L, fw.sum(0));

        Monoid<Long> sum = new Monoid<>() {
            public Long identity() { return 0L; }
            public Long combine(Long a, Long b) { return a + b; }
        };
        SegTree<Long> seg = new SegTree<>(java.util.Arrays.asList(1L, 2L, 3L, 4L), sum);
        eq(5L, seg.prod(1, 3));
        seg.set(2, 10L);
        eq(17L, seg.allProd());
        eq(0L, seg.prod(2, 2));
        eq(10L, seg.get(2));

        Monoid<String> concat = new Monoid<>() {
            public String identity() { return ""; }
            public String combine(String a, String b) { return a + b; }
        };
        SegTree<String> words = new SegTree<>(java.util.Arrays.asList("a", "b", "c"), concat);
        eq("abc", words.prod(0, 3));
        eq("bc", words.prod(1, 3));
        words.set(1, "X");
        eq("aXc", words.allProd());

        CommutativeGroup<Integer> xor = new CommutativeGroup<>() {
            public Integer identity() { return 0; }
            public Integer combine(Integer a, Integer b) { return a ^ b; }
            public Integer inverse(Integer a) { return a; }
        };
        FenwickTree<Integer> fx = new FenwickTree<>(3, xor);
        fx.add(0, 3); fx.add(1, 5); fx.add(2, 6);
        eq(3 ^ 5 ^ 6, fx.sum(3));
        eq(5 ^ 6, fx.sum(1, 3));

        int[][] g = {{1, 2}, {0, 3}, {0}, {1}, {}};
        int[] bfs = BFS.dist(g, 0);
        eq(2, bfs[3]); eq(-1, bfs[4]);

        Dijkstra.Edge[][] wg = {
            {new Dijkstra.Edge(1, 5), new Dijkstra.Edge(2, 2)},
            {new Dijkstra.Edge(3, 1)},
            {new Dijkstra.Edge(1, 1)}, {}, {}
        };
        long[] dist = Dijkstra.dist(wg, 0);
        eq(3L, dist[1]); eq(4L, dist[3]); eq(Dijkstra.INF, dist[4]);

        eq(1024L, ModMath.pow(2, 10, 1000000007L));
        eq(4L, ModMath.inv(2, 7));
        eq(0L, ModMath.pow(0, 0, 1));
        System.out.println("All tests passed");
    }
}
