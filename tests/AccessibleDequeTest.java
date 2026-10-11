/** AccessibleDequeの両端操作、添字参照、周回と拡張を検証する。 */
class AccessibleDequeTest {
    /** 期待値と実際の値が等しいことを確認する。 */
    static void eq(Object expected, Object actual) {
        if (!java.util.Objects.equals(expected, actual))
            throw new AssertionError("expected=" + expected + ", actual=" + actual);
    }

    /** 指定の例外が発生することを確認する。 */
    static void throwsType(Class<? extends Throwable> type, Runnable action) {
        try { action.run(); }
        catch (Throwable ex) {
            if (type.isInstance(ex)) return;
            throw new AssertionError("unexpected exception: " + ex);
        }
        throw new AssertionError("exception expected: " + type);
    }

    /** 順序、先頭と末尾、null禁止、反復子を検証する。 */
    static void basic() {
        AccessibleDeque<String> d = new AccessibleDeque<>(0);
        eq(0, d.size());
        eq(null, d.peekFirst());
        eq(null, d.peekLast());
        throwsType(java.util.NoSuchElementException.class, d::popFront);
        throwsType(java.util.NoSuchElementException.class, d::popBack);
        d.pushBack("b");
        d.pushFront("a");
        d.addLast("c");
        eq(3, d.size());
        eq("a", d.peekFirst());
        eq("c", d.peekLast());
        eq("b", d.get(1));
        eq("b", d.set(1, "B"));
        eq("B", d.get(1));
        eq("a", d.removeFirst());
        eq("c", d.removeLast());
        eq("B", d.popFront());
        eq(true, d.isEmpty());
        throwsType(IndexOutOfBoundsException.class, () -> d.get(0));
        throwsType(IndexOutOfBoundsException.class, () -> d.set(-1, "x"));
        throwsType(NullPointerException.class, () -> d.addFirst(null));
        throwsType(NullPointerException.class, () -> d.addLast(null));
        throwsType(IllegalArgumentException.class, () -> new AccessibleDeque<String>(-1));

        AccessibleDeque<String> other = new AccessibleDeque<>(java.util.Arrays.asList("a", "b", "c"));
        StringBuilder sb = new StringBuilder();
        for (String x : other) sb.append(x);
        eq("abc", sb.toString());
        java.util.Iterator<String> iter = other.iterator();
        eq("a", iter.next());
        other.addLast("d");
        throwsType(java.util.ConcurrentModificationException.class, iter::hasNext);
        other.clear();
        eq(0, other.size());
        eq(null, other.peekLast());
    }

    /** ランダム操作をArrayListによる単純実装と比較する。 */
    static void randomized() {
        java.util.Random rnd = new java.util.Random(112233);
        for (int initialCapacity : new int[]{0, 1, 2, 3, 8, 16}) {
            AccessibleDeque<Integer> d = new AccessibleDeque<>(initialCapacity);
            java.util.ArrayList<Integer> a = new java.util.ArrayList<>();
            for (int t = 0; t < 18000; t++) {
                int mode = rnd.nextInt(11);
                int value = rnd.nextInt();
                if (mode == 0) {
                    d.addFirst(value);
                    a.add(0, value);
                } else if (mode == 1) {
                    d.addLast(value);
                    a.add(value);
                } else if (mode == 2) {
                    d.pushFront(value);
                    a.add(0, value);
                } else if (mode == 3) {
                    d.pushBack(value);
                    a.add(value);
                } else if (mode == 4) {
                    if (a.isEmpty()) throwsType(java.util.NoSuchElementException.class, d::removeFirst);
                    else eq(a.remove(0), d.popFront());
                } else if (mode == 5) {
                    if (a.isEmpty()) throwsType(java.util.NoSuchElementException.class, d::removeLast);
                    else eq(a.remove(a.size() - 1), d.popBack());
                } else if (mode == 6) {
                    if (!a.isEmpty()) {
                        int i = rnd.nextInt(a.size());
                        eq(a.get(i), d.get(i));
                    }
                } else if (mode == 7) {
                    if (!a.isEmpty()) {
                        int i = rnd.nextInt(a.size());
                        eq(a.set(i, value), d.set(i, value));
                    }
                } else if (mode == 8 && rnd.nextInt(100) == 0) {
                    d.clear();
                    a.clear();
                } else if (mode == 9) {
                    eq(a.isEmpty() ? null : a.get(0), d.peekFirst());
                    eq(a.isEmpty() ? null : a.get(a.size() - 1), d.peekLast());
                }
                eq(a.size(), d.size());
                eq(a.isEmpty(), d.isEmpty());
                if (t % 73 == 0) {
                    int index = 0;
                    for (Integer x : d) eq(a.get(index++), x);
                    eq(a.size(), index);
                    for (int i = 0; i < a.size(); i++) eq(a.get(i), d.get(i));
                }
            }
        }
    }

    /** 全テストを実行する。 */
    public static void main(String[] args) {
        basic();
        randomized();
        System.out.println("AccessibleDeque tests passed");
    }
}
