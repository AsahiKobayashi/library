class FenwickTree<T> {
    private final int n;
    private final Object[] bit;
    private final T e;
    private final java.util.function.BinaryOperator<T> op;
    private final java.util.function.UnaryOperator<T> inverse;

    // Requires a commutative group: associative op, identity e, inverse.
    FenwickTree(int n, T e, java.util.function.BinaryOperator<T> op,
                java.util.function.UnaryOperator<T> inverse) {
        if (n < 0) throw new IllegalArgumentException();
        this.n = n;
        this.e = e;
        this.op = op;
        this.inverse = inverse;
        bit = new Object[n + 1];
        java.util.Arrays.fill(bit, e);
    }

    @SuppressWarnings("unchecked")
    private T val(int i) { return (T) bit[i]; }

    // a[i] = op(a[i], x)
    void add(int i, T x) {
        if (i < 0 || i >= n) throw new IndexOutOfBoundsException();
        for (i++; i <= n; i += i & -i) bit[i] = op.apply(val(i), x);
    }

    // [0, r)
    T sum(int r) {
        if (r < 0 || r > n) throw new IndexOutOfBoundsException();
        T s = e;
        for (; r > 0; r -= r & -r) s = op.apply(s, val(r));
        return s;
    }

    // [l, r)
    T sum(int l, int r) {
        if (l < 0 || l > r || r > n) throw new IndexOutOfBoundsException();
        return op.apply(sum(r), inverse.apply(sum(l)));
    }
}
