class DSU {
    private final int[] parent, size;
    private int groups;

    DSU(int n) {
        parent = new int[n];
        size = new int[n];
        groups = n;
        for (int i = 0; i < n; i++) { parent[i] = i; size[i] = 1; }
    }

    int leader(int a) {
        if (parent[a] == a) return a;
        return parent[a] = leader(parent[a]);
    }

    boolean merge(int a, int b) {
        a = leader(a);
        b = leader(b);
        if (a == b) return false;
        if (size[a] < size[b]) { int t = a; a = b; b = t; }
        parent[b] = a;
        size[a] += size[b];
        groups--;
        return true;
    }

    boolean same(int a, int b) { return leader(a) == leader(b); }
    int size(int a) { return size[leader(a)]; }
    int groups() { return groups; }
}
