class SegTree {
    private final int n, size;
    private final long[] d;

    SegTree(int n) {
        this.n = n;
        int s = 1;
        while (s < n) s <<= 1;
        size = s;
        d = new long[2 * size];
    }

    SegTree(long[] a) {
        this(a.length);
        for (int i = 0; i < n; i++) d[size + i] = a[i];
        for (int i = size - 1; i > 0; i--) d[i] = d[i << 1] + d[i << 1 | 1];
    }

    void set(int i, long x) {
        i += size;
        d[i] = x;
        while ((i >>= 1) > 0) d[i] = d[i << 1] + d[i << 1 | 1];
    }

    long get(int i) { return d[size + i]; }

    // [l, r)
    long prod(int l, int r) {
        long left = 0, right = 0;
        for (l += size, r += size; l < r; l >>= 1, r >>= 1) {
            if ((l & 1) != 0) left += d[l++];
            if ((r & 1) != 0) right += d[--r];
        }
        return left + right;
    }

    long allProd() { return d[1]; }
}
