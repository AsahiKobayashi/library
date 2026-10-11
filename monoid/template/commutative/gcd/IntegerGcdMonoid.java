/**
 * Integerの最大公約数（引数は非負整数）を表す可換モノイド。
 * 加減乗算の結果がintの範囲を超える場合、Javaのintとしてオーバーフローする。
 */
class IntegerGcdMonoid implements CommutativeMonoid<Integer> {
    /** @return 単位元。 */
    public Integer e() { return 0; }

    /**
     * 二つの整数を最大公約数（引数は非負整数）で結合する。
     * @param a 左側の値
     * @param b 右側の値
     * @return 結合結果
     */
    public Integer op(Integer a, Integer b) { return gcd(a, b); }

    /** 非負整数の最大公約数を求める。 */
    private int gcd(int a, int b) {
        if (a < 0 || b < 0) throw new IllegalArgumentException("GCDの引数は非負整数にしてください");
        while (b != 0) { int t = a % b; a = b; b = t; }
        return a;
    }
}
