/** long整数の乗算（単位元1）を定義するCommutativeMonoid。 */
class LongProductMonoid implements CommutativeMonoid<Long> {
    /** @return 単位元。 */
    public Long e() { return 1L; }

    /** @return aとbを結合した結果。 */
    public Long op(Long a, Long b) { return a * b; }
}
