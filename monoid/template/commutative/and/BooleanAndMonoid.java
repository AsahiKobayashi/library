/** 真偽値の論理AND（単位元true）を定義するCommutativeMonoid。 */
class BooleanAndMonoid implements CommutativeMonoid<Boolean> {
    /** @return 単位元。 */
    public Boolean e() { return true; }

    /** @return aとbを結合した結果。 */
    public Boolean op(Boolean a, Boolean b) { return a && b; }
}
