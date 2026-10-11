/**
 * Integerの区間和と要素数の組。
 * 合計や更新結果がintの範囲を超える場合はオーバーフローする。
 */
final class IntSumLen {
    final int sum;
    final int len;

    /**
     * 区間和と要素数を設定する。
     * @param sum 区間和
     * @param len 要素数
     */
    IntSumLen(int sum, int len) {
        this.sum = sum;
        this.len = len;
    }

    /**
     * 1要素を区間和と要素数の組に変換する。
     * @param x 要素の値
     * @return 和がx、長さ1の組
     */
    static IntSumLen leaf(int x) { return new IntSumLen(x, 1); }

    /** @return 区間和と長さを示す文字列表現。 */
    public String toString() { return "(" + sum + ", " + len + ")"; }

    /**
     * 区間和と要素数が一致するか調べる。
     * @param other 比較対象
     * @return 両方の値が等しければtrue
     */
    public boolean equals(Object other) {
        if (!(other instanceof IntSumLen)) return false;
        IntSumLen x = (IntSumLen) other;
        return sum == x.sum && len == x.len;
    }

    /** @return 区間和と要素数から求めたハッシュ値。 */
    public int hashCode() { return java.util.Objects.hash(sum, len); }
}
