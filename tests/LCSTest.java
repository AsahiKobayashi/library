/** 最長共通部分列と最長共通部分文字列の動作を検証する。 */
class LCSTest {
    /** 期待値との一致を確認する。 */
    static void eq(Object expected, Object actual) {
        if (!java.util.Objects.equals(expected, actual))
            throw new AssertionError(expected + " != " + actual);
    }

    /** 文字列が元の部分列であるか判定する。 */
    static boolean subsequence(String sub, String s) {
        int j = 0;
        for (int i = 0; i < s.length() && j < sub.length(); i++)
            if (s.charAt(i) == sub.charAt(j)) j++;
        return j == sub.length();
    }

    /** 部分文字列の最長長を直接計算する。 */
    static int naiveSubstringLength(String a, String b) {
        int best = 0;
        for (int i = 0; i < a.length(); i++)
            for (int j = 0; j < b.length(); j++) {
                int len = 0;
                while (i + len < a.length() && j + len < b.length()
                    && a.charAt(i + len) == b.charAt(j + len)) len++;
                best = Math.max(best, len);
            }
        return best;
    }

    /** 最長共通部分列の長さを二次元DPで求める。 */
    static int naiveSubsequenceLength(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        for (int i = 0; i < a.length(); i++)
            for (int j = 0; j < b.length(); j++)
                dp[i + 1][j + 1] = a.charAt(i) == b.charAt(j)
                    ? dp[i][j] + 1 : Math.max(dp[i][j + 1], dp[i + 1][j]);
        return dp[a.length()][b.length()];
    }

    /** 決定的ケースとランダムケースを検証する。 */
    public static void main(String[] args) {
        eq(4, LongestCommonSubsequence.length("ABCBDAB", "BDCABA"));
        eq(4, LongestCommonSubsequence.one("ABCBDAB", "BDCABA").length());
        eq(3, LongestCommonSubstring.length("abcXYZdef", "123XYZ456"));
        eq("XYZ", LongestCommonSubstring.one("abcXYZdef", "123XYZ456"));
        eq(0, LongestCommonSubstring.length("", "abc"));
        eq("", LongestCommonSubsequence.one("", "abc"));
        eq(3, LongestCommonSubsequence.length(java.util.Arrays.asList(1, 2, 3, 4),
                                               java.util.Arrays.asList(2, 3, 4, 5)));
        eq(java.util.Arrays.asList(2, 3, 4), LongestCommonSubsequence.one(
            java.util.Arrays.asList(1, 2, 3, 4), java.util.Arrays.asList(2, 3, 4, 5)));
        java.util.Random rnd = new java.util.Random(2026);
        for (int t = 0; t < 750; t++) {
            int n = rnd.nextInt(13), m = rnd.nextInt(13);
            StringBuilder aa = new StringBuilder(), bb = new StringBuilder();
            for (int i = 0; i < n; i++) aa.append((char) ('a' + rnd.nextInt(4)));
            for (int i = 0; i < m; i++) bb.append((char) ('a' + rnd.nextInt(4)));
            String a = aa.toString(), b = bb.toString();
            int expectSub = naiveSubsequenceLength(a, b);
            eq(expectSub, LongestCommonSubsequence.length(a, b));
            String oneSub = LongestCommonSubsequence.one(a, b);
            eq(expectSub, oneSub.length());
            eq(true, subsequence(oneSub, a));
            eq(true, subsequence(oneSub, b));
            int expectStr = naiveSubstringLength(a, b);
            LongestCommonSubstring.Match match = LongestCommonSubstring.find(a, b);
            eq(expectStr, match.length);
            eq(expectStr, LongestCommonSubstring.length(a, b));
            eq(a.substring(match.startA, match.startA + match.length),
                b.substring(match.startB, match.startB + match.length));
            eq(expectStr, LongestCommonSubstring.one(a, b).length());
        }
        System.out.println("LCS and longest-common-substring tests passed");
    }
}
