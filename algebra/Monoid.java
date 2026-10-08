/**
 * 結合法則と単位元を持つ二項演算（モノイド）。
 * @param <T> 管理する値の型
 */
interface Monoid<T> {
    /** @return 単位元。 */
    T identity();

    /**
     * 2つの値を左から右の順番で結合する。
     * @param a 左側の値
     * @param b 右側の値
     * @return 結合結果
     */
    T combine(T a, T b);
}
