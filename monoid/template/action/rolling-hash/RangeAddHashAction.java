/**
 * Rolling Hashで区間内の整数に同じ値を加算するモノイド作用。
 * 更新fは加算量（Long）。各整数の桁あふれを伴わない加算を想定する。
 */
class RangeAddHashAction implements MonoidAction<RollingHash.Hash, Long> {
    private final RollingHash rolling;

    /** @param rolling 対象の最大長を指定したRollingHash */
    RangeAddHashAction(RollingHash rolling) { this.rolling = java.util.Objects.requireNonNull(rolling); }

    /** @return 空区間ハッシュ。 */
    public RollingHash.Hash e() { return rolling.e(); }

    /** 左区間の後ろに右区間を結合する。 */
    public RollingHash.Hash op(RollingHash.Hash a, RollingHash.Hash b) { return rolling.concat(a, b); }

    /** @return 加算しない操作である0。 */
    public Long id() { return 0L; }

    /** 区間内のすべての数値にfを加算する。 */
    public RollingHash.Hash mapping(Long f, RollingHash.Hash x) { return rolling.add(x, f); }

    /** 古い加算量gに新しい加算量fを合成する。 */
    public Long composition(Long f, Long g) { return f + g; }
}
