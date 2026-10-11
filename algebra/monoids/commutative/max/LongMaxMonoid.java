/** long整数の最大値（単位元Long.MIN_VALUE）を定義するCommutativeMonoid。 */
class LongMaxMonoid implements CommutativeMonoid<Long> {
    /** @return 単位元。 */
    public Long e() { return Long.MIN_VALUE; }

    /** @return aとbを結合した結果。 */
    public Long op(Long a, Long b) { return Math.max(a, b); }
}
