/**
 * AVL木によるジェネリックな整列多重集合（重複を許す）。
 * 追加・削除・k番目・順位・前後の値の検索は最悪O(log U)。
 * Uは異なる値の個数。反復は昇順、重複も含む。
 * 比較器が返す0を同一要素とみなす。nullは格納不可。
 * @param <T> 格納する値の型
 */
class SortedMultiset<T> implements java.lang.Iterable<T> {
    /** 要素の出現回数・部分木サイズ・AVLの高さを持つノード。 */
    private static final class Node<E> {
        E key;
        long count, size;
        int distinct, height;
        Node<E> left, right;

        /** 要素とその出現回数で葉を作る。 */
        Node(E key, long count) {
            this.key = key;
            this.count = this.size = count;
            distinct = height = 1;
        }
    }

    private final java.util.Comparator<? super T> comparator;
    private Node<T> root;
    private int modCount;

    /** 要素の自然順序で空の多重集合を生成する。 */
    SortedMultiset() { this((java.util.Comparator<? super T>) null); }

    /**
     * 比較器を指定して空の多重集合を生成する。
     * @param comparator 要素の順序（nullなら自然順序）
     */
    SortedMultiset(java.util.Comparator<? super T> comparator) {
        this.comparator = comparator;
    }

    /**
     * 初期要素を自然順序で格納する。
     * @param values 初期要素列
     */
    SortedMultiset(java.util.Collection<? extends T> values) {
        this();
        addAll(values);
    }

    /**
     * 比較器と初期要素列を指定する。
     * @param values 初期要素列
     * @param comparator 並び順（nullなら自然順序）
     */
    SortedMultiset(java.util.Collection<? extends T> values,
                   java.util.Comparator<? super T> comparator) {
        this(comparator);
        addAll(values);
    }

    /** @return 使用している比較器（自然順序ならnull）。 */
    java.util.Comparator<? super T> comparator() { return comparator; }

    /** 要素を比較器または自然順序で比較する。 */
    @SuppressWarnings("unchecked")
    private int compare(T a, T b) {
        if (comparator != null) return comparator.compare(a, b);
        return ((java.lang.Comparable<? super T>) a).compareTo(b);
    }

    /** @return ノードがnullなら0、そうでなければ高さ。 */
    private static <E> int height(Node<E> x) { return x == null ? 0 : x.height; }

    /** @return ノードがnullなら0、そうでなければ部分木の総要素数。 */
    private static <E> long total(Node<E> x) { return x == null ? 0 : x.size; }

    /** @return ノードがnullなら0、そうでなければ異なる値の個数。 */
    private static <E> int unique(Node<E> x) { return x == null ? 0 : x.distinct; }

    /** 左右の子から部分木の情報を再計算する。 */
    private void pull(Node<T> x) {
        x.height = Math.max(height(x.left), height(x.right)) + 1;
        x.size = x.count + total(x.left) + total(x.right);
        x.distinct = 1 + unique(x.left) + unique(x.right);
    }

    /** AVL木を左回転する。 */
    private Node<T> rotateLeft(Node<T> x) {
        Node<T> y = x.right;
        x.right = y.left;
        y.left = x;
        pull(x);
        pull(y);
        return y;
    }

    /** AVL木を右回転する。 */
    private Node<T> rotateRight(Node<T> x) {
        Node<T> y = x.left;
        x.left = y.right;
        y.right = x;
        pull(x);
        pull(y);
        return y;
    }

    /** 部分木の情報を更新し、AVL平衡を回復する。 */
    private Node<T> balance(Node<T> x) {
        pull(x);
        int diff = height(x.left) - height(x.right);
        if (diff > 1) {
            if (height(x.left.left) < height(x.left.right)) x.left = rotateLeft(x.left);
            return rotateRight(x);
        }
        if (diff < -1) {
            if (height(x.right.right) < height(x.right.left)) x.right = rotateRight(x.right);
            return rotateLeft(x);
        }
        return x;
    }

    /** 子へ再帰して要素の出現回数を増やす。 */
    private Node<T> insert(Node<T> x, T value, long amount) {
        if (x == null) return new Node<>(value, amount);
        int c = compare(value, x.key);
        if (c < 0) x.left = insert(x.left, value, amount);
        else if (c > 0) x.right = insert(x.right, value, amount);
        else x.count += amount;
        return balance(x);
    }

    /** 部分木の最小ノードを取得する。 */
    private Node<T> minNode(Node<T> x) {
        while (x.left != null) x = x.left;
        return x;
    }

    /** 部分木の最大ノードを取得する。 */
    private Node<T> maxNode(Node<T> x) {
        while (x.right != null) x = x.right;
        return x;
    }

    /** 部分木から最小ノード全体を除去する。 */
    private Node<T> eraseMin(Node<T> x) {
        if (x.left == null) return x.right;
        x.left = eraseMin(x.left);
        return balance(x);
    }

    /** 子へ再帰して指定個数を削除する。 */
    private Node<T> erase(Node<T> x, T value, long amount) {
        int c = compare(value, x.key);
        if (c < 0) x.left = erase(x.left, value, amount);
        else if (c > 0) x.right = erase(x.right, value, amount);
        else if (amount < x.count) x.count -= amount;
        else {
            if (x.left == null) return x.right;
            if (x.right == null) return x.left;
            Node<T> next = minNode(x.right);
            x.key = next.key;
            x.count = next.count;
            x.right = eraseMin(x.right);
        }
        return balance(x);
    }

    /** @return 重複を含めた全要素数。 */
    long size() { return total(root); }

    /** @return 異なる要素の種類数。 */
    int distinctSize() { return unique(root); }

    /** @return 空ならtrue。 */
    boolean isEmpty() { return root == null; }

    /**
     * 値を1個挿入する。
     * @param value 追加する要素（null不可）
     */
    void add(T value) { add(value, 1); }

    /**
     * 値を指定個数だけ挿入する。
     * @param value 追加する要素（null不可）
     * @param amount 追加する個数（正の整数）
     */
    void add(T value, long amount) {
        java.util.Objects.requireNonNull(value, "value");
        if (amount <= 0) throw new IllegalArgumentException("amount must be positive");
        Math.addExact(size(), amount);
        // 空木の自然順序でもComparableであることを検証する。
        if (root == null) compare(value, value);
        root = insert(root, value, amount);
        modCount++;
    }

    /**
     * コレクション中の全要素を1個ずつ挿入する。
     * @param values 追加する要素列
     */
    void addAll(java.util.Collection<? extends T> values) {
        for (T value : java.util.Objects.requireNonNull(values)) add(value);
    }

    /**
     * 指定値の出現回数を取得する。
     * @param value 調べる要素
     * @return 出現回数
     */
    long count(T value) {
        java.util.Objects.requireNonNull(value, "value");
        Node<T> x = root;
        while (x != null) {
            int c = compare(value, x.key);
            if (c == 0) return x.count;
            x = c < 0 ? x.left : x.right;
        }
        return 0;
    }

    /**
     * 指定値が存在するか判定する。
     * @param value 調べる要素
     * @return 存在するならtrue
     */
    boolean contains(T value) { return count(value) != 0; }

    /**
     * 指定値を1個削除する。
     * @param value 削除する要素
     * @return 削除できたならtrue
     */
    boolean remove(T value) { return remove(value, 1) != 0; }

    /**
     * 指定値を最大amount個削除する。
     * @param value 削除する要素
     * @param amount 削除上限（正の整数）
     * @return 削除した要素数
     */
    long remove(T value, long amount) {
        java.util.Objects.requireNonNull(value, "value");
        if (amount <= 0) throw new IllegalArgumentException("amount must be positive");
        long removed = Math.min(count(value), amount);
        if (removed == 0) return 0;
        root = erase(root, value, removed);
        modCount++;
        return removed;
    }

    /**
     * 指定値をすべて削除する。
     * @param value 削除する要素
     * @return 削除した個数
     */
    long removeAll(T value) {
        java.util.Objects.requireNonNull(value, "value");
        long removed = count(value);
        if (removed != 0) {
            root = erase(root, value, removed);
            modCount++;
        }
        return removed;
    }

    /** 全要素を削除する。 */
    void clear() {
        root = null;
        modCount++;
    }

    /**
     * 昇順でk番目の値を取得する（重複を含む）。
     * @param k 0始まりの順位
     * @return k番目の値
     * @throws IndexOutOfBoundsException kが範囲外の場合
     */
    T kth(long k) {
        if (k < 0 || k >= size()) throw new IndexOutOfBoundsException();
        Node<T> x = root;
        while (x != null) {
            long left = total(x.left);
            if (k < left) x = x.left;
            else if (k < left + x.count) return x.key;
            else { k -= left + x.count; x = x.right; }
        }
        throw new AssertionError("AVL subtree counts are inconsistent");
    }

    /**
     * 指定値より小さい要素の個数（重複を含む）。
     * @param value 閾値
     * @return value未満の要素数
     */
    long countLessThan(T value) {
        java.util.Objects.requireNonNull(value, "value");
        long answer = 0;
        Node<T> x = root;
        while (x != null) {
            int c = compare(value, x.key);
            if (c <= 0) x = x.left;
            else {
                answer += total(x.left) + x.count;
                x = x.right;
            }
        }
        return answer;
    }

    /**
     * 指定値以下の要素の個数（重複を含む）。
     * @param value 閾値
     * @return value以下の要素数
     */
    long countLessThanOrEqual(T value) {
        java.util.Objects.requireNonNull(value, "value");
        long answer = 0;
        Node<T> x = root;
        while (x != null) {
            int c = compare(value, x.key);
            if (c < 0) x = x.left;
            else {
                answer += total(x.left);
                if (c == 0) return answer + x.count;
                answer += x.count;
                x = x.right;
            }
        }
        return answer;
    }

    /**
     * 最小の要素を取得する。
     * @return 最小値
     * @throws java.util.NoSuchElementException 空の場合
     */
    T first() {
        if (root == null) throw new java.util.NoSuchElementException();
        return minNode(root).key;
    }

    /**
     * 最大の要素を取得する。
     * @return 最大値
     * @throws java.util.NoSuchElementException 空の場合
     */
    T last() {
        if (root == null) throw new java.util.NoSuchElementException();
        return maxNode(root).key;
    }

    /**
     * 最小の要素を1個削除して返す。
     * @return 削除した最小値
     */
    T pollFirst() { T value = first(); remove(value); return value; }

    /**
     * 最大の要素を1個削除して返す。
     * @return 削除した最大値
     */
    T pollLast() { T value = last(); remove(value); return value; }

    /**
     * valueより小さい最大の値を取得する。
     * @param value 比較対象
     * @return 該当する値（存在しなければnull）
     */
    T lower(T value) { return neighbor(value, false, false); }

    /**
     * value以下の最大の値を取得する。
     * @param value 比較対象
     * @return 該当する値（存在しなければnull）
     */
    T floor(T value) { return neighbor(value, true, false); }

    /**
     * value以上の最小の値を取得する。
     * @param value 比較対象
     * @return 該当する値（存在しなければnull）
     */
    T ceiling(T value) { return neighbor(value, true, true); }

    /**
     * valueより大きい最小の値を取得する。
     * @param value 比較対象
     * @return 該当する値（存在しなければnull）
     */
    T higher(T value) { return neighbor(value, false, true); }

    /**
     * 指定した比較条件の直前または直後の要素を検索する。
     * @param value 基準値
     * @param inclusive 等しい値も含めるならtrue
     * @param successor 大きい方向ならtrue、小さい方向ならfalse
     * @return 該当する要素またはnull
     */
    private T neighbor(T value, boolean inclusive, boolean successor) {
        java.util.Objects.requireNonNull(value, "value");
        Node<T> node = root;
        T result = null;
        while (node != null) {
            int c = compare(value, node.key);
            if (c == 0 && inclusive) return node.key;
            if ((successor && c < 0) || (!successor && c > 0)) {
                result = node.key;
                node = successor ? node.left : node.right;
            } else {
                node = successor ? node.right : node.left;
            }
        }
        return result;
    }

    /**
     * 重複を含む昇順の反復子を取得する。
     * 走査中の変更はConcurrentModificationException。
     * @return 昇順の反復子（remove非対応）
     */
    public java.util.Iterator<T> iterator() {
        return new java.util.Iterator<>() {
            private final java.util.ArrayDeque<Node<T>> stack = new java.util.ArrayDeque<>();
            private final int expected = modCount;
            private T current;
            private long remaining;

            {
                pushLeft(root);
            }

            /** 指定部分木の最左経路をスタックへ積む。 */
            private void pushLeft(Node<T> x) {
                while (x != null) {
                    stack.push(x);
                    x = x.left;
                }
            }

            /** @return 次の要素が存在すればtrue。 */
            public boolean hasNext() {
                if (expected != modCount) throw new java.util.ConcurrentModificationException();
                return remaining != 0 || !stack.isEmpty();
            }

            /** @return 次の要素。 */
            public T next() {
                if (!hasNext()) throw new java.util.NoSuchElementException();
                if (remaining == 0) {
                    Node<T> node = stack.pop();
                    current = node.key;
                    remaining = node.count;
                    pushLeft(node.right);
                }
                remaining--;
                return current;
            }
        };
    }
}
