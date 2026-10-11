/**
 * 交換法則と逆元を持つモノイド（可換群）。
 * Fenwick Tree の区間集約に使用する。
 * @param <T> 管理する値の型
 */
interface CommutativeGroup<T> extends Monoid<T> {
    /**
     * 値の逆元を返す。
     * @param a 対象の値
     * @return op(a, inverse(a)) が単位元となる値
     */
    T inverse(T a);
}
