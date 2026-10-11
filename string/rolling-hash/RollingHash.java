/**
 * 2つの素数法を用いるRolling Hash。左から右への順序を保持する。
 * 文字列はUTF-16のchar単位で扱い、文字コードに1を加えてハッシュ化する。
 * 同じ基数・法なので異なるインスタンス間のHashも比較可能。
 * ハッシュ一致には理論上衝突の可能性がある。
 */
class RollingHash {
    static final long MOD1 = 1_000_000_007L;
    static final long MOD2 = 1_000_000_009L;
    static final long BASE = 911_382_323L;

    /** 区間のハッシュと長さ。変更不可。 */
    static final class Hash {
        final long h1, h2;
        final int len;

        /** @param h1 第1の法でのハッシュ @param h2 第2の法でのハッシュ @param len 区間長 */
        Hash(long h1, long h2, int len) {
            this.h1 = h1;
            this.h2 = h2;
            this.len = len;
        }

        /** @return 長さと両方のハッシュが等しければtrue。 */
        public boolean equals(Object obj) {
            if (!(obj instanceof Hash)) return false;
            Hash b = (Hash) obj;
            return len == b.len && h1 == b.h1 && h2 == b.h2;
        }

        /** @return 長さとハッシュ値によるハッシュコード。 */
        public int hashCode() { return java.util.Objects.hash(h1, h2, len); }

        /** @return 内容の文字列表現。 */
        public String toString() { return "(" + h1 + ", " + h2 + ", len=" + len + ")"; }
    }

    private final int maxLength;
    private final long[] pow1, pow2, ones1, ones2;
    private long[] pre1, pre2;

    /**
     * 結合や更新で扱う最大の区間長を指定する。元配列を保持しない場合はこちら。
     * @param maxLength 扱う最大長（0以上）
     */
    RollingHash(int maxLength) {
        if (maxLength < 0) throw new IllegalArgumentException("最大長は0以上");
        this.maxLength = maxLength;
        pow1 = new long[maxLength + 1];
        pow2 = new long[maxLength + 1];
        ones1 = new long[maxLength + 1];
        ones2 = new long[maxLength + 1];
        pow1[0] = pow2[0] = 1;
        for (int i = 1; i <= maxLength; i++) {
            pow1[i] = pow1[i - 1] * BASE % MOD1;
            pow2[i] = pow2[i - 1] * BASE % MOD2;
            ones1[i] = (ones1[i - 1] * BASE + 1) % MOD1;
            ones2[i] = (ones2[i - 1] * BASE + 1) % MOD2;
        }
    }

    /**
     * 文字列の区間ハッシュを前計算する。
     * @param text 元文字列（UTF-16のchar単位）
     */
    RollingHash(String text) {
        this(text.length());
        long[] codes = new long[text.length()];
        for (int i = 0; i < codes.length; i++) codes[i] = text.charAt(i) + 1L;
        build(codes);
    }

    /**
     * 整数列の区間ハッシュを前計算する。負の値も使用できる。
     * @param values 元の整数列
     */
    RollingHash(int[] values) {
        this(values.length);
        long[] codes = new long[values.length];
        for (int i = 0; i < values.length; i++) codes[i] = values[i];
        build(codes);
    }

    /**
     * long整数列の区間ハッシュを前計算する。値は各法で正規化する。
     * @param values 元の整数列
     */
    RollingHash(long[] values) {
        this(values.length);
        build(values);
    }

    /** 元配列の累積ハッシュを構築する。 */
    private void build(long[] values) {
        pre1 = new long[values.length + 1];
        pre2 = new long[values.length + 1];
        for (int i = 0; i < values.length; i++) {
            pre1[i + 1] = (pre1[i] * BASE + norm(values[i], MOD1)) % MOD1;
            pre2[i + 1] = (pre2[i] * BASE + norm(values[i], MOD2)) % MOD2;
        }
    }

    /** 数値を[0, mod)に正規化する。 */
    private static long norm(long x, long mod) {
        long r = x % mod;
        return r < 0 ? r + mod : r;
    }

    /** @return 前計算時に指定した最大長。 */
    int capacity() { return maxLength; }

    /** @return 空区間のハッシュ（結合の単位元）。 */
    Hash e() { return new Hash(0, 0, 0); }

    /**
     * 1つの数値をハッシュ化する。
     * @param value 数値
     * @return 長さ1のハッシュ
     */
    Hash of(long value) { return new Hash(norm(value, MOD1), norm(value, MOD2), 1); }

    /**
     * 1文字をハッシュ化する。文字列コンストラクタと同じ符号化を行う。
     * @param c 文字
     * @return 長さ1のハッシュ
     */
    Hash ofChar(char c) { return of(c + 1L); }

    /**
     * 2つの区間を左から右に連結する。
     * @param left 左の区間
     * @param right 右の区間
     * @return 連結後のハッシュ
     */
    Hash concat(Hash left, Hash right) {
        long len = (long) left.len + right.len;
        if (len > maxLength || left.len < 0 || right.len < 0)
            throw new IllegalArgumentException("区間長が最大長を超えました");
        return new Hash(
            (left.h1 * pow1[right.len] + right.h1) % MOD1,
            (left.h2 * pow2[right.len] + right.h2) % MOD2,
            (int) len
        );
    }

    /**
     * 同じ数値を指定回数並べた区間のハッシュを計算する（区間代入用）。
     * @param value 繰り返す数値
     * @param len 区間の長さ
     * @return 指定回数繰り返したハッシュ
     */
    Hash repeat(long value, int len) {
        if (len < 0 || len > maxLength) throw new IndexOutOfBoundsException();
        return new Hash(norm(value, MOD1) * ones1[len] % MOD1,
                        norm(value, MOD2) * ones2[len] % MOD2, len);
    }

    /**
     * 区間内の各数値にdeltaを加えたハッシュを計算する（区間加算用）。
     * 整数値の加算でオーバーフローが起きない場合の意味を想定する。
     * @param hash 更新前のハッシュ
     * @param delta 全要素に加算する値
     * @return 更新後のハッシュ
     */
    Hash add(Hash hash, long delta) {
        if (hash.len < 0 || hash.len > maxLength) throw new IndexOutOfBoundsException();
        return new Hash((hash.h1 + norm(delta, MOD1) * ones1[hash.len]) % MOD1,
                        (hash.h2 + norm(delta, MOD2) * ones2[hash.len]) % MOD2, hash.len);
    }

    /**
     * 元の文字列・配列の区間[l, r)のハッシュをO(1)で取得する。
     * @param l 区間の左端（含む）
     * @param r 区間の右端（含まない）
     * @return 区間ハッシュ
     */
    Hash hash(int l, int r) {
        if (pre1 == null) throw new IllegalStateException("元配列を指定していません");
        if (l < 0 || l > r || r >= pre1.length) throw new IndexOutOfBoundsException();
        int len = r - l;
        return new Hash((pre1[r] - pre1[l] * pow1[len] % MOD1 + MOD1) % MOD1,
                        (pre2[r] - pre2[l] * pow2[len] % MOD2 + MOD2) % MOD2, len);
    }

    /**
     * 別のRollingHashと区間が一致するかハッシュで判定する（衝突の可能性あり）。
     * @param l 自分の区間の左端
     * @param r 自分の区間の右端
     * @param other 比較対象
     * @param l2 比較先の左端
     * @param r2 比較先の右端
     * @return ハッシュが等しければtrue
     */
    boolean same(int l, int r, RollingHash other, int l2, int r2) {
        return hash(l, r).equals(other.hash(l2, r2));
    }

    /**
     * 自分の位置iと別の列の位置jから始まる最長共通接頭辞の長さをハッシュで求める。
     * @param i 自分の開始位置
     * @param other 比較対象
     * @param j 比較先の開始位置
     * @return 最長共通接頭辞の推定長（衝突の可能性あり）
     */
    int lcp(int i, RollingHash other, int j) {
        if (pre1 == null || other.pre1 == null) throw new IllegalStateException();
        if (i < 0 || i >= pre1.length || j < 0 || j >= other.pre1.length)
            throw new IndexOutOfBoundsException();
        int lo = 0, hi = Math.min(pre1.length - 1 - i, other.pre1.length - 1 - j) + 1;
        while (hi - lo > 1) {
            int mid = lo + (hi - lo) / 2;
            if (same(i, i + mid, other, j, j + mid)) lo = mid;
            else hi = mid;
        }
        return lo;
    }
}
