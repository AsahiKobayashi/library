/**
 * モノイド作用を用いた遅延セグメント木。
 * 点代入・区間更新・区間集約を各O(log n)で処理する。
 * 0-indexed、区間は [l, r) 。
 * @param <S> 集約値の型
 * @param <F> 遅延更新の型
 */
class LazySegTree<S, F> {
    private final int n, size, log;
    private final Object[] d, lz;
    private final MonoidAction<S, F> act;

    /**
     * 全要素を単位元で初期化する。
     * @param n 要素数（0以上）
     * @param act モノイドと更新作用の定義
     */
    LazySegTree(int n, MonoidAction<S, F> act) {
        if (n < 0) throw new IllegalArgumentException("n must be non-negative");
        this.n = n;
        this.act = java.util.Objects.requireNonNull(act);
        int s = 1, h = 0;
        while (s < n) { s <<= 1; h++; }
        size = s;
        log = h;
        d = new Object[2 * size];
        lz = new Object[size];
        java.util.Arrays.fill(d, act.e());
        java.util.Arrays.fill(lz, act.id());
    }

    /**
     * 初期配列から構築する。
     * @param a 初期値
     * @param act モノイドと更新作用の定義
     */
    LazySegTree(java.util.List<? extends S> a, MonoidAction<S, F> act) {
        this(a.size(), act);
        for (int i = 0; i < n; i++) d[size + i] = a.get(i);
        for (int i = size - 1; i >= 1; i--) pull(i);
    }

    /** @return k番目のノードの集約値。 */
    @SuppressWarnings("unchecked")
    private S val(int k) { return (S) d[k]; }

    /** @return k番目のノードが持つ遅延更新。 */
    @SuppressWarnings("unchecked")
    private F lazy(int k) { return (F) lz[k]; }

    /** 2つの子ノードを結合して親ノードを更新する。 */
    private void pull(int k) { d[k] = act.op(val(k << 1), val(k << 1 | 1)); }

    /** ノードに更新fを適用し、子へ渡す更新を合成する。 */
    private void allApply(int k, F f) {
        d[k] = act.mapping(f, val(k));
        if (k < size) lz[k] = act.composition(f, lazy(k));
    }

    /** 遅延更新を子に伝播する。 */
    private void push(int k) {
        allApply(k << 1, lazy(k));
        allApply(k << 1 | 1, lazy(k));
        lz[k] = act.id();
    }

    /**
     * i番目の要素をxで置き換える。
     * @param i 更新する添字
     * @param x 新しい値
     */
    void set(int i, S x) {
        if (i < 0 || i >= n) throw new IndexOutOfBoundsException();
        i += size;
        for (int j = log; j >= 1; j--) push(i >> j);
        d[i] = x;
        for (int j = 1; j <= log; j++) pull(i >> j);
    }

    /**
     * i番目の要素を取得する。
     * @param i 添字
     * @return 遅延更新を反映した値
     */
    S get(int i) {
        if (i < 0 || i >= n) throw new IndexOutOfBoundsException();
        i += size;
        for (int j = log; j >= 1; j--) push(i >> j);
        return val(i);
    }

    /**
     * 区間[l, r)の値を左から右の順序で結合する。
     * @param l 左端（含む）
     * @param r 右端（含まない）
     * @return 区間の集約値。空区間なら単位元
     */
    S prod(int l, int r) {
        if (l < 0 || l > r || r > n) throw new IndexOutOfBoundsException();
        if (l == r) return act.e();
        l += size;
        r += size;
        for (int j = log; j >= 1; j--) {
            if (((l >> j) << j) != l) push(l >> j);
            if (((r >> j) << j) != r) push((r - 1) >> j);
        }
        S left = act.e(), right = act.e();
        while (l < r) {
            if ((l & 1) != 0) left = act.op(left, val(l++));
            if ((r & 1) != 0) right = act.op(val(--r), right);
            l >>= 1;
            r >>= 1;
        }
        return act.op(left, right);
    }

    /** @return 全要素の集約値。 */
    S allProd() { return val(1); }

    /**
     * i番目の要素に更新fを適用する。
     * @param i 添字
     * @param f 更新操作
     */
    void apply(int i, F f) {
        if (i < 0 || i >= n) throw new IndexOutOfBoundsException();
        i += size;
        for (int j = log; j >= 1; j--) push(i >> j);
        d[i] = act.mapping(f, val(i));
        for (int j = 1; j <= log; j++) pull(i >> j);
    }

    /**
     * 区間[l, r)のすべての要素に更新fを適用する。
     * @param l 左端（含む）
     * @param r 右端（含まない）
     * @param f 更新操作
     */
    void apply(int l, int r, F f) {
        if (l < 0 || l > r || r > n) throw new IndexOutOfBoundsException();
        if (l == r) return;
        l += size;
        r += size;
        for (int j = log; j >= 1; j--) {
            if (((l >> j) << j) != l) push(l >> j);
            if (((r >> j) << j) != r) push((r - 1) >> j);
        }
        int l0 = l, r0 = r;
        while (l < r) {
            if ((l & 1) != 0) allApply(l++, f);
            if ((r & 1) != 0) allApply(--r, f);
            l >>= 1;
            r >>= 1;
        }
        l = l0;
        r = r0;
        for (int j = 1; j <= log; j++) {
            if (((l >> j) << j) != l) pull(l >> j);
            if (((r >> j) << j) != r) pull((r - 1) >> j);
        }
    }

    /**
     * g(prod(l, r))がtrueである最大のrを求める。
     * gは単位元に対してtrue、区間を伸ばすとtrueからfalseへ単調に変化する必要がある。
     * @param l 左端（含む）
     * @param g 判定条件
     * @return 条件を満たす最大の右端
     */
    int maxRight(int l, java.util.function.Predicate<S> g) {
        if (l < 0 || l > n) throw new IndexOutOfBoundsException();
        if (!g.test(act.e())) throw new IllegalArgumentException("g(e()) must be true");
        if (l == n) return n;
        l += size;
        for (int j = log; j >= 1; j--) push(l >> j);
        S sm = act.e();
        do {
            while ((l & 1) == 0) l >>= 1;
            if (!g.test(act.op(sm, val(l)))) {
                while (l < size) {
                    push(l);
                    l <<= 1;
                    if (g.test(act.op(sm, val(l)))) {
                        sm = act.op(sm, val(l));
                        l++;
                    }
                }
                return l - size;
            }
            sm = act.op(sm, val(l));
            l++;
        } while ((l & -l) != l);
        return n;
    }

    /**
     * g(prod(l, r))がtrueである最小のlを求める。
     * gは単位元に対してtrue、区間を左へ伸ばすとtrueからfalseへ単調に変化する必要がある。
     * @param r 右端（含まない）
     * @param g 判定条件
     * @return 条件を満たす最小の左端
     */
    int minLeft(int r, java.util.function.Predicate<S> g) {
        if (r < 0 || r > n) throw new IndexOutOfBoundsException();
        if (!g.test(act.e())) throw new IllegalArgumentException("g(e()) must be true");
        if (r == 0) return 0;
        r += size;
        for (int j = log; j >= 1; j--) push((r - 1) >> j);
        S sm = act.e();
        do {
            r--;
            while (r > 1 && (r & 1) != 0) r >>= 1;
            if (!g.test(act.op(val(r), sm))) {
                while (r < size) {
                    push(r);
                    r = (r << 1) | 1;
                    if (g.test(act.op(val(r), sm))) {
                        sm = act.op(val(r), sm);
                        r--;
                    }
                }
                return r + 1 - size;
            }
            sm = act.op(val(r), sm);
        } while ((r & -r) != r);
        return 0;
    }
}
