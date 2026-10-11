/** LazySegTreeと定番モノイドの境界条件・ランダムケースを検証する。 */
class LazySegTreeTest {
    /** 期待値と結果が等しいことを確認する。 */
    static void eq(Object expected, Object actual) {
        if (!java.util.Objects.equals(expected, actual))
            throw new AssertionError("expected=" + expected + ", actual=" + actual);
    }

    /** 1要素のlong値を長さ付きデータに変換する。 */
    static java.util.List<SumLen> values(long[] a) {
        java.util.List<SumLen> list = new java.util.ArrayList<>();
        for (long x : a) list.add(SumLen.leaf(x));
        return list;
    }

    /** 指定区間の和を単純に計算する。 */
    static long naive(long[] a, int l, int r) {
        long sum = 0;
        for (int i = l; i < r; i++) sum += a[i];
        return sum;
    }

    /** 定番のモノイドと作用の基本動作を確認する。 */
    static void presets() {
        eq(6L, new LongSumMonoid().op(2L, 4L));
        eq(1L, new LongProductMonoid().e());
        eq(2L, new LongMinMonoid().op(2L, 4L));
        eq(4L, new LongMaxMonoid().op(2L, 4L));
        eq(6L, new LongGcdMonoid().op(18L, 24L));
        eq(7L, new LongXorMonoid().op(2L, 5L));
        eq(true, new BooleanAndMonoid().e());
        eq(false, new BooleanOrMonoid().e());
        eq("ab", new StringConcatMonoid().op("a", "b"));
        eq(false, ((Object) new StringConcatMonoid()) instanceof CommutativeMonoid);
        SegTree<Long> s = new SegTree<>(java.util.Arrays.asList(7L, 2L, 5L), new LongMinMonoid());
        eq(2L, s.prod(0, 3));
        SegTree<String> t = new SegTree<>(java.util.Arrays.asList("x", "y"), new StringConcatMonoid());
        eq("xy", t.prod(0, 2));
    }

    /** 空配列・点更新・区間加算・区間代入の基本動作を確認する。 */
    static void basic() {
        LazySegTree<SumLen, Long> empty = new LazySegTree<>(0, new RangeAddSumAction());
        eq(new SumLen(0, 0), empty.allProd());
        eq(new SumLen(0, 0), empty.prod(0, 0));
        eq(0, empty.maxRight(0, s -> s.sum <= 0));
        eq(0, empty.minLeft(0, s -> s.sum <= 0));
        empty.apply(0, 0, 5L);

        LazySegTree<SumLen, Long> add = new LazySegTree<>(values(new long[]{1, 2, 3}), new RangeAddSumAction());
        eq(new SumLen(6, 3), add.allProd());
        add.apply(0, 2, 4L); // [5,6,3]
        eq(new SumLen(14, 3), add.allProd());
        add.apply(2, 10L); // [5,6,13]
        eq(new SumLen(13, 1), add.get(2));
        add.set(1, SumLen.leaf(9)); // [5,9,13]
        eq(new SumLen(27, 3), add.prod(0, 3));
        eq(new SumLen(0, 0), add.prod(2, 2));

        LazySegTree<SumLen, Long> assign = new LazySegTree<>(values(new long[]{1, 2, 3, 4}), new RangeAssignSumAction());
        assign.apply(0, 4, 7L);
        assign.apply(1, 3, 0L);
        eq(new SumLen(14, 4), assign.allProd());
        assign.apply(0, 4, null); // 代入なし
        eq(new SumLen(14, 4), assign.allProd());
        assign.apply(3, -5L);
        eq(new SumLen(2, 4), assign.allProd());
        eq(new SumLen(-5, 1), assign.get(3));
    }

    /** 正の値の配列でmaxRight/minLeftを単純計算と比較する。 */
    static void searches() {
        java.util.Random rd = new java.util.Random(55);
        for (int n = 0; n <= 18; n++) {
            long[] a = new long[n];
            LazySegTree<SumLen, Long> seg = new LazySegTree<>(values(a), new RangeAddSumAction());
            for (int t = 0; t < 80; t++) {
                int l = rd.nextInt(n + 1), r = rd.nextInt(n + 1);
                if (l > r) { int x = l; l = r; r = x; }
                long v = rd.nextInt(4);
                seg.apply(l, r, v);
                for (int i = l; i < r; i++) a[i] += v;
                long threshold = rd.nextInt(80);
                int start = rd.nextInt(n + 1);
                int maxRight = start;
                long x = 0;
                while (maxRight < n && x + a[maxRight] <= threshold) x += a[maxRight++];
                eq(maxRight, seg.maxRight(start, s -> s.sum <= threshold));
                int end = rd.nextInt(n + 1);
                int minLeft = end;
                x = 0;
                while (minLeft > 0 && x + a[minLeft - 1] <= threshold) x += a[--minLeft];
                eq(minLeft, seg.minLeft(end, s -> s.sum <= threshold));
            }
        }
    }

    /** 区間加算・区間代入を単純な配列実装とランダム比較する。 */
    static void randomUpdates() {
        java.util.Random rd = new java.util.Random(20261011);
        for (int n = 0; n <= 40; n++) {
            for (boolean assignment : new boolean[]{false, true}) {
                long[] a = new long[n];
                for (int i = 0; i < n; i++) a[i] = rd.nextInt(101) - 50;
                MonoidAction<SumLen, Long> action =
                    assignment ? new RangeAssignSumAction() : new RangeAddSumAction();
                LazySegTree<SumLen, Long> seg = new LazySegTree<>(values(a), action);
                for (int t = 0; t < 300; t++) {
                    int l = rd.nextInt(n + 1), r = rd.nextInt(n + 1);
                    if (l > r) { int x = l; l = r; r = x; }
                    long v = rd.nextInt(101) - 50;
                    int type = rd.nextInt(5);
                    if (type == 0) {
                        seg.apply(l, r, v);
                        for (int i = l; i < r; i++) a[i] = assignment ? v : a[i] + v;
                    } else if (type == 1 && n > 0) {
                        int i = rd.nextInt(n);
                        seg.apply(i, v);
                        a[i] = assignment ? v : a[i] + v;
                    } else if (type == 2 && n > 0) {
                        int i = rd.nextInt(n);
                        seg.set(i, SumLen.leaf(v));
                        a[i] = v;
                    } else if (type == 3 && n > 0) {
                        int i = rd.nextInt(n);
                        eq(new SumLen(a[i], 1), seg.get(i));
                    } else {
                        eq(new SumLen(naive(a, l, r), r - l), seg.prod(l, r));
                    }
                    if (t % 20 == 0) {
                        eq(new SumLen(naive(a, 0, n), n), seg.allProd());
                    }
                }
            }
        }
    }

    /** 全テストを実行する。 */
    public static void main(String[] args) {
        presets();
        basic();
        searches();
        randomUpdates();
        System.out.println("LazySegTree and monoid preset tests passed");
    }
}
