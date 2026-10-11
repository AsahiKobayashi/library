/** Integerの各モノイド、SegTree、遅延セグメント木を検証する。 */
class IntegerLibraryTest {
    /**
     * 値が等しいことを検証する。
     * @param expected 期待する値
     * @param actual 実際の値
     */
    static void eq(Object expected, Object actual) {
        if (!java.util.Objects.equals(expected, actual))
            throw new AssertionError("expected=" + expected + ", actual=" + actual);
    }

    /**
     * int配列から区間和・要素数の組へ変換する。
     * @param a 元の配列
     * @return IntSumLenのリスト
     */
    static java.util.List<IntSumLen> values(int[] a) {
        java.util.List<IntSumLen> list = new java.util.ArrayList<>();
        for (int v : a) list.add(IntSumLen.leaf(v));
        return list;
    }

    /** 定番のIntegerモノイドを検証する。 */
    static void monoids() {
        eq(0, new IntegerSumMonoid().e());
        eq(6, new IntegerSumMonoid().op(2, 4));
        eq(1, new IntegerProductMonoid().e());
        eq(12, new IntegerProductMonoid().op(3, 4));
        eq(Integer.MAX_VALUE, new IntegerMinMonoid().e());
        eq(2, new IntegerMinMonoid().op(2, 4));
        eq(Integer.MIN_VALUE, new IntegerMaxMonoid().e());
        eq(4, new IntegerMaxMonoid().op(2, 4));
        eq(6, new IntegerGcdMonoid().op(18, 24));
        eq(0, new IntegerGcdMonoid().op(0, 0));
        eq(7, new IntegerXorMonoid().op(2, 5));
        eq(-1, new IntegerAndMonoid().e());
        eq(2, new IntegerAndMonoid().op(3, 6));
        eq(0, new IntegerOrMonoid().e());
        eq(7, new IntegerOrMonoid().op(3, 6));

        // Javaのintとしてオーバーフローする。
        eq(Integer.MIN_VALUE, new IntegerSumMonoid().op(Integer.MAX_VALUE, 1));

        SegTree<Integer> s = new SegTree<>(
            java.util.Arrays.asList(4, 1, 7), new IntegerSumMonoid());
        eq(12, s.prod(0, 3));
        s.set(1, 8);
        eq(19, s.allProd());
        SegTree<Integer> minimum = new SegTree<>(
            java.util.Arrays.asList(4, 1, 7), new IntegerMinMonoid());
        eq(1, minimum.prod(0, 3));
    }

    /** 区間加算・区間代入の基本動作と空配列を検証する。 */
    static void lazyBasic() {
        LazySegTree<IntSumLen, Integer> add = new LazySegTree<>(
            values(new int[]{1, 2, 3}), new IntRangeAddSumAction());
        eq(new IntSumLen(6, 3), add.allProd());
        add.apply(0, 2, 4);
        eq(new IntSumLen(14, 3), add.allProd());
        add.apply(2, -3);
        eq(new IntSumLen(11, 3), add.prod(0, 3));
        add.set(1, IntSumLen.leaf(9));
        eq(new IntSumLen(14, 2), add.prod(0, 2));
        eq(new IntSumLen(0, 0), add.prod(2, 2));
        eq(new IntSumLen(0, 1), add.get(2));

        LazySegTree<IntSumLen, Integer> assign = new LazySegTree<>(
            values(new int[]{1, 2, 3, 4}), new IntRangeAssignSumAction());
        assign.apply(0, 4, 7);
        assign.apply(1, 3, 0);
        eq(new IntSumLen(14, 4), assign.allProd());
        assign.apply(0, 4, null);
        eq(new IntSumLen(14, 4), assign.allProd());
        assign.apply(3, -5);
        eq(new IntSumLen(2, 4), assign.allProd());

        LazySegTree<IntSumLen, Integer> empty =
            new LazySegTree<>(0, new IntRangeAddSumAction());
        eq(new IntSumLen(0, 0), empty.allProd());
        eq(new IntSumLen(0, 0), empty.prod(0, 0));
    }

    /** 単純配列とのランダム比較で区間演算と境界条件を検証する。 */
    static void randomized() {
        java.util.Random rand = new java.util.Random(1011);
        for (int n = 0; n <= 32; n++) {
            for (boolean assign : new boolean[]{false, true}) {
                int[] a = new int[n];
                for (int i = 0; i < n; i++) a[i] = rand.nextInt(101) - 50;
                MonoidAction<IntSumLen, Integer> action =
                    assign ? new IntRangeAssignSumAction() : new IntRangeAddSumAction();
                LazySegTree<IntSumLen, Integer> tree = new LazySegTree<>(values(a), action);
                for (int t = 0; t < 400; t++) {
                    int l = rand.nextInt(n + 1), r = rand.nextInt(n + 1);
                    if (l > r) { int tmp = l; l = r; r = tmp; }
                    int x = rand.nextInt(101) - 50;
                    int mode = rand.nextInt(5);
                    if (mode == 0) {
                        tree.apply(l, r, x);
                        for (int i = l; i < r; i++) a[i] = assign ? x : a[i] + x;
                    } else if (mode == 1 && n > 0) {
                        int i = rand.nextInt(n);
                        tree.apply(i, x);
                        a[i] = assign ? x : a[i] + x;
                    } else if (mode == 2 && n > 0) {
                        int i = rand.nextInt(n);
                        tree.set(i, IntSumLen.leaf(x));
                        a[i] = x;
                    } else if (mode == 3 && n > 0) {
                        int i = rand.nextInt(n);
                        eq(IntSumLen.leaf(a[i]), tree.get(i));
                    } else {
                        int expected = 0;
                        for (int i = l; i < r; i++) expected += a[i];
                        eq(new IntSumLen(expected, r - l), tree.prod(l, r));
                    }
                    if (t % 30 == 0) {
                        int all = 0;
                        for (int v : a) all += v;
                        eq(new IntSumLen(all, n), tree.allProd());
                    }
                }
            }
        }
    }

    /** すべてのInteger関連テストを実行する。 */
    public static void main(String[] args) {
        monoids();
        lazyBasic();
        randomized();
        System.out.println("Integer monoids and lazy tree tests passed");
    }
}
