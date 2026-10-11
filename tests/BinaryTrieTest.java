/** BinaryTrieの重複、順位、XOR、削除を単純な多重集合で検証する。 */
class BinaryTrieTest {
    /** 2つの値が一致することを確認する。 */
    static void eq(Object expected, Object actual) {
        if (!java.util.Objects.equals(expected, actual))
            throw new AssertionError("expected=" + expected + ", actual=" + actual);
    }

    /** 条件に違反した場合は例外を投げる。 */
    static void require(boolean value) {
        if (!value) throw new AssertionError("condition is false");
    }

    /** 指定された型の例外が発生することを確認する。 */
    static void throwsType(Class<? extends Throwable> type, Runnable action) {
        try { action.run(); }
        catch (Throwable ex) {
            if (type.isInstance(ex)) return;
            throw new AssertionError("wrong exception: " + ex);
        }
        throw new AssertionError("exception expected: " + type);
    }

    /** 境界値と基本操作を確認する。 */
    static void basic() {
        BinaryTrie t = new BinaryTrie(3);
        eq(0L, t.size());
        require(t.isEmpty());
        throwsType(IndexOutOfBoundsException.class, () -> t.kth(0));
        throwsType(java.util.NoSuchElementException.class, () -> t.minXor(1));
        throwsType(IllegalArgumentException.class, () -> t.add(8));
        throwsType(IllegalArgumentException.class, () -> t.add(-1));
        t.add(3);
        t.add(3);
        t.add(5);
        eq(3L, t.size());
        eq(2L, t.count(3));
        // 3の重複分を含めて2個が4未満。
        eq(2L, t.countLessThan(4));
        eq(3L, t.kth(0));
        eq(3L, t.kth(1));
        eq(5L, t.kth(2));
        eq(5L, t.max());
        eq(3L, t.min());
        eq(2L, t.minXor(1));   // 3 xor 1 = 2
        eq(4L, t.maxXor(1));   // 5 xor 1 = 4
        eq(3L, t.argMinXor(1));
        eq(5L, t.argMaxXor(1));
        eq(3L, t.lowerBound(2));
        eq(5L, t.lowerBound(4));
        eq(null, t.lowerBound(6));
        eq(3L, t.countLessThan(8));
        t.xorAll(7);            // {4,4,2}
        eq(2L, t.kth(0));
        eq(4L, t.kth(1));
        eq(4L, t.kth(2));
        eq(2L, t.count(4));
        require(t.erase(4));
        eq(1L, t.count(4));
        require(t.erase(4));
        require(!t.erase(4));
        require(t.erase(2));
        require(t.isEmpty());
        t.add(0);
        eq(0L, t.min());
        t.clear();
        require(t.isEmpty());
        t.add(7);
        eq(7L, t.kth(0));

        BinaryTrie wide = new BinaryTrie(63);
        wide.add(Long.MAX_VALUE);
        wide.add(0L);
        eq(Long.MAX_VALUE, wide.max());
        eq(0L, wide.min());
        wide.xorAll(Long.MAX_VALUE);
        eq(Long.MAX_VALUE, wide.max());
        eq(0L, wide.min());
        eq(1L, wide.countLessThan(Long.MAX_VALUE));
        eq(Long.MAX_VALUE, wide.lowerBound(Long.MAX_VALUE));
        throwsType(IllegalArgumentException.class, () -> new BinaryTrie(0));
        throwsType(IllegalArgumentException.class, () -> new BinaryTrie(64));
    }

    /** ランダムな操作を多重集合の単純実装と比較する。 */
    static void randomized() {
        java.util.Random rnd = new java.util.Random(10112026);
        for (int bits : new int[]{1, 2, 5, 12, 31, 63}) {
            long mask = bits == 63 ? Long.MAX_VALUE : (1L << bits) - 1;
            BinaryTrie tree = new BinaryTrie(bits);
            java.util.ArrayList<Long> a = new java.util.ArrayList<>();
            for (int iter = 0; iter < 2500; iter++) {
                long value = rnd.nextLong() & mask;
                int mode = rnd.nextInt(8);
                if ((mode == 0 || mode == 1 || mode == 2) && a.size() < 85) {
                    tree.add(value);
                    a.add(value);
                } else if (mode == 3) {
                    if (!a.isEmpty() && rnd.nextBoolean())
                        value = a.get(rnd.nextInt(a.size()));
                    boolean expected = a.remove(Long.valueOf(value));
                    eq(expected, tree.erase(value));
                } else if (mode == 4) {
                    long x = rnd.nextLong() & mask;
                    tree.xorAll(x);
                    for (int i = 0; i < a.size(); i++) a.set(i, a.get(i) ^ x);
                } else if (mode == 5 && rnd.nextInt(80) == 0) {
                    tree.clear();
                    a.clear();
                }

                if (iter % 3 != 0) continue;
                java.util.ArrayList<Long> sorted = new java.util.ArrayList<>(a);
                java.util.Collections.sort(sorted);
                eq((long) sorted.size(), tree.size());
                eq(sorted.isEmpty(), tree.isEmpty());
                long count = 0;
                for (long x : sorted) if (x == value) count++;
                eq(count, tree.count(value));
                eq(count > 0, tree.contains(value));

                long less = 0;
                for (long x : sorted) if (x < value) less++;
                eq(less, tree.countLessThan(value));
                if (bits < 63) eq((long) sorted.size(), tree.countLessThan(mask + 1));
                Long lb = null;
                for (long x : sorted) if (x >= value) { lb = x; break; }
                eq(lb, tree.lowerBound(value));

                if (sorted.isEmpty()) {
                    throwsType(IndexOutOfBoundsException.class, () -> tree.kth(0));
                    continue;
                }

                eq(sorted.get(0), tree.min());
                eq(sorted.get(sorted.size() - 1), tree.max());
                int k = rnd.nextInt(sorted.size());
                eq(sorted.get(k), tree.kth(k));
                long minXor = Long.MAX_VALUE, maxXor = 0;
                for (long x : sorted) {
                    minXor = Math.min(minXor, x ^ value);
                    maxXor = Math.max(maxXor, x ^ value);
                }
                eq(minXor, tree.minXor(value));
                eq(maxXor, tree.maxXor(value));
                require(sorted.contains(tree.argMinXor(value)));
                require(sorted.contains(tree.argMaxXor(value)));
                eq(minXor, tree.argMinXor(value) ^ value);
                eq(maxXor, tree.argMaxXor(value) ^ value);
            }
        }
    }

    /** すべてのテストを実行する。 */
    public static void main(String[] args) {
        basic();
        randomized();
        System.out.println("BinaryTrie tests passed");
    }
}
