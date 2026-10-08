class ModMath {
    // mod <= Integer.MAX_VALUE, exp >= 0
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

    // mod is prime, a % mod != 0
    static long inv(long a, long mod) {
        if (mod <= 1 || mod > Integer.MAX_VALUE || a % mod == 0)
            throw new IllegalArgumentException();
        return pow(a, mod - 2, mod);
    }
}
