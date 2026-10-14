// Source: https://github.com/AsahiKobayashi/library/blob/main/tests/MultisetTest.java
/** MultisetとSortedMultisetを標準コレクションの単純実装と比較する。 */
class MultisetTest {
    /**
     * 値が一致していることを確認する。
     * @param expected 期待値
     * @param actual 実際の値
     */
    static void eq(Object expected, Object actual) {
        if (!java.util.Objects.equals(expected, actual))
            throw new AssertionError("expected=" + expected + ", actual=" + actual);
    }

    /** 指定種類の例外が発生することを確認する。 */
    static void throwsType(Class<? extends Throwable> type, Runnable action) {
        try { action.run(); }
        catch (Throwable ex) {
            if (type.isInstance(ex)) return;
            throw new AssertionError("unexpected exception: " + ex);
        }
        throw new AssertionError("exception expected: " + type);
    }

    /** 異なる要素の出現回数を比較する。 */
    static <T> java.util.Map<T, Long> frequencies(java.lang.Iterable<T> values) {
        java.util.HashMap<T, Long> answer = new java.util.HashMap<>();
        for (T x : values) answer.merge(x, 1L, Long::sum);
        return answer;
    }

    /** HashMapによる多重集合の境界条件を検証する。 */
    static void multisetBasic() {
        Multiset<String> set = new Multiset<>(java.util.Arrays.asList("a", "b", "a", null));
        eq(4L, set.size());
        eq(3, set.distinctSize());
        eq(2L, set.count("a"));
        eq(1L, set.count(null));
        eq(true, set.contains(null));
        eq(false, set.contains("missing"));
        eq(1L, set.remove("a", 1));
        eq(true, set.remove("a"));
        eq(false, set.remove("a"));
        eq(0L, set.count("a"));
        eq(1L, set.removeAll(null));
        set.add("big", 3_000_000_000L);
        eq(3_000_000_001L, set.size());
        eq(3_000_000_000L, set.count("big"));
        eq(2_999_999_999L, set.remove("big", 2_999_999_999L));
        eq(1L, set.count("big"));
        eq(1L, set.removeAll("big"));
        throwsType(IllegalArgumentException.class, () -> set.add("x", 0));
        throwsType(IllegalArgumentException.class, () -> set.remove("x", 0));
        eq(new java.util.HashSet<>(java.util.Arrays.asList("b")), set.elementSet());
        java.util.Iterator<String> it = set.iterator();
        eq("b", it.next());
        set.add("c");
        throwsType(java.util.ConcurrentModificationException.class, it::hasNext);
        set.clear();
        eq(0L, set.size());
        eq(true, set.isEmpty());
        throwsType(java.util.NoSuchElementException.class, () -> set.iterator().next());
    }

    /** HashMapの単純実装とランダム操作を比較する。 */
    static void multisetRandom() {
        java.util.Random rnd = new java.util.Random(410);
        Multiset<Integer> set = new Multiset<>();
        java.util.HashMap<Integer, Long> ref = new java.util.HashMap<>();
        long total = 0;
        for (int t = 0; t < 12000; t++) {
            int x = rnd.nextInt(41) - 20;
            int mode = rnd.nextInt(7);
            long amount = rnd.nextInt(5) + 1L;
            if (mode <= 2) {
                set.add(x, amount);
                ref.merge(x, amount, Long::sum);
                total += amount;
            } else if (mode == 3 || mode == 4) {
                long old = ref.getOrDefault(x, 0L), removed = Math.min(old, amount);
                eq(removed, set.remove(x, amount));
                if (old == removed) ref.remove(x);
                else ref.put(x, old - removed);
                total -= removed;
            } else if (mode == 5) {
                long old = ref.getOrDefault(x, 0L);
                eq(old, set.removeAll(x));
                ref.remove(x);
                total -= old;
            } else if (rnd.nextInt(75) == 0) {
                set.clear();
                ref.clear();
                total = 0;
            }
            eq(total, set.size());
            eq(ref.size(), set.distinctSize());
            eq(ref.getOrDefault(x, 0L), set.count(x));
            eq(ref.containsKey(x), set.contains(x));
            if (t % 73 == 0) {
                eq(ref.keySet(), set.elementSet());
                eq(ref, frequencies(set));
            }
        }
    }

    /** 整列多重集合の特殊なケースを検証する。 */
    static void sortedBasic() {
        SortedMultiset<Integer> set = new SortedMultiset<>();
        eq(true, set.isEmpty());
        eq(0L, set.size());
        throwsType(java.util.NoSuchElementException.class, set::first);
        throwsType(java.util.NoSuchElementException.class, set::last);
        throwsType(IndexOutOfBoundsException.class, () -> set.kth(0));
        throwsType(IllegalArgumentException.class, () -> set.add(1, 0));
        throwsType(NullPointerException.class, () -> set.add(null));
        set.add(3);
        set.add(1, 3);
        set.add(5);
        set.add(3);
        eq(6L, set.size());
        eq(3, set.distinctSize());
        eq(3L, set.count(1));
        eq(2L, set.count(3));
        eq(1, set.first());
        eq(5, set.last());
        eq(1, set.kth(2));
        eq(3, set.kth(3));
        eq(5, set.kth(5));
        eq(3L, set.countLessThan(3));
        eq(5L, set.countLessThanOrEqual(3));
        eq(null, set.lower(1));
        eq(1, set.floor(1));
        eq(3, set.ceiling(2));
        eq(5, set.higher(3));
        eq(null, set.higher(5));
        eq(3, set.floor(4));
        eq(3, set.lower(4));
        eq(2L, set.remove(1, 2));
        eq(1L, set.count(1));
        eq(2L, set.removeAll(3));
        eq(1, set.pollFirst());
        eq(5, set.pollLast());
        eq(true, set.isEmpty());
        eq(0, set.distinctSize());
        set.add(7, 4_000_000_000L);
        eq(4_000_000_000L, set.size());
        eq(7, set.kth(3_999_999_999L));
        eq(4_000_000_000L, set.countLessThanOrEqual(7));
        eq(4_000_000_000L, set.removeAll(7));
        throwsType(IndexOutOfBoundsException.class, () -> set.kth(-1));

        SortedMultiset<String> byLength =
            new SortedMultiset<>(java.util.Comparator.comparingInt(String::length));
        byLength.add("aa");
        byLength.add("bb");
        byLength.add("z");
        eq(2L, byLength.count("xx"));
        eq(2, byLength.distinctSize());
        eq("aa", byLength.kth(1));
        eq("z", byLength.first());

        SortedMultiset<Integer> reversed =
            new SortedMultiset<>(java.util.Arrays.asList(2, 1, 3, 2), java.util.Comparator.reverseOrder());
        eq(java.util.Arrays.asList(3, 2, 2, 1), toList(reversed));
        eq(1L, reversed.countLessThan(2));
        eq(3, reversed.first());
        eq(1, reversed.last());

        SortedMultiset<Integer> small = new SortedMultiset<>(java.util.Arrays.asList(1, 2));
        java.util.Iterator<Integer> it = small.iterator();
        eq(1, it.next());
        small.add(3);
        throwsType(java.util.ConcurrentModificationException.class, it::hasNext);
    }

    /**
     * Iterableの内容をリストへ格納する。
     * @param values 変換する要素列
     * @return 要素を順番に格納したリスト
     */
    static <T> java.util.List<T> toList(java.lang.Iterable<T> values) {
        java.util.ArrayList<T> answer = new java.util.ArrayList<>();
        for (T x : values) answer.add(x);
        return answer;
    }

    /** AVL木の全操作をTreeMapの単純実装とランダム比較する。 */
    static void sortedRandom() {
        java.util.Random rnd = new java.util.Random(20261014);
        for (boolean reverse : new boolean[]{false, true}) {
            java.util.Comparator<Integer> comparator =
                reverse ? java.util.Comparator.reverseOrder() : java.util.Comparator.naturalOrder();
            SortedMultiset<Integer> set = new SortedMultiset<>(comparator);
            java.util.TreeMap<Integer, Long> ref = new java.util.TreeMap<>(comparator);
            long size = 0;
            for (int step = 0; step < 16000; step++) {
                int x = rnd.nextInt(101) - 50;
                int op = rnd.nextInt(9);
                long amount = rnd.nextInt(4) + 1L;
                if (op <= 2) {
                    set.add(x, amount);
                    ref.merge(x, amount, Long::sum);
                    size += amount;
                } else if (op == 3 || op == 4) {
                    long old = ref.getOrDefault(x, 0L), removed = Math.min(old, amount);
                    eq(removed, set.remove(x, amount));
                    if (old == removed) ref.remove(x);
                    else ref.put(x, old - removed);
                    size -= removed;
                } else if (op == 5) {
                    long old = ref.getOrDefault(x, 0L);
                    eq(old, set.removeAll(x));
                    ref.remove(x);
                    size -= old;
                } else if (op == 6 && !ref.isEmpty()) {
                    Integer expected = ref.firstKey();
                    eq(expected, set.pollFirst());
                    long old = ref.get(expected);
                    if (old == 1) ref.remove(expected);
                    else ref.put(expected, old - 1);
                    size--;
                } else if (op == 7 && !ref.isEmpty()) {
                    Integer expected = ref.lastKey();
                    eq(expected, set.pollLast());
                    long old = ref.get(expected);
                    if (old == 1) ref.remove(expected);
                    else ref.put(expected, old - 1);
                    size--;
                } else if (op == 8 && rnd.nextInt(120) == 0) {
                    set.clear();
                    ref.clear();
                    size = 0;
                }
                eq(size, set.size());
                eq(ref.size(), set.distinctSize());
                eq(ref.getOrDefault(x, 0L), set.count(x));
                eq(ref.containsKey(x), set.contains(x));
                eq(ref.lowerKey(x), set.lower(x));
                eq(ref.floorKey(x), set.floor(x));
                eq(ref.ceilingKey(x), set.ceiling(x));
                eq(ref.higherKey(x), set.higher(x));

                long less = 0, lessEqual = 0;
                for (var e : ref.entrySet()) {
                    if (comparator.compare(e.getKey(), x) < 0) less += e.getValue();
                    if (comparator.compare(e.getKey(), x) <= 0) lessEqual += e.getValue();
                }
                eq(less, set.countLessThan(x));
                eq(lessEqual, set.countLessThanOrEqual(x));

                if (step % 59 == 0 || (size > 0 && step % 13 == 0)) {
                    if (ref.isEmpty()) {
                        eq(true, set.isEmpty());
                        throwsType(java.util.NoSuchElementException.class, set::first);
                    } else {
                        eq(ref.firstKey(), set.first());
                        eq(ref.lastKey(), set.last());
                        long k = (rnd.nextLong() & Long.MAX_VALUE) % size;
                        long skip = k;
                        Integer expected = null;
                        for (var e : ref.entrySet()) {
                            if (skip < e.getValue()) { expected = e.getKey(); break; }
                            skip -= e.getValue();
                        }
                        eq(expected, set.kth(k));
                    }
                }
                if (step % 83 == 0) {
                    java.util.ArrayList<Integer> expected = new java.util.ArrayList<>();
                    for (var e : ref.entrySet()) for (long i = 0; i < e.getValue(); i++) expected.add(e.getKey());
                    eq(expected, toList(set));
                }
            }
        }
    }

    /** 全テストを実行する。 */
    public static void main(String[] args) {
        multisetBasic();
        multisetRandom();
        sortedBasic();
        sortedRandom();
        System.out.println("Multiset and SortedMultiset tests passed");
    }
}
