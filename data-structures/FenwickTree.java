/**
 * 可換群に対応した Fenwick Tree。点更新と区間集約を行う。
 * 添字は0始まり、区間は半開区間 [l, r)。
 * @param <T> 要素の型
 */
class FenwickTree<T> {
    private final int n;
    private final Object[] bit;
    private final CommutativeGroup<T> group;

    /**
     * 全要素を単位元で初期化する。
     * @param n 要素数
     * @param group 可換な結合演算、単位元、逆元
     */
    FenwickTree(int n, CommutativeGroup<T> group) {
        if (n < 0) throw new IllegalArgumentException();
        this.n = n;
        this.group = group;
        bit = new Object[n + 1];
        java.util.Arrays.fill(bit, group.identity());
    }

    /** @return 内部ノードの値（型キャスト用）。 */
    @SuppressWarnings("unchecked")
    private T val(int i) { return (T) bit[i]; }

    /**
     * i番目の値にxを結合する。
     * @param i 更新する添字
     * @param x 結合する値
     */
    void add(int i, T x) {
        if (i < 0 || i >= n) throw new IndexOutOfBoundsException();
        for (i++; i <= n; i += i & -i) bit[i] = group.combine(val(i), x);
    }

    /**
     * [0, r) の結合結果を求める。
     * @param r 右端（含まない）
     * @return prefixの結合結果
     */
    T sum(int r) {
        if (r < 0 || r > n) throw new IndexOutOfBoundsException();
        T s = group.identity();
        for (; r > 0; r -= r & -r) s = group.combine(s, val(r));
        return s;
    }

    /**
     * [l, r) の結合結果を求める。
     * @param l 左端（含む）
     * @param r 右端（含まない）
     * @return 区間の結合結果
     */
    T sum(int l, int r) {
        if (l < 0 || l > r || r > n) throw new IndexOutOfBoundsException();
        return group.combine(sum(r), group.inverse(sum(l)));
    }
}
