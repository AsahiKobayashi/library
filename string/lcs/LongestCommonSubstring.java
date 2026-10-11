/**
 * 最長共通部分文字列（Longest Common Substring）。
 * 文字が連続した共通区間を求める。最長共通部分列とは異なる。
 * Rolling Hashの二分探索を使い、候補はregionMatchesで実際に照合する。
 * 通常はO((n+m) log(min(n,m)+1))程度。照合が多い場合はそれ以上かかる。
 * UTF-16のchar単位で扱う。
 */
class LongestCommonSubstring {
    private LongestCommonSubstring() {}

    /** 共通部分文字列の位置と長さ。 */
    static final class Match {
        final int startA, startB, length;

        /** 2つの文字列中の開始位置と一致長を保持する。 */
        Match(int startA, int startB, int length) {
            this.startA = startA;
            this.startB = startB;
            this.length = length;
        }

        /**
         * 1つ目の文字列から一致部分を切り出す。
         * @param a 1つ目の文字列
         * @return 共通部分文字列
         */
        String text(String a) { return a.substring(startA, startA + length); }
    }

    /**
     * 2つの文字列の最長共通部分文字列とその位置を求める。
     * @param a 1つ目の文字列
     * @param b 2つ目の文字列
     * @return 最長共通部分文字列の位置と長さ
     */
    static Match find(String a, String b) {
        RollingHash ra = new RollingHash(a), rb = new RollingHash(b);
        int lo = 0, hi = Math.min(a.length(), b.length()) + 1;
        Match best = new Match(0, 0, 0);
        while (hi - lo > 1) {
            int mid = lo + (hi - lo) / 2;
            Match found = findWithLength(a, b, ra, rb, mid);
            if (found != null) { lo = mid; best = found; }
            else hi = mid;
        }
        return best;
    }

    /**
     * 最長共通部分文字列の長さを返す。
     * @param a 1つ目の文字列
     * @param b 2つ目の文字列
     * @return 最長一致長
     */
    static int length(String a, String b) { return find(a, b).length; }

    /**
     * 最長共通部分文字列を1つ返す。
     * @param a 1つ目の文字列
     * @param b 2つ目の文字列
     * @return 最長共通部分文字列の1つ
     */
    static String one(String a, String b) { return find(a, b).text(a); }

    /** 指定長の共通部分文字列があるか探す（ハッシュ一致後に実文字列も確認）。 */
    private static Match findWithLength(String a, String b, RollingHash ra, RollingHash rb, int len) {
        if (len == 0) return new Match(0, 0, 0);
        boolean aIsShorter = a.length() <= b.length();
        String shortText = aIsShorter ? a : b;
        String longText = aIsShorter ? b : a;
        RollingHash shortHash = aIsShorter ? ra : rb;
        RollingHash longHash = aIsShorter ? rb : ra;
        java.util.HashMap<RollingHash.Hash, java.util.ArrayList<Integer>> positions = new java.util.HashMap<>();
        for (int i = 0; i + len <= shortText.length(); i++) {
            RollingHash.Hash hash = shortHash.hash(i, i + len);
            positions.computeIfAbsent(hash, k -> new java.util.ArrayList<>()).add(i);
        }
        for (int j = 0; j + len <= longText.length(); j++) {
            java.util.ArrayList<Integer> candidates = positions.get(longHash.hash(j, j + len));
            if (candidates == null) continue;
            for (int i : candidates) {
                if (!shortText.regionMatches(i, longText, j, len)) continue;
                return aIsShorter ? new Match(i, j, len) : new Match(j, i, len);
            }
        }
        return null;
    }
}
