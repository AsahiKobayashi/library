/** 非負整数の最大公約数（単位元0）を定義するCommutativeMonoid。 */
class LongGcdMonoid implements CommutativeMonoid<Long> {
    /** @return 単位元。 */
    public Long e() { return 0L; }

    /** @return aとbを結合した結果。 */
    public Long op(Long a, Long b) { return gcd(a, b); }

    /** 二つの非負整数の最大公約数を求める。 */
    private long gcd(long a, long b) {
        if (a < 0 || b < 0) throw new IllegalArgumentException("GCDの入力は非負");
        while (b != 0) { long t = a % b; a = b; b = t; }
        return a;
    }
}
