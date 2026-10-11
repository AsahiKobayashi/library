/** RollingHash.Hashを左から右へ結合する非可換モノイド。 */
class RollingHashMonoid implements Monoid<RollingHash.Hash> {
    private final RollingHash rolling;

    /** @param rolling 結合する列の最大長に合わせて作ったRollingHash */
    RollingHashMonoid(RollingHash rolling) { this.rolling = java.util.Objects.requireNonNull(rolling); }

    /** @return 空区間のハッシュ。 */
    public RollingHash.Hash e() { return rolling.e(); }

    /**
     * 左区間の後ろに右区間を結合する。
     * @param a 左区間
     * @param b 右区間
     * @return 結合後のハッシュ
     */
    public RollingHash.Hash op(RollingHash.Hash a, RollingHash.Hash b) {
        return rolling.concat(a, b);
    }
}
