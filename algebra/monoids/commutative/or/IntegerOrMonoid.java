/**
 * Integerのビット単位ORを表す可換モノイド。
 * 加減乗算の結果がintの範囲を超える場合、Javaのintとしてオーバーフローする。
 */
class IntegerOrMonoid implements CommutativeMonoid<Integer> {
    /** @return 単位元。 */
    public Integer e() { return 0; }

    /**
     * 二つの整数をビット単位ORで結合する。
     * @param a 左側の値
     * @param b 右側の値
     * @return 結合結果
     */
    public Integer op(Integer a, Integer b) { return a | b; }
}
