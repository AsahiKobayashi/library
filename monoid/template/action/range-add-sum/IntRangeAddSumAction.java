/**
 * Integerの区間加算・区間和に対応するモノイド作用。
 * 区間集約値はIntSumLen、更新値はIntegerの加算量。
 */
class IntRangeAddSumAction implements MonoidAction<IntSumLen, Integer> {
    /** @return 和0・長さ0の単位元。 */
    public IntSumLen e() { return new IntSumLen(0, 0); }

    /**
     * 区間和と区間長をまとめる。
     * @param a 左側の区間
     * @param b 右側の区間
     * @return 結合後の区間
     */
    public IntSumLen op(IntSumLen a, IntSumLen b) {
        return new IntSumLen(a.sum + b.sum, a.len + b.len);
    }

    /** @return 更新しないことを示す加算量0。 */
    public Integer id() { return 0; }

    /**
     * 区間内の各値にfを加える。
     * @param f 加算量
     * @param x 更新前の区間和と長さ
     * @return 更新後の区間和と長さ
     */
    public IntSumLen mapping(Integer f, IntSumLen x) {
        return new IntSumLen(x.sum + f * x.len, x.len);
    }

    /**
     * 先にg、その後fを加算する更新を合成する。
     * @param f 新しい加算量
     * @param g 以前の加算量
     * @return 合成した加算量
     */
    public Integer composition(Integer f, Integer g) { return f + g; }
}
