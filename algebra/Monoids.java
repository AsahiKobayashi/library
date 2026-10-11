/** 競技プログラミングでよく使うモノイドの定義集。 */
class Monoids {
    private Monoids() {}

    /** long の加算。単位元は0。 */
    static final CommutativeMonoid<Long> LONG_SUM = new CommutativeMonoid<>() {
        public Long e() { return 0L; }
        public Long op(Long a, Long b) { return a + b; }
    };

    /** long の乗算。単位元は1。 */
    static final CommutativeMonoid<Long> LONG_PRODUCT = new CommutativeMonoid<>() {
        public Long e() { return 1L; }
        public Long op(Long a, Long b) { return a * b; }
    };

    /** long の最小値。単位元はLong.MAX_VALUE。 */
    static final CommutativeMonoid<Long> LONG_MIN = new CommutativeMonoid<>() {
        public Long e() { return Long.MAX_VALUE; }
        public Long op(Long a, Long b) { return Math.min(a, b); }
    };

    /** long の最大値。単位元はLong.MIN_VALUE。 */
    static final CommutativeMonoid<Long> LONG_MAX = new CommutativeMonoid<>() {
        public Long e() { return Long.MIN_VALUE; }
        public Long op(Long a, Long b) { return Math.max(a, b); }
    };

    /** 非負のlongの最大公約数。単位元は0。 */
    static final CommutativeMonoid<Long> LONG_GCD = new CommutativeMonoid<>() {
        public Long e() { return 0L; }
        public Long op(Long a, Long b) {
            if (a < 0 || b < 0) throw new IllegalArgumentException("GCDの引数は非負にしてください");
            while (b != 0) { long t = a % b; a = b; b = t; }
            return a;
        }
    };

    /** long のビット単位XOR。単位元は0。 */
    static final CommutativeMonoid<Long> LONG_XOR = new CommutativeMonoid<>() {
        public Long e() { return 0L; }
        public Long op(Long a, Long b) { return a ^ b; }
    };

    /** Boolean の論理AND。単位元はtrue。 */
    static final CommutativeMonoid<Boolean> BOOL_AND = new CommutativeMonoid<>() {
        public Boolean e() { return true; }
        public Boolean op(Boolean a, Boolean b) { return a && b; }
    };

    /** Boolean の論理OR。単位元はfalse。 */
    static final CommutativeMonoid<Boolean> BOOL_OR = new CommutativeMonoid<>() {
        public Boolean e() { return false; }
        public Boolean op(Boolean a, Boolean b) { return a || b; }
    };

    /** 文字列の連結。結合順序を保持する非可換モノイド。 */
    static final Monoid<String> STRING_CONCAT = new Monoid<>() {
        public String e() { return ""; }
        public String op(String a, String b) { return a + b; }
    };
}
