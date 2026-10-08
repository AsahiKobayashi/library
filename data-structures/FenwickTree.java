class FenwickTree {
    private final int n;
    private final long[] bit;

    FenwickTree(int n) {
        this.n = n;
        bit = new long[n + 1];
    }

    // a[i] += x
    void add(int i, long x) {
        for (i++; i <= n; i += i & -i) bit[i] += x;
    }

    // [0, r)
    long sum(int r) {
        long s = 0;
        for (; r > 0; r -= r & -r) s += bit[r];
        return s;
    }

    // [l, r)
    long sum(int l, int r) { return sum(r) - sum(l); }
}
