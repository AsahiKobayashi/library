/** 区間加算・区間和を実現するモノイド作用。更新操作は加算量Long。 */
class RangeAddSumAction implements MonoidAction<SumLen, Long> {
    /** @return 和が0で長さ0の単位元。 */
    public SumLen e() { return new SumLen(0L, 0); }

    /** 二つの区間和と長さを結合する。 */
    public SumLen op(SumLen a, SumLen b) {
        return new SumLen(a.sum + b.sum, a.len + b.len);
    }

    /** @return 更新なしを表す0。 */
    public Long id() { return 0L; }

    /** 区間内の各要素にfを加算する。 */
    public SumLen mapping(Long f, SumLen x) {
        return new SumLen(x.sum + f * x.len, x.len);
    }

    /** gによる加算の後にfによる加算を合成する。 */
    public Long composition(Long f, Long g) { return f + g; }
}
