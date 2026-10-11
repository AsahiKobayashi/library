/**
 * Rolling Hashで区間内の全要素を同じ数値に代入するモノイド作用。
 * 更新fは代入する数値（Long）。nullは「更新なし」を表す。
 * 文字列の場合は1文字のコードを char + 1 として渡す。
 */
class RangeAssignHashAction implements MonoidAction<RollingHash.Hash, Long> {
    private final RollingHash rolling;

    /** @param rolling 対象の最大長を指定したRollingHash */
    RangeAssignHashAction(RollingHash rolling) { this.rolling = java.util.Objects.requireNonNull(rolling); }

    /** @return 空区間ハッシュ。 */
    public RollingHash.Hash e() { return rolling.e(); }

    /** 左区間の後ろに右区間を結合する。 */
    public RollingHash.Hash op(RollingHash.Hash a, RollingHash.Hash b) { return rolling.concat(a, b); }

    /** @return 更新なしを表すnull。 */
    public Long id() { return null; }

    /** 区間内のすべての要素をfに代入する。 */
    public RollingHash.Hash mapping(Long f, RollingHash.Hash x) {
        return f == null ? x : rolling.repeat(f, x.len);
    }

    /** 古い操作gの後に新しい操作fを適用する。 */
    public Long composition(Long f, Long g) { return f == null ? g : f; }
}
