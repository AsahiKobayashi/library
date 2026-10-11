/** 競技プログラミングでよく使う遅延更新用モノイド作用の定義集。 */
class MonoidActions {
    private MonoidActions() {}

    /**
     * 区間和と区間長を組にした値。
     * 区間加算・区間代入をしたとき、和の増減を区間長から求める。
     */
    static final class SumLen {
        final long sum;
        final int len;

        /** @param sum 区間和 @param len 区間の要素数 */
        SumLen(long sum, int len) {
            this.sum = sum;
            this.len = len;
        }

        /** @return 比較しやすい文字列表現。 */
        public String toString() { return "(" + sum + ", " + len + ")"; }

        /** @return 区間和と区間長が一致すればtrue。 */
        public boolean equals(Object other) {
            if (!(other instanceof SumLen)) return false;
            SumLen b = (SumLen) other;
            return sum == b.sum && len == b.len;
        }

        /** @return 区間和と区間長のハッシュ値。 */
        public int hashCode() { return java.util.Objects.hash(sum, len); }
    }

    /** 1要素の値をSumLenに変換する。 */
    static SumLen leaf(long x) { return new SumLen(x, 1); }

    /** 区間加算・区間和。更新Fは加算量（Long）。 */
    static final MonoidAction<SumLen, Long> RANGE_ADD_SUM = new MonoidAction<>() {
        public SumLen e() { return new SumLen(0L, 0); }
        public SumLen op(SumLen a, SumLen b) {
            return new SumLen(a.sum + b.sum, a.len + b.len);
        }
        public Long id() { return 0L; }
        public SumLen mapping(Long f, SumLen x) {
            return new SumLen(x.sum + f * x.len, x.len);
        }
        public Long composition(Long f, Long g) { return f + g; }
    };

    /**
     * 区間代入・区間和。更新Fは代入値（Long）。
     * nullを「更新なし」を表す単位元とする。値としてのnullは使用しない。
     */
    static final MonoidAction<SumLen, Long> RANGE_ASSIGN_SUM = new MonoidAction<>() {
        public SumLen e() { return new SumLen(0L, 0); }
        public SumLen op(SumLen a, SumLen b) {
            return new SumLen(a.sum + b.sum, a.len + b.len);
        }
        public Long id() { return null; }
        public SumLen mapping(Long f, SumLen x) {
            return f == null ? x : new SumLen(f * x.len, x.len);
        }
        public Long composition(Long f, Long g) { return f == null ? g : f; }
    };
}
