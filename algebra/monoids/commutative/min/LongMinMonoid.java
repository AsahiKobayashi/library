/** long整数の最小値（単位元Long.MAX_VALUE）を定義するCommutativeMonoid。 */
class LongMinMonoid implements CommutativeMonoid<Long> {
    /** @return 単位元。 */
    public Long e() { return Long.MAX_VALUE; }

    /** @return aとbを結合した結果。 */
    public Long op(Long a, Long b) { return Math.min(a, b); }
}
