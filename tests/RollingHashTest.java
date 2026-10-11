/** RollingHashの区間抽出・連結・遅延更新・木との連携を検証する。 */
class RollingHashTest {
    /** 期待値と実際の値の一致を確認する。 */
    static void eq(Object expected, Object actual) {
        if (!java.util.Objects.equals(expected, actual))
            throw new AssertionError("expected=" + expected + ", actual=" + actual);
    }

    /** 文字列を左から順にハッシュとして結合する。 */
    static RollingHash.Hash hashOf(RollingHash h, String s) {
        RollingHash.Hash value = h.e();
        for (int i = 0; i < s.length(); i++) value = h.concat(value, h.ofChar(s.charAt(i)));
        return value;
    }

    /** ハッシュ、LCP、連結順序、繰り返し・加算の基本動作を検証する。 */
    static void basic() {
        RollingHash a = new RollingHash("abracadabra"), b = new RollingHash("cad");
        eq(a.hash(4, 7), b.hash(0, 3));
        eq(true, a.same(4, 7, b, 0, 3));
        eq(3, a.lcp(4, b, 0));
        eq(0, a.lcp(11, b, 0));
        eq(a.e(), a.hash(0, 0));
        eq(a.hash(0, 11), hashOf(a, "abracadabra"));
        eq(a.hash(0, 5), a.concat(a.hash(0, 3), a.hash(3, 5)));
        eq(false, a.concat(a.hash(0, 1), a.hash(1, 2)).equals(
                  a.concat(a.hash(1, 2), a.hash(0, 1))));
        eq(new RollingHash("ZZZZ").hash(0, 4), a.repeat('Z' + 1L, 4));
        eq(new RollingHash(new int[]{-2, -2, -2}).hash(0, 3), a.repeat(-2, 3));
        RollingHash c = new RollingHash(new long[]{3, -10, 4});
        eq(new RollingHash(new long[]{5, -8, 6}).hash(0, 3), c.add(c.hash(0, 3), 2L));
        eq(hashOf(a, "hello"), hashOf(new RollingHash(5), "hello"));
    }

    /** ランダムな配列の全区間を直接構築したハッシュと比較する。 */
    static void randomBasic() {
        java.util.Random random = new java.util.Random(731);
        for (int n = 0; n <= 32; n++) {
            int[] codes = new int[n];
            for (int i = 0; i < n; i++) codes[i] = random.nextInt(1001) - 500;
            RollingHash hash = new RollingHash(codes);
            for (int l = 0; l <= n; l++) for (int r = l; r <= n; r++) {
                int[] sub = java.util.Arrays.copyOfRange(codes, l, r);
                eq(new RollingHash(sub).hash(0, sub.length), hash.hash(l, r));
                int split = l + random.nextInt(r - l + 1);
                eq(hash.hash(l, r), hash.concat(hash.hash(l, split), hash.hash(split, r)));
            }
        }
    }

    /** SegTreeとLazySegTreeの結果を単純配列のハッシュと比較する。 */
    static void dynamic() {
        java.util.Random random = new java.util.Random(113);
        for (int n = 0; n <= 22; n++) {
            long[] values = new long[n];
            for (int i = 0; i < n; i++) values[i] = random.nextInt(101) - 50;
            RollingHash h = new RollingHash(n);
            java.util.ArrayList<RollingHash.Hash> leaves = new java.util.ArrayList<>();
            for (long v : values) leaves.add(h.of(v));
            SegTree<RollingHash.Hash> seg = new SegTree<>(leaves, new RollingHashMonoid(h));
            LazySegTree<RollingHash.Hash, Long> add = new LazySegTree<>(leaves, new RangeAddHashAction(h));
            LazySegTree<RollingHash.Hash, Long> assign = new LazySegTree<>(leaves, new RangeAssignHashAction(h));
            long[] arrAdd = values.clone(), arrAssign = values.clone();
            for (int it = 0; it < 220; it++) {
                int l = random.nextInt(n + 1), r = random.nextInt(n + 1);
                if (l > r) { int t = l; l = r; r = t; }
                int x = random.nextInt(101) - 50;
                int kind = random.nextInt(5);
                if (kind == 0) {
                    add.apply(l, r, (long) x);
                    for (int i = l; i < r; i++) arrAdd[i] += x;
                } else if (kind == 1) {
                    assign.apply(l, r, (long) x);
                    for (int i = l; i < r; i++) arrAssign[i] = x;
                } else if (kind == 2 && n > 0) {
                    int p = random.nextInt(n);
                    seg.set(p, h.of(x));
                    values[p] = x;
                    add.set(p, h.of(x));
                    arrAdd[p] = x;
                    assign.set(p, h.of(x));
                    arrAssign[p] = x;
                } else if (kind == 3 && n > 0) {
                    int p = random.nextInt(n);
                    add.apply(p, (long) x);
                    arrAdd[p] += x;
                    assign.apply(p, (long) x);
                    arrAssign[p] = x;
                }
                eq(new RollingHash(values).hash(l, r), seg.prod(l, r));
                eq(new RollingHash(arrAdd).hash(l, r), add.prod(l, r));
                eq(new RollingHash(arrAssign).hash(l, r), assign.prod(l, r));
                if (it % 17 == 0) {
                    eq(new RollingHash(arrAdd).hash(0, n), add.allProd());
                    eq(new RollingHash(arrAssign).hash(0, n), assign.allProd());
                }
            }
        }

        RollingHash h = new RollingHash(4);
        java.util.List<RollingHash.Hash> chars = java.util.Arrays.asList(
            h.ofChar('a'), h.ofChar('b'), h.ofChar('c'), h.ofChar('d'));
        LazySegTree<RollingHash.Hash, Long> replace =
            new LazySegTree<>(chars, new RangeAssignHashAction(h));
        replace.apply(1, 3, (long) 'Z' + 1);
        eq(new RollingHash("aZZd").hash(0, 4), replace.allProd());
        replace.apply(1, 3, null);
        eq(new RollingHash("aZZd").hash(0, 4), replace.allProd());
    }

    /** 全テストを実行する。 */
    public static void main(String[] args) {
        basic();
        randomBasic();
        dynamic();
        System.out.println("RollingHash tests passed");
    }
}
