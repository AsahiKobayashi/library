class SegTree<T> {
    private final int n, size;
    private final Object[] d;
    private final T e;
    private final java.util.function.BinaryOperator<T> op;

    SegTree(int n, T e, java.util.function.BinaryOperator<T> op) {
        if (n < 0) throw new IllegalArgumentException();
        this.n = n;
        this.e = e;
        this.op = op;
        int s = 1;
        while (s < n) s <<= 1;
        size = s;
        d = new Object[2 * size];
        java.util.Arrays.fill(d, e);
    }

    SegTree(java.util.List<T> a, T e, java.util.function.BinaryOperator<T> op) {
        this(a.size(), e, op);
        for (int i = 0; i < n; i++) d[size + i] = a.get(i);
        for (int i = size - 1; i > 0; i--) pull(i);
    }

    @SuppressWarnings("unchecked")
    private T val(int i) { return (T) d[i]; }

    private void pull(int i) { d[i] = op.apply(val(i << 1), val(i << 1 | 1)); }

    void set(int i, T x) {
        if (i < 0 || i >= n) throw new IndexOutOfBoundsException();
        i += size;
        d[i] = x;
        while ((i >>= 1) > 0) pull(i);
    }

    T get(int i) {
        if (i < 0 || i >= n) throw new IndexOutOfBoundsException();
        return val(size + i);
    }

    // [l, r), supports non-commutative monoids
    T prod(int l, int r) {
        if (l < 0 || l > r || r > n) throw new IndexOutOfBoundsException();
        T left = e, right = e;
        for (l += size, r += size; l < r; l >>= 1, r >>= 1) {
            if ((l & 1) != 0) left = op.apply(left, val(l++));
            if ((r & 1) != 0) right = op.apply(val(--r), right);
        }
        return op.apply(left, right);
    }

    T allProd() { return val(1); }
}
