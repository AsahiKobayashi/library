/**
 * 要素を重複して格納できるジェネリックな多重集合。
 * HashMapを使うため追加・削除・個数取得は平均O(1)。
 * 要素の等価性はequals/hashCodeで判定する（nullも格納可能）。
 * @param <T> 要素の型
 */
class Multiset<T> implements java.lang.Iterable<T> {
    private final java.util.HashMap<T, Long> counts = new java.util.HashMap<>();
    private long size;
    private int modCount;

    /** 空の多重集合を生成する。 */
    Multiset() {}

    /**
     * 初期要素を登録する。
     * @param values 初期要素（重複を保持する）
     */
    Multiset(java.util.Collection<? extends T> values) {
        addAll(values);
    }

    /** @return 重複を含めた要素の総数。 */
    long size() { return size; }

    /** @return 異なる要素の種類数。 */
    int distinctSize() { return counts.size(); }

    /** @return 集合が空ならtrue。 */
    boolean isEmpty() { return size == 0; }

    /**
     * 要素を1個追加する。
     * @param value 追加する要素
     */
    void add(T value) { add(value, 1); }

    /**
     * 要素を指定個数追加する。
     * @param value 追加する要素
     * @param amount 追加個数（正の整数）
     */
    void add(T value, long amount) {
        if (amount <= 0) throw new IllegalArgumentException("amount must be positive");
        long nextSize = Math.addExact(size, amount);
        long nextCount = Math.addExact(count(value), amount);
        counts.put(value, nextCount);
        size = nextSize;
        modCount++;
    }

    /**
     * コレクション中の全要素を1個ずつ追加する。
     * @param values 追加する要素列
     */
    void addAll(java.util.Collection<? extends T> values) {
        for (T value : java.util.Objects.requireNonNull(values)) add(value);
    }

    /**
     * 指定要素を1個削除する。
     * @param value 削除する要素
     * @return 削除できた場合true
     */
    boolean remove(T value) { return remove(value, 1) != 0; }

    /**
     * 指定要素を最大amount個削除する。
     * @param value 削除する要素
     * @param amount 削除上限（正の整数）
     * @return 実際に削除した個数
     */
    long remove(T value, long amount) {
        if (amount <= 0) throw new IllegalArgumentException("amount must be positive");
        long old = count(value);
        if (old == 0) return 0;
        long removed = Math.min(old, amount);
        if (removed == old) counts.remove(value);
        else counts.put(value, old - removed);
        size -= removed;
        modCount++;
        return removed;
    }

    /**
     * 指定要素をすべて削除する。
     * @param value 削除する要素
     * @return 削除した個数
     */
    long removeAll(T value) {
        long old = count(value);
        if (old != 0) {
            counts.remove(value);
            size -= old;
            modCount++;
        }
        return old;
    }

    /**
     * 指定要素の出現個数を求める。
     * @param value 調べる要素
     * @return 出現個数
     */
    long count(T value) { return counts.getOrDefault(value, 0L); }

    /**
     * 指定要素が1個以上あるか確認する。
     * @param value 調べる要素
     * @return 1個以上あるならtrue
     */
    boolean contains(T value) { return counts.containsKey(value); }

    /**
     * 異なる要素の集合をコピーして返す。
     * @return 変更が元の多重集合に影響しない集合
     */
    java.util.Set<T> elementSet() {
        return new java.util.HashSet<>(counts.keySet());
    }

    /** 全要素を削除する。 */
    void clear() {
        counts.clear();
        size = 0;
        modCount++;
    }

    /**
     * 重複も含めて要素を順不同で走査する。
     * 走査中の多重集合の変更はConcurrentModificationException。
     * @return 全要素の反復子（remove非対応）
     */
    public java.util.Iterator<T> iterator() {
        return new java.util.Iterator<>() {
            private final java.util.Iterator<java.util.Map.Entry<T, Long>> entries =
                counts.entrySet().iterator();
            private final int expected = modCount;
            private T current;
            private long remaining;

            /** @return 続きの要素があればtrue。 */
            public boolean hasNext() {
                if (expected != modCount) throw new java.util.ConcurrentModificationException();
                return remaining > 0 || entries.hasNext();
            }

            /** @return 次の要素。 */
            public T next() {
                if (!hasNext()) throw new java.util.NoSuchElementException();
                if (remaining == 0) {
                    java.util.Map.Entry<T, Long> entry = entries.next();
                    current = entry.getKey();
                    remaining = entry.getValue();
                }
                remaining--;
                return current;
            }
        };
    }
}
