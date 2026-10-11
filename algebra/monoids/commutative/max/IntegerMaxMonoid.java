/**
 * Integerの最大値を表す可換モノイド。
 * 加減乗算の結果がintの範囲を超える場合、Javaのintとしてオーバーフローする。
 */
class IntegerMaxMonoid implements CommutativeMonoid<Integer> {
    /** @return 単位元。 */
    public Integer e() { return Integer.MIN_VALUE; }

    /**
     * 二つの整数を最大値で結合する。
     * @param a 左側の値
     * @param b 右側の値
     * @return 結合結果
     */
    public Integer op(Integer a, Integer b) { return Math.max(a, b); }
}
