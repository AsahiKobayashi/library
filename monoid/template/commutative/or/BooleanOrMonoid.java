/** 真偽値の論理OR（単位元false）を定義するCommutativeMonoid。 */
class BooleanOrMonoid implements CommutativeMonoid<Boolean> {
    /** @return 単位元。 */
    public Boolean e() { return false; }

    /** @return aとbを結合した結果。 */
    public Boolean op(Boolean a, Boolean b) { return a || b; }
}
