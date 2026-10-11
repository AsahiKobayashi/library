/**
 * long 型の加算と区間和を扱う Fenwick Tree（Binary Indexed Tree）。
 * 添字は0始まり、区間は半開区間 [l, r)。
 */
class FenwickTree {
    private final int n;
    private final long[] bit;

    /**
     * 要素数 n の配列を、すべて0で初期化する。
     * @param n 要素数（0以上）
     */
    FenwickTree(int n) {
        if (n < 0) throw new IllegalArgumentException("n must be non-negative");
        this.n = n;
        bit = new long[n + 1];
    }

    /**
     * i番目の値に x を加算する。
     * @param i 更新する添字（0以上n未満）
     * @param x 加算する値（負数も可）
     */
    void add(int i, long x) {
        if (i < 0 || i >= n) throw new IndexOutOfBoundsException();
        for (i++; i <= n; i += i & -i) bit[i] += x;
    }

    /**
     * 区間 [0, r) の和を求める。
     * @param r 右端（含まない）
     * @return 累積和
     */
    long sum(int r) {
        if (r < 0 || r > n) throw new IndexOutOfBoundsException();
        long s = 0;
        for (; r > 0; r -= r & -r) s += bit[r];
        return s;
    }

    /**
     * 区間 [l, r) の和を求める。
     * @param l 左端（含む）
     * @param r 右端（含まない）
     * @return 区間和。空区間なら0
     */
    long sum(int l, int r) {
        if (l < 0 || l > r || r > n) throw new IndexOutOfBoundsException();
        return sum(r) - sum(l);
    }
}
