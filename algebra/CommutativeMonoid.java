/**
 * 交換法則も成り立つモノイド（可換モノイド）。
 * op(a, b) == op(b, a) を満たす。逆元は必要ない。
 * @param <T> 管理する値の型
 */
interface CommutativeMonoid<T> extends Monoid<T> {
}
