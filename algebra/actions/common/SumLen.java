/**
 * 区間和と区間長を組にした値。
 * 区間加算・区間代入で和の変化量を求めるために用いる。
 */
final class SumLen {
    final long sum;
    final int len;

    /** @param sum 区間和 @param len 区間長 */
    SumLen(long sum, int len) {
        this.sum = sum;
        this.len = len;
    }

    /** 1要素から区間長付きの値を作る。 */
    static SumLen leaf(long x) { return new SumLen(x, 1); }

    /** @return 比較用の文字列表現。 */
    public String toString() { return "(" + sum + ", " + len + ")"; }

    /** @return 区間和と区間長が等しければtrue。 */
    public boolean equals(Object other) {
        if (!(other instanceof SumLen)) return false;
        SumLen s = (SumLen) other;
        return sum == s.sum && len == s.len;
    }

    /** @return 値に対応するハッシュコード。 */
    public int hashCode() { return java.util.Objects.hash(sum, len); }
}
