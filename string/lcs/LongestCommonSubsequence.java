/**
 * 最長共通部分列（LCS / Longest Common Subsequence）。
 * 文字は連続していなくてもよい。文字列はUTF-16のchar単位で扱う。
 * 長さの計算は時間O(nm)、追加空間O(min(n,m))。
 * 実際の部分列の復元は時間・空間ともO(nm)。
 */
class LongestCommonSubsequence {
    private LongestCommonSubsequence() {}

    /**
     * 2つの文字列の最長共通部分列の長さを返す。
     * @param a 1つ目の文字列
     * @param b 2つ目の文字列
     * @return LCSの長さ
     */
    static int length(String a, String b) {
        if (a.length() < b.length()) { String t = a; a = b; b = t; }
        int[] dp = new int[b.length() + 1];
        for (int i = 0; i < a.length(); i++) {
            int prev = 0;
            for (int j = 0; j < b.length(); j++) {
                int old = dp[j + 1];
                if (a.charAt(i) == b.charAt(j)) dp[j + 1] = prev + 1;
                else dp[j + 1] = Math.max(dp[j + 1], dp[j]);
                prev = old;
            }
        }
        return dp[b.length()];
    }

    /**
     * 最長共通部分列を1つ復元する。
     * @param a 1つ目の文字列
     * @param b 2つ目の文字列
     * @return 共通部分列の1つ（複数候補がある場合はそのうちの1つ）
     */
    static String one(String a, String b) {
        int n = a.length(), m = b.length();
        int[][] dp = new int[n + 1][m + 1];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                dp[i + 1][j + 1] = a.charAt(i) == b.charAt(j)
                    ? dp[i][j] + 1 : Math.max(dp[i][j + 1], dp[i + 1][j]);
            }
        }
        StringBuilder reverse = new StringBuilder();
        int i = n, j = m;
        while (i > 0 && j > 0) {
            if (a.charAt(i - 1) == b.charAt(j - 1)) {
                reverse.append(a.charAt(i - 1));
                i--;
                j--;
            } else if (dp[i - 1][j] >= dp[i][j - 1]) i--;
            else j--;
        }
        return reverse.reverse().toString();
    }

    /**
     * 任意のリストに対して最長共通部分列の長さを求める。
     * 要素はObjects.equalsで比較する。
     * @param a 1つ目のリスト
     * @param b 2つ目のリスト
     * @return LCSの長さ
     */
    static <T> int length(java.util.List<T> a, java.util.List<T> b) {
        if (a.size() < b.size()) { java.util.List<T> t = a; a = b; b = t; }
        int[] dp = new int[b.size() + 1];
        for (T x : a) {
            int prev = 0;
            for (int j = 0; j < b.size(); j++) {
                int old = dp[j + 1];
                if (java.util.Objects.equals(x, b.get(j))) dp[j + 1] = prev + 1;
                else dp[j + 1] = Math.max(dp[j + 1], dp[j]);
                prev = old;
            }
        }
        return dp[b.size()];
    }

    /**
     * 任意のリストの最長共通部分列を1つ復元する。
     * @param a 1つ目のリスト
     * @param b 2つ目のリスト
     * @return LCSの要素リスト
     */
    static <T> java.util.List<T> one(java.util.List<T> a, java.util.List<T> b) {
        int n = a.size(), m = b.size();
        int[][] dp = new int[n + 1][m + 1];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                dp[i + 1][j + 1] = java.util.Objects.equals(a.get(i), b.get(j))
                    ? dp[i][j] + 1 : Math.max(dp[i][j + 1], dp[i + 1][j]);
            }
        }
        java.util.ArrayList<T> reverse = new java.util.ArrayList<>();
        int i = n, j = m;
        while (i > 0 && j > 0) {
            if (java.util.Objects.equals(a.get(i - 1), b.get(j - 1))) {
                reverse.add(a.get(i - 1));
                i--;
                j--;
            } else if (dp[i - 1][j] >= dp[i][j - 1]) i--;
            else j--;
        }
        java.util.Collections.reverse(reverse);
        return reverse;
    }
}
