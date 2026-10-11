/** long整数の加算（単位元0）を定義するCommutativeMonoid。 */
class LongSumMonoid implements CommutativeMonoid<Long> {
    /** @return 単位元。 */
    public Long e() { return 0L; }

    /** @return aとbを結合した結果。 */
    public Long op(Long a, Long b) { return a + b; }
}
