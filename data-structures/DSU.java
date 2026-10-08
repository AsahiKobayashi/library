/** 経路圧縮とサイズ併合を行う Union-Find。 */
class DSU {
    private final int[] parent, size;
    private int groups;

    /** @param n 頂点数。最初は全頂点が別の連結成分に属する。 */
    DSU(int n) {
        parent = new int[n];
        size = new int[n];
        groups = n;
        for (int i = 0; i < n; i++) { parent[i] = i; size[i] = 1; }
    }

    /** @param a 頂点 @return aの属する連結成分の代表元。 */
    int leader(int a) {
        if (parent[a] == a) return a;
        return parent[a] = leader(parent[a]);
    }

    /**
     * 2頂点の連結成分を統合する。
     * @return 新しく統合されたときtrue、すでに同じ成分ならfalse
     */
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

    /** @return aとbが同じ連結成分ならtrue。 */
    boolean same(int a, int b) { return leader(a) == leader(b); }

    /** @return aの属する連結成分の頂点数。 */
    int size(int a) { return size[leader(a)]; }

    /** @return 現在の連結成分の数。 */
    int groups() { return groups; }
}
