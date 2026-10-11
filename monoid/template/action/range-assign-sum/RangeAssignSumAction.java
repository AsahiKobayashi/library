/**
 * 区間代入・区間和を実現するモノイド作用。
 * 更新操作はLong。nullは「代入しない」の意味で使用する。
 */
class RangeAssignSumAction implements MonoidAction<SumLen, Long> {
    /** @return 和が0で長さ0の単位元。 */
    public SumLen e() { return new SumLen(0L, 0); }

    /** 二つの区間和と長さを結合する。 */
    public SumLen op(SumLen a, SumLen b) {
        return new SumLen(a.sum + b.sum, a.len + b.len);
    }

    /** @return 更新なしを示すnull。 */
    public Long id() { return null; }

    /** 区間内の各要素をfで置き換える（nullなら何もしない）。 */
    public SumLen mapping(Long f, SumLen x) {
        return f == null ? x : new SumLen(f * x.len, x.len);
    }

    /** gの後にfを適用する。fがnullなら既存のgを維持する。 */
    public Long composition(Long f, Long g) { return f == null ? g : f; }
}
