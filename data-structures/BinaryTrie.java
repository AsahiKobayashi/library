/**
 * 非負整数を管理するBinary Trie（重複を許す多重集合）。
 * 下位bitWidthビットだけを扱い、値の範囲は[0, 2^bitWidth)。
 * bitWidthは1～63。符号付き負数は扱わない。
 * 挿入・削除・検索・順位取得はO(bitWidth)、全要素XORはO(1)。
 * 競プロでのXOR最小化・最大化、k番目の値などに利用する。
 */
class BinaryTrie {
    private static final class Node {
        Node zero, one;
        long count;
    }

    private final int bitWidth;
    private final long maximum;
    private final Node root = new Node();
    private long xorMask;

    /** 標準の31ビット（0以上2^31未満）で構築する。 */
    BinaryTrie() { this(31); }

    /**
     * 扱うビット数を指定する。
     * @param bitWidth ビット数（1～63）。63ならLong.MAX_VALUEまで扱える
     */
    BinaryTrie(int bitWidth) {
        if (bitWidth < 1 || bitWidth > 63)
            throw new IllegalArgumentException("bitWidth must be between 1 and 63");
        this.bitWidth = bitWidth;
        maximum = bitWidth == 63 ? Long.MAX_VALUE : (1L << bitWidth) - 1;
    }

    /** 指定した値が使用可能な範囲にあるか検証する。 */
    private void check(long value) {
        if (value < 0 || value > maximum)
            throw new IllegalArgumentException("value out of configured bit range: " + value);
    }

    /** @return 設定したビット数。 */
    int bitWidth() { return bitWidth; }

    /** @return 重複を含む要素の総数。 */
    long size() { return root.count; }

    /** @return 要素がなければtrue。 */
    boolean isEmpty() { return root.count == 0; }

    /**
     * 現在の値を1個挿入する。
     * @param value 挿入する非負整数
     */
    void add(long value) {
        check(value);
        long raw = value ^ xorMask;
        Node node = root;
        node.count++;
        for (int i = bitWidth - 1; i >= 0; i--) {
            if (((raw >>> i) & 1L) == 0) {
                if (node.zero == null) node.zero = new Node();
                node = node.zero;
            } else {
                if (node.one == null) node.one = new Node();
                node = node.one;
            }
            node.count++;
        }
    }

    /**
     * 指定値の出現個数を求める。
     * @param value 調べる非負整数
     * @return 出現個数
     */
    long count(long value) {
        check(value);
        long raw = value ^ xorMask;
        Node node = root;
        for (int i = bitWidth - 1; i >= 0; i--) {
            node = ((raw >>> i) & 1L) == 0 ? node.zero : node.one;
            if (node == null) return 0;
        }
        return node.count;
    }

    /**
     * 指定値が1個以上含まれるか判定する。
     * @param value 調べる非負整数
     * @return 含まれるならtrue
     */
    boolean contains(long value) { return count(value) != 0; }

    /**
     * 指定値を1個だけ削除する。なくなったノードは切り離す。
     * @param value 削除する非負整数
     * @return 実際に削除できたならtrue
     */
    boolean erase(long value) {
        check(value);
        if (count(value) == 0) return false;
        long raw = value ^ xorMask;
        Node node = root;
        node.count--;
        for (int i = bitWidth - 1; i >= 0; i--) {
            boolean one = ((raw >>> i) & 1L) != 0;
            Node child = one ? node.one : node.zero;
            child.count--;
            if (child.count == 0) {
                if (one) node.one = null;
                else node.zero = null;
                break;
            }
            node = child;
        }
        return true;
    }

    /**
     * 全要素にXORを一括適用する。
     * @param mask XORする非負整数
     */
    void xorAll(long mask) {
        check(mask);
        xorMask ^= mask;
    }

    /**
     * 昇順でk番目の値を求める（重複を含む）。
     * @param k 0始まりの順位
     * @return k番目の値
     * @throws IndexOutOfBoundsException kが範囲外の場合
     */
    long kth(long k) {
        if (k < 0 || k >= root.count) throw new IndexOutOfBoundsException();
        Node node = root;
        long result = 0;
        for (int i = bitWidth - 1; i >= 0; i--) {
            int maskBit = (int) ((xorMask >>> i) & 1);
            Node left = maskBit == 0 ? node.zero : node.one;
            long leftCount = left == null ? 0 : left.count;
            if (k < leftCount) node = left;
            else {
                k -= leftCount;
                node = maskBit == 0 ? node.one : node.zero;
                result |= 1L << i;
            }
        }
        return result;
    }

    /**
     * 指定値より小さい要素の数を求める（重複を含む）。
     * bitWidthが63未満なら上限2^bitWidthを指定してもよい。
     * @param value 閾値（非負）
     * @return value未満の要素数
     */
    long countLessThan(long value) {
        if (value < 0) throw new IllegalArgumentException("value must be non-negative");
        if (value > maximum) return root.count;
        Node node = root;
        long result = 0;
        for (int i = bitWidth - 1; i >= 0 && node != null; i--) {
            int maskBit = (int) ((xorMask >>> i) & 1);
            Node left = maskBit == 0 ? node.zero : node.one;
            Node right = maskBit == 0 ? node.one : node.zero;
            if (((value >>> i) & 1L) == 0) node = left;
            else {
                if (left != null) result += left.count;
                node = right;
            }
        }
        return result;
    }

    /**
     * 指定値以上の最小の要素を求める。
     * @param value 下限（非負）
     * @return 該当する値。なければnull
     */
    Long lowerBound(long value) {
        long k = countLessThan(value);
        return k == size() ? null : kth(k);
    }

    /** @return 最小値。空集合なら例外を投げる。 */
    long min() { return kth(0); }

    /** @return 最大値。空集合なら例外を投げる。 */
    long max() { return kth(size() - 1); }

    /**
     * 指定値とのXORを最小または最大にする要素を取得する。
     * @param value 比較する非負整数
     * @param maximize XORを最大化するならtrue
     * @return 最適な要素（XOR結果そのものではない）
     */
    private long bestXorElement(long value, boolean maximize) {
        check(value);
        if (isEmpty()) throw new java.util.NoSuchElementException();
        long query = value ^ xorMask;
        Node node = root;
        long raw = 0;
        for (int i = bitWidth - 1; i >= 0; i--) {
            int same = (int) ((query >>> i) & 1L);
            int preferred = maximize ? same ^ 1 : same;
            Node chosen = preferred == 0 ? node.zero : node.one;
            if (chosen == null || chosen.count == 0) preferred ^= 1;
            if (preferred == 1) raw |= 1L << i;
            node = preferred == 0 ? node.zero : node.one;
        }
        return raw ^ xorMask;
    }

    /**
     * 指定値とのXORが最小になる要素を取得する。
     * @param value 比較する非負整数
     * @return XOR値を最小化する集合内の要素
     */
    long argMinXor(long value) { return bestXorElement(value, false); }

    /**
     * 指定値とのXORが最大になる要素を取得する。
     * @param value 比較する非負整数
     * @return XOR値を最大化する集合内の要素
     */
    long argMaxXor(long value) { return bestXorElement(value, true); }

    /**
     * 指定値との最小XOR値を取得する。
     * @param value 比較する非負整数
     * @return 最小XOR値
     */
    long minXor(long value) { return argMinXor(value) ^ value; }

    /**
     * 指定値との最大XOR値を取得する。
     * @param value 比較する非負整数
     * @return 最大XOR値
     */
    long maxXor(long value) { return argMaxXor(value) ^ value; }

    /** すべての要素とXORマスクをクリアする。 */
    void clear() {
        root.zero = root.one = null;
        root.count = 0;
        xorMask = 0;
    }
}
