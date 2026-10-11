/**
 * 任意のモノイドで区間集約するセグメント木。
 * 添字は0始まり、区間は半開区間 [l, r)。
 * @param <T> 要素の型
 */
class SegTree<T> {
    private final int n, size;
    private final Object[] d;
    private final Monoid<T> monoid;

    /**
     * 全要素を単位元で初期化する。
     * @param n 要素数
     * @param monoid 結合演算と単位元
     */
    SegTree(int n, Monoid<T> monoid) {
        if (n < 0) throw new IllegalArgumentException();
        this.n = n;
        this.monoid = monoid;
        int s = 1;
        while (s < n) s <<= 1;
        size = s;
        d = new Object[2 * size];
        java.util.Arrays.fill(d, monoid.e());
    }

    /**
     * 初期配列から構築する。
     * @param a 初期値
     * @param monoid 結合演算と単位元
     */
    SegTree(java.util.List<T> a, Monoid<T> monoid) {
        this(a.size(), monoid);
        for (int i = 0; i < n; i++) d[size + i] = a.get(i);
        for (int i = size - 1; i > 0; i--) pull(i);
    }

    /** @return 内部ノードの値（型キャスト用）。 */
    @SuppressWarnings("unchecked")
    private T val(int i) { return (T) d[i]; }

    /** 子ノードを結合して親ノードを更新する。 */
    private void pull(int i) { d[i] = monoid.op(val(i << 1), val(i << 1 | 1)); }

    /**
     * 要素を代入する。
     * @param i 更新する添字
     * @param x 新しい値
     */
    void set(int i, T x) {
        if (i < 0 || i >= n) throw new IndexOutOfBoundsException();
        i += size;
        d[i] = x;
        while ((i >>= 1) > 0) pull(i);
    }

    /**
     * 1要素の値を取得する。
     * @param i 添字
     * @return i番目の値
     */
    T get(int i) {
        if (i < 0 || i >= n) throw new IndexOutOfBoundsException();
        return val(size + i);
    }

    /**
     * [l, r) の値を左から右の順で結合する。非可換な演算にも対応。
     * @param l 左端（含む）
     * @param r 右端（含まない）
     * @return 区間の結合結果。空区間では単位元
     */
    T prod(int l, int r) {
        if (l < 0 || l > r || r > n) throw new IndexOutOfBoundsException();
        T left = monoid.e(), right = monoid.e();
        for (l += size, r += size; l < r; l >>= 1, r >>= 1) {
            if ((l & 1) != 0) left = monoid.op(left, val(l++));
            if ((r & 1) != 0) right = monoid.op(val(--r), right);
        }
        return monoid.op(left, right);
    }

    /** @return 全要素の結合結果。 */
    T allProd() { return val(1); }
}
