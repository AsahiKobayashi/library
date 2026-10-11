/**
 * Integerの区間代入・区間和に対応するモノイド作用。
 * 区間集約値はIntSumLen、更新値はIntegerの代入値。
 * 更新値のnullは「代入しない」を表す。
 */
class IntRangeAssignSumAction implements MonoidAction<IntSumLen, Integer> {
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

    /** @return 代入しないことを表すnull。 */
    public Integer id() { return null; }

    /**
     * 区間内の各値をfで置き換える。
     * @param f 代入値。nullなら変更しない
     * @param x 更新前の区間和と長さ
     * @return 更新後の区間和と長さ
     */
    public IntSumLen mapping(Integer f, IntSumLen x) {
        return f == null ? x : new IntSumLen(f * x.len, x.len);
    }

    /**
     * 先にg、次にfを適用する更新を合成する。
     * @param f 新しい代入値
     * @param g 以前の代入値
     * @return 新しい方を優先した代入値
     */
    public Integer composition(Integer f, Integer g) { return f == null ? g : f; }
}
