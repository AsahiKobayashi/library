/** 文字列連結（演算順序を保持）を定義するMonoid。 */
class StringConcatMonoid implements Monoid<String> {
    /** @return 単位元。 */
    public String e() { return ""; }

    /** @return aとbを結合した結果。 */
    public String op(String a, String b) { return a + b; }
}
