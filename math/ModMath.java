/** 整数の剰余演算に関する便利メソッド。 */
class ModMath {
    /**
     * aのexp乗をmodで割った余りを求める（二分累乗）。
     * @param a 底
     * @param exp 0以上の指数
     * @param mod 1以上Integer.MAX_VALUE以下の法
     * @return a^exp mod mod
     */
    static long pow(long a, long exp, long mod) {
        if (mod <= 0 || mod > Integer.MAX_VALUE || exp < 0)
            throw new IllegalArgumentException();
        long res = 1 % mod;
        a = (a % mod + mod) % mod;
        while (exp > 0) {
            if ((exp & 1) != 0) res = res * a % mod;
            a = a * a % mod;
            exp >>= 1;
        }
        return res;
    }

    /**
     * フェルマーの小定理で逆元を求める。
     * @param a modで割り切れない整数
     * @param mod 素数である法（Integer.MAX_VALUE以下）
     * @return aの乗法逆元
     */
    static long inv(long a, long mod) {
        if (mod <= 1 || mod > Integer.MAX_VALUE || a % mod == 0)
            throw new IllegalArgumentException();
        return pow(a, mod - 2, mod);
    }
}
