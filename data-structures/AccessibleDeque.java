// Source: https://github.com/AsahiKobayashi/library/blob/main/data-structures/AccessibleDeque.java
/**
 * 添字アクセス可能な可変長Deque（両端キュー）。
 * リングバッファによって先頭・末尾への追加と削除は償却O(1)、
 * get / setによる0始まりの添字アクセスはO(1)。
 * java.util.ArrayDequeと同様、null要素は格納できない。
 * @param <T> 格納する要素の型
 */
class AccessibleDeque<T> implements java.lang.Iterable<T> {
    private Object[] data;
    private int head, size, modCount;

    /** 初期容量8で空のDequeを構築する。 */
    AccessibleDeque() { this(8); }

    /**
     * 初期容量を指定して空のDequeを構築する。
     * @param capacity 初期容量（0以上）
     */
    AccessibleDeque(int capacity) {
        if (capacity < 0) throw new IllegalArgumentException("capacity must be non-negative");
        data = new Object[Math.max(1, capacity)];
    }

    /**
     * 初期要素からDequeを構築する。
     * @param values 先頭から末尾への要素列
     */
    AccessibleDeque(java.util.Collection<? extends T> values) {
        this(java.util.Objects.requireNonNull(values).size());
        for (T value : values) addLast(value);
    }

    /** @return 現在の要素数。 */
    int size() { return size; }

    /** @return 空ならtrue。 */
    boolean isEmpty() { return size == 0; }

    /**
     * 論理的な添字をリングバッファ内の添字へ変換する。
     * @param index 先頭からの相対位置
     * @return 配列内の添字
     */
    private int physical(int index) {
        return (int) (((long) head + index) % data.length);
    }

    /** バッファが満杯なら容量を拡大して順序を維持する。 */
    private void growIfNeeded() {
        if (size < data.length) return;
        int nextCapacity = data.length <= (Integer.MAX_VALUE - 8) / 2
            ? data.length * 2 : Integer.MAX_VALUE - 8;
        if (nextCapacity <= data.length) throw new OutOfMemoryError("Deque is too large");
        Object[] next = new Object[nextCapacity];
        for (int i = 0; i < size; i++) next[i] = data[physical(i)];
        data = next;
        head = 0;
    }

    /** 要素の添字を検証する。 */
    private void checkIndex(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException("index=" + index);
    }

    /**
     * 先頭に要素を追加する。
     * @param value 追加する非null要素
     */
    void addFirst(T value) {
        java.util.Objects.requireNonNull(value, "value");
        growIfNeeded();
        head = head == 0 ? data.length - 1 : head - 1;
        data[head] = value;
        size++;
        modCount++;
    }

    /**
     * 末尾に要素を追加する。
     * @param value 追加する非null要素
     */
    void addLast(T value) {
        java.util.Objects.requireNonNull(value, "value");
        growIfNeeded();
        data[physical(size)] = value;
        size++;
        modCount++;
    }

    /**
     * 先頭に要素を追加する（addFirstの別名）。
     * @param value 追加する要素
     */
    void pushFront(T value) { addFirst(value); }

    /**
     * 末尾に要素を追加する（addLastの別名）。
     * @param value 追加する要素
     */
    void pushBack(T value) { addLast(value); }

    /**
     * 先頭の要素を削除して返す。
     * @return 削除した要素
     * @throws java.util.NoSuchElementException 空の場合
     */
    @SuppressWarnings("unchecked")
    T removeFirst() {
        if (size == 0) throw new java.util.NoSuchElementException();
        T value = (T) data[head];
        data[head] = null;
        head = head + 1 == data.length ? 0 : head + 1;
        size--;
        if (size == 0) head = 0;
        modCount++;
        return value;
    }

    /**
     * 末尾の要素を削除して返す。
     * @return 削除した要素
     * @throws java.util.NoSuchElementException 空の場合
     */
    @SuppressWarnings("unchecked")
    T removeLast() {
        if (size == 0) throw new java.util.NoSuchElementException();
        int index = physical(size - 1);
        T value = (T) data[index];
        data[index] = null;
        size--;
        if (size == 0) head = 0;
        modCount++;
        return value;
    }

    /** @return 先頭から取り出した要素（removeFirstの別名）。 */
    T popFront() { return removeFirst(); }

    /** @return 末尾から取り出した要素（removeLastの別名）。 */
    T popBack() { return removeLast(); }

    /**
     * 先頭を削除せず取得する。
     * @return 先頭の値。空ならnull
     */
    @SuppressWarnings("unchecked")
    T peekFirst() { return size == 0 ? null : (T) data[head]; }

    /**
     * 末尾を削除せず取得する。
     * @return 末尾の値。空ならnull
     */
    @SuppressWarnings("unchecked")
    T peekLast() { return size == 0 ? null : (T) data[physical(size - 1)]; }

    /**
     * 指定した位置の要素を取得する。
     * @param index 先頭を0とした添字
     * @return 要素
     */
    @SuppressWarnings("unchecked")
    T get(int index) {
        checkIndex(index);
        return (T) data[physical(index)];
    }

    /**
     * 指定した位置の要素を置き換える。
     * @param index 先頭を0とした添字
     * @param value 新しい非null要素
     * @return 置き換える前の要素
     */
    T set(int index, T value) {
        checkIndex(index);
        java.util.Objects.requireNonNull(value, "value");
        int physicalIndex = physical(index);
        @SuppressWarnings("unchecked")
        T previous = (T) data[physicalIndex];
        data[physicalIndex] = value;
        return previous;
    }

    /** 格納されている要素をすべて削除する。 */
    void clear() {
        for (int i = 0; i < size; i++) data[physical(i)] = null;
        head = 0;
        size = 0;
        modCount++;
    }

    /**
     * 先頭から末尾へ走査する反復子を返す。
     * 反復中に要素を追加・削除した場合はConcurrentModificationExceptionを投げる。
     * @return 順方向の反復子
     */
    public java.util.Iterator<T> iterator() {
        return new java.util.Iterator<>() {
            private int index;
            private final int expectedModCount = modCount;

            /** @return 次の要素があればtrue。 */
            public boolean hasNext() {
                if (expectedModCount != modCount) throw new java.util.ConcurrentModificationException();
                return index < size;
            }

            /** @return 次の要素。 */
            public T next() {
                if (!hasNext()) throw new java.util.NoSuchElementException();
                return get(index++);
            }
        };
    }
}
