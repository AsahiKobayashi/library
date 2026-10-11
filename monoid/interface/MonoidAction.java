/**
 * 遅延セグメント木で用いるモノイドと作用の組。
 * composition(f, g) は「先にg、次にf」を意味する。
 * mappingは結合演算を保つ必要があるため、区間和なら要素数も値に持たせる。
 * @param <S> 区間集約の値の型
 * @param <F> 更新操作の型
 */
interface MonoidAction<S, F> extends Monoid<S> {
    /** @return 更新操作の単位元（何もしない操作）。 */
    F id();

    /**
     * 更新操作fを区間集約値xに適用する。
     * @param f 更新操作
     * @param x 更新前の区間集約値
     * @return 更新後の区間集約値
     */
    S mapping(F f, S x);

    /**
     * 更新操作を合成する。gの後にfを実行する。
     * @param f 新しく適用する操作
     * @param g 既に保持している操作
     * @return 合成された更新操作
     */
    F composition(F f, F g);
}
