// Source: https://github.com/AsahiKobayashi/library/blob/main/tests/ArraysUtilTest.java
/** ArraysUtilの境界条件・ジェネリクス・ランダムケースを検証する。 */
class ArraysUtilTest {
    /** 期待値と実際の値が等しいことを確認する。 */
    static void eq(Object expected, Object actual) {
        if (!java.util.Objects.equals(expected, actual))
            throw new AssertionError("expected=" + expected + ", actual=" + actual);
    }

    /** int配列が一致することを確認する。 */
    static void ints(int[] a, int... expected) {
        if (!java.util.Arrays.equals(expected, a))
            throw new AssertionError(java.util.Arrays.toString(a) + " != " + java.util.Arrays.toString(expected));
    }

    /** long配列が一致することを確認する。 */
    static void longs(long[] a, long... expected) {
        if (!java.util.Arrays.equals(expected, a))
            throw new AssertionError(java.util.Arrays.toString(a) + " != " + java.util.Arrays.toString(expected));
    }

    /** double配列が一致することを確認する。 */
    static void doubles(double[] a, double... expected) {
        if (!java.util.Arrays.equals(expected, a))
            throw new AssertionError(java.util.Arrays.toString(a) + " != " + java.util.Arrays.toString(expected));
    }

    /** 基本操作と境界条件を検証する。 */
    static void basic() {
        int[] a = {5, 1, 1, 3, -9};
        ArraysUtil.swap(a, 0, 4);
        ints(a, -9, 1, 1, 3, 5);
        ArraysUtil.reverse(a, 1, 4);
        ints(a, -9, 3, 1, 1, 5);
        ArraysUtil.reverse(a);
        ints(a, 5, 1, 1, 3, -9);
        ArraysUtil.sortDescending(a);
        ints(a, 5, 3, 1, 1, -9);
        ints(ArraysUtil.sortedUnique(a), -9, 1, 3, 5);
        ints(ArraysUtil.compress(a), 3, 2, 1, 1, 0);
        longs(ArraysUtil.prefixSum(a), 0, 5, 8, 9, 10, 1);

        int[] b = {Integer.MIN_VALUE, 0, 0, 8, Integer.MAX_VALUE};
        eq(1, ArraysUtil.lowerBound(b, 0));
        eq(3, ArraysUtil.upperBound(b, 0));
        eq(0, ArraysUtil.lowerBound(b, Integer.MIN_VALUE));
        eq(5, ArraysUtil.upperBound(b, Integer.MAX_VALUE));
        eq(0, ArraysUtil.lowerBound(new int[]{}, 4));
        ints(ArraysUtil.sortedUnique(new int[]{}));
        ints(ArraysUtil.compress(new int[]{}));
        longs(ArraysUtil.prefixSum(new int[]{}), 0);
        int[] empty = {};
        ArraysUtil.reverse(empty);
        ArraysUtil.sortDescending(empty);

        int[][] matrix = {{1, 2}, {}, {9}};
        ArraysUtil.fill(matrix, 4);
        ints(matrix[0], 4, 4);
        ints(matrix[1]);
        ints(matrix[2], 4);
        long[][] matrixLong = {{1, 2}, {7}};
        ArraysUtil.fill(matrixLong, -1);
        longs(matrixLong[0], -1, -1);
        longs(matrixLong[1], -1);
        double[][] matrixDouble = {{5.5}, {3.3}};
        ArraysUtil.fill(matrixDouble, 2.5);
        doubles(matrixDouble[0], 2.5);
        String[][] matrixText = {{"a", "b"}, {"c"}};
        ArraysUtil.fill(matrixText, "z");
        eq("z", matrixText[0][0]);
        eq("z", matrixText[1][0]);

        long[] numbers = {-10L, Long.MAX_VALUE, 7L, 7L, Long.MIN_VALUE};
        ArraysUtil.swap(numbers, 0, 4);
        ArraysUtil.reverse(numbers, 1, 4);
        ArraysUtil.sortDescending(numbers);
        longs(numbers, Long.MAX_VALUE, 7, 7, -10, Long.MIN_VALUE);
        longs(ArraysUtil.sortedUnique(numbers), Long.MIN_VALUE, -10, 7, Long.MAX_VALUE);
        ints(ArraysUtil.compress(numbers), 3, 2, 2, 1, 0);
        eq(0, ArraysUtil.lowerBound(new long[]{-9, -1, 4}, -10));
        eq(3, ArraysUtil.upperBound(new long[]{-9, -1, 4}, 4));
        longs(ArraysUtil.prefixSum(new long[]{3, -5, 4}), 0, 3, -2, 2);

        double[] d = {Double.NaN, -0.0, 0.0, 3.5, Double.NEGATIVE_INFINITY, Double.NaN};
        ArraysUtil.sortDescending(d);
        doubles(d, Double.NaN, Double.NaN, 3.5, 0.0, -0.0, Double.NEGATIVE_INFINITY);
        doubles(ArraysUtil.sortedUnique(d), Double.NEGATIVE_INFINITY, -0.0, 0.0, 3.5, Double.NaN);
        ints(ArraysUtil.compress(d), 4, 4, 3, 2, 1, 0);
        eq(1, ArraysUtil.lowerBound(new double[]{-0.0, 0.0, Double.NaN}, 0.0));
        eq(2, ArraysUtil.upperBound(new double[]{-0.0, 0.0, Double.NaN}, 0.0));
        doubles(ArraysUtil.prefixSum(new double[]{1.5, -2.5, 3}), 0, 1.5, -1, 2);
        ArraysUtil.reverse(d, 0, 2);
        ArraysUtil.swap(d, 0, 1);

        String[] s = {"bbb", "a", "cc", "a"};
        ArraysUtil.swap(s, 0, 1);
        ArraysUtil.reverse(s, 1, 3);
        ArraysUtil.sortDescending(s);
        eq(java.util.Arrays.asList("cc", "bbb", "a", "a"), java.util.Arrays.asList(s));
        eq(java.util.Arrays.asList("a", "bbb", "cc"),
           java.util.Arrays.asList(ArraysUtil.sortedUnique(s)));
        eq(1, ArraysUtil.lowerBound(new String[]{"a", "bb", "ccc"}, "bb"));
        eq(3, ArraysUtil.upperBound(new String[]{"a", "bb", "bb", "ccc"}, "bb"));

        java.util.Comparator<String> byLength =
            java.util.Comparator.comparingInt(String::length);
        String[] words = {"a", "XX", "b", "YYY", "zz"};
        eq(java.util.Arrays.asList("a", "XX", "YYY"),
           java.util.Arrays.asList(ArraysUtil.sortedUnique(words, byLength)));
        ints(ArraysUtil.compress(words, byLength), 0, 1, 0, 2, 1);
        eq(1, ArraysUtil.lowerBound(new String[]{"a", "bb", "ccc"}, "xx", byLength));
        eq(2, ArraysUtil.upperBound(new String[]{"a", "bb", "ccc"}, "xx", byLength));
        ArraysUtil.sortDescending(words, byLength);
        eq(3, words[0].length());
    }

    /** ランダム配列を単純なソート・検索・反転と比較する。 */
    static void random() {
        java.util.Random random = new java.util.Random(20261015);
        for (int n = 0; n <= 80; n++) for (int repeat = 0; repeat < 70; repeat++) {
            int[] a = new int[n];
            long[] b = new long[n];
            for (int i = 0; i < n; i++) {
                a[i] = random.nextInt(21) - 10;
                b[i] = a[i];
            }
            int[] original = a.clone(), sorted = a.clone();
            java.util.Arrays.sort(sorted);
            int[] unique = ArraysUtil.sortedUnique(a), ranks = ArraysUtil.compress(a);
            long[] prefix = ArraysUtil.prefixSum(a);
            ints(a, original);
            for (int i = 0; i < n; i++) {
                eq(ArraysUtil.lowerBound(unique, a[i]), ranks[i]);
                eq(prefix[i] + a[i], prefix[i + 1]);
            }
            long[] longSorted = b.clone();
            java.util.Arrays.sort(longSorted);
            for (int x = -12; x <= 12; x++) {
                int lower = 0, upper = 0;
                for (int y : a) {
                    if (y < x) lower++;
                    if (y <= x) upper++;
                }
                eq(lower, ArraysUtil.lowerBound(sorted, x));
                eq(upper, ArraysUtil.upperBound(sorted, x));
                eq(lower, ArraysUtil.lowerBound(longSorted, x));
                eq(upper, ArraysUtil.upperBound(longSorted, x));
            }
            java.util.Arrays.sort(a);
            ArraysUtil.reverse(a);
            for (int i = 0; i < n; i++) eq(sorted[n - 1 - i], a[i]);
            int l = random.nextInt(n + 1), r = random.nextInt(n + 1);
            if (l > r) { int temp = l; l = r; r = temp; }
            int[] expected = a.clone();
            for (int i = l, j = r - 1; i < j; i++, j--) {
                int temp = expected[i]; expected[i] = expected[j]; expected[j] = temp;
            }
            ArraysUtil.reverse(a, l, r);
            ints(a, expected);
        }
    }

    /** すべてのテストを実行する。 */
    public static void main(String[] args) {
        basic();
        random();
        System.out.println("ArraysUtil tests passed");
    }
}
