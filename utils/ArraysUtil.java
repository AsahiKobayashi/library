// Source: https://github.com/AsahiKobayashi/library/blob/main/utils/ArraysUtil.java
/**
 * 競技プログラミング向け配列ユーティリティ。
 * 0-indexedで、範囲を指定する場合は半開区間[l, r)を使う。
 * swap/reverse/sortDescendingは元配列を変更し、
 * sortedUnique/compress/prefixSumは元配列を変更しない。
 * importと外部クラスへの依存は不要。
 */
final class ArraysUtil {
    /** インスタンス化しない。 */
    private ArraysUtil() {}

    /** 配列の添字を交換する。 */
    static void swap(int[] a, int i, int j) {
        int x = a[i]; a[i] = a[j]; a[j] = x;
    }

    /** 配列の添字を交換する。 */
    static void swap(long[] a, int i, int j) {
        long x = a[i]; a[i] = a[j]; a[j] = x;
    }

    /** 配列の添字を交換する。 */
    static void swap(double[] a, int i, int j) {
        double x = a[i]; a[i] = a[j]; a[j] = x;
    }

    /** 配列の添字を交換する。 */
    static <T> void swap(T[] a, int i, int j) {
        T x = a[i]; a[i] = a[j]; a[j] = x;
    }

    /** int配列をその場で反転する。 */
    static void reverse(int[] a) { reverse(a, 0, a.length); }

    /** int配列の[l, r)だけをその場で反転する。 */
    static void reverse(int[] a, int l, int r) {
        checkRange(a.length, l, r);
        for (int i = l, j = r - 1; i < j; i++, j--) swap(a, i, j);
    }

    /** long配列をその場で反転する。 */
    static void reverse(long[] a) { reverse(a, 0, a.length); }

    /** long配列の[l, r)だけをその場で反転する。 */
    static void reverse(long[] a, int l, int r) {
        checkRange(a.length, l, r);
        for (int i = l, j = r - 1; i < j; i++, j--) swap(a, i, j);
    }

    /** double配列をその場で反転する。 */
    static void reverse(double[] a) { reverse(a, 0, a.length); }

    /** double配列の[l, r)だけをその場で反転する。 */
    static void reverse(double[] a, int l, int r) {
        checkRange(a.length, l, r);
        for (int i = l, j = r - 1; i < j; i++, j--) swap(a, i, j);
    }

    /** オブジェクト配列をその場で反転する。 */
    static <T> void reverse(T[] a) { reverse(a, 0, a.length); }

    /** オブジェクト配列の[l, r)だけをその場で反転する。 */
    static <T> void reverse(T[] a, int l, int r) {
        checkRange(a.length, l, r);
        for (int i = l, j = r - 1; i < j; i++, j--) swap(a, i, j);
    }

    /** int配列を降順ソートする（元配列を変更）。 */
    static void sortDescending(int[] a) {
        java.util.Arrays.sort(a);
        reverse(a);
    }

    /** long配列を降順ソートする（元配列を変更）。 */
    static void sortDescending(long[] a) {
        java.util.Arrays.sort(a);
        reverse(a);
    }

    /** double配列をDouble.compareの降順にソートする（元配列を変更）。 */
    static void sortDescending(double[] a) {
        java.util.Arrays.sort(a);
        reverse(a);
    }

    /** 自然順序の逆順でオブジェクト配列をソートする（元配列を変更）。 */
    static <T extends Comparable<? super T>> void sortDescending(T[] a) {
        java.util.Arrays.sort(a, java.util.Collections.reverseOrder());
    }

    /** 指定した比較器の逆順でソートする（元配列を変更）。 */
    static <T> void sortDescending(T[] a, java.util.Comparator<? super T> comparator) {
        java.util.Arrays.sort(a, java.util.Objects.requireNonNull(comparator).reversed());
    }

    /** 配列長と半開区間[l, r)の妥当性を検証する。 */
    private static void checkRange(int n, int l, int r) {
        if (l < 0 || l > r || r > n) throw new IndexOutOfBoundsException();
    }

    /** 昇順int配列でkey以上となる最初の添字を返す（なければlength）。O(log n)。 */
    static int lowerBound(int[] a, int key) {
        int l = 0, r = a.length;
        while (l < r) {
            int m = (l + r) >>> 1;
            if (a[m] < key) l = m + 1; else r = m;
        }
        return l;
    }

    /** 昇順int配列でkeyより大きい最初の添字を返す（なければlength）。O(log n)。 */
    static int upperBound(int[] a, int key) {
        int l = 0, r = a.length;
        while (l < r) {
            int m = (l + r) >>> 1;
            if (a[m] <= key) l = m + 1; else r = m;
        }
        return l;
    }

    /** 昇順long配列でkey以上となる最初の添字を返す。O(log n)。 */
    static int lowerBound(long[] a, long key) {
        int l = 0, r = a.length;
        while (l < r) {
            int m = (l + r) >>> 1;
            if (a[m] < key) l = m + 1; else r = m;
        }
        return l;
    }

    /** 昇順long配列でkeyより大きい最初の添字を返す。O(log n)。 */
    static int upperBound(long[] a, long key) {
        int l = 0, r = a.length;
        while (l < r) {
            int m = (l + r) >>> 1;
            if (a[m] <= key) l = m + 1; else r = m;
        }
        return l;
    }

    /** Double.compareで昇順のdouble配列からkey以上の最初の添字を返す。O(log n)。 */
    static int lowerBound(double[] a, double key) {
        int l = 0, r = a.length;
        while (l < r) {
            int m = (l + r) >>> 1;
            if (Double.compare(a[m], key) < 0) l = m + 1; else r = m;
        }
        return l;
    }

    /** Double.compareで昇順のdouble配列からkeyより大きい最初の添字を返す。O(log n)。 */
    static int upperBound(double[] a, double key) {
        int l = 0, r = a.length;
        while (l < r) {
            int m = (l + r) >>> 1;
            if (Double.compare(a[m], key) <= 0) l = m + 1; else r = m;
        }
        return l;
    }

    /** 自然順序の昇順配列でkey以上の最初の添字を返す。O(log n)。 */
    static <T extends Comparable<? super T>> int lowerBound(T[] a, T key) {
        return lowerBound(a, key, java.util.Comparator.naturalOrder());
    }

    /** 自然順序の昇順配列でkeyより大きい最初の添字を返す。O(log n)。 */
    static <T extends Comparable<? super T>> int upperBound(T[] a, T key) {
        return upperBound(a, key, java.util.Comparator.naturalOrder());
    }

    /** 指定比較器でソート済みの配列からkey以上の最初の添字を返す。O(log n)。 */
    static <T> int lowerBound(T[] a, T key, java.util.Comparator<? super T> comparator) {
        int l = 0, r = a.length;
        while (l < r) {
            int m = (l + r) >>> 1;
            if (comparator.compare(a[m], key) < 0) l = m + 1; else r = m;
        }
        return l;
    }

    /** 指定比較器でソート済みの配列からkeyより大きい最初の添字を返す。O(log n)。 */
    static <T> int upperBound(T[] a, T key, java.util.Comparator<? super T> comparator) {
        int l = 0, r = a.length;
        while (l < r) {
            int m = (l + r) >>> 1;
            if (comparator.compare(a[m], key) <= 0) l = m + 1; else r = m;
        }
        return l;
    }

    /** int配列を昇順ソートして重複を除いた新配列を返す。O(n log n)。 */
    static int[] sortedUnique(int[] a) {
        int[] b = a.clone();
        java.util.Arrays.sort(b);
        int len = 0;
        for (int x : b) if (len == 0 || b[len - 1] != x) b[len++] = x;
        return java.util.Arrays.copyOf(b, len);
    }

    /** long配列を昇順ソートして重複を除いた新配列を返す。O(n log n)。 */
    static long[] sortedUnique(long[] a) {
        long[] b = a.clone();
        java.util.Arrays.sort(b);
        int len = 0;
        for (long x : b) if (len == 0 || b[len - 1] != x) b[len++] = x;
        return java.util.Arrays.copyOf(b, len);
    }

    /** Double.compareの昇順でソートして重複を除いた新配列を返す。O(n log n)。 */
    static double[] sortedUnique(double[] a) {
        double[] b = a.clone();
        java.util.Arrays.sort(b);
        int len = 0;
        for (double x : b) if (len == 0 || Double.compare(b[len - 1], x) != 0) b[len++] = x;
        return java.util.Arrays.copyOf(b, len);
    }

    /** 自然順序でソートして重複を除いた新配列を返す。O(n log n)。 */
    static <T extends Comparable<? super T>> T[] sortedUnique(T[] a) {
        return sortedUnique(a, java.util.Comparator.naturalOrder());
    }

    /** 比較器の昇順でソートして重複を除いた新配列を返す。比較結果0なら同じ値。O(n log n)。 */
    static <T> T[] sortedUnique(T[] a, java.util.Comparator<? super T> comparator) {
        T[] b = a.clone();
        java.util.Arrays.sort(b, comparator);
        int len = 0;
        for (T x : b) if (len == 0 || comparator.compare(b[len - 1], x) != 0) b[len++] = x;
        return java.util.Arrays.copyOf(b, len);
    }

    /** int配列を元の順番のまま0始まりの座標に圧縮した新配列を返す。O(n log n)。 */
    static int[] compress(int[] a) {
        int[] keys = sortedUnique(a), ranks = new int[a.length];
        for (int i = 0; i < a.length; i++) ranks[i] = lowerBound(keys, a[i]);
        return ranks;
    }

    /** long配列を元の順番のまま0始まりの座標に圧縮した新配列を返す。O(n log n)。 */
    static int[] compress(long[] a) {
        long[] keys = sortedUnique(a);
        int[] ranks = new int[a.length];
        for (int i = 0; i < a.length; i++) ranks[i] = lowerBound(keys, a[i]);
        return ranks;
    }

    /** double配列をDouble.compareの順序で0始まりの座標に圧縮する。O(n log n)。 */
    static int[] compress(double[] a) {
        double[] keys = sortedUnique(a);
        int[] ranks = new int[a.length];
        for (int i = 0; i < a.length; i++) ranks[i] = lowerBound(keys, a[i]);
        return ranks;
    }

    /** 自然順序の0始まりの座標に圧縮する。O(n log n)。 */
    static <T extends Comparable<? super T>> int[] compress(T[] a) {
        return compress(a, java.util.Comparator.naturalOrder());
    }

    /** 比較器に従って0始まりの座標に圧縮する。比較結果0なら同じ座標。O(n log n)。 */
    static <T> int[] compress(T[] a, java.util.Comparator<? super T> comparator) {
        T[] keys = sortedUnique(a, comparator);
        int[] ranks = new int[a.length];
        for (int i = 0; i < a.length; i++) ranks[i] = lowerBound(keys, a[i], comparator);
        return ranks;
    }

    /** int配列の累積和を長さn+1のlong配列で返す。sum[l,r)=prefix[r]-prefix[l]。 */
    static long[] prefixSum(int[] a) {
        long[] prefix = new long[a.length + 1];
        for (int i = 0; i < a.length; i++) prefix[i + 1] = prefix[i] + a[i];
        return prefix;
    }

    /** long配列の累積和を長さn+1のlong配列で返す（longのオーバーフローに注意）。 */
    static long[] prefixSum(long[] a) {
        long[] prefix = new long[a.length + 1];
        for (int i = 0; i < a.length; i++) prefix[i + 1] = prefix[i] + a[i];
        return prefix;
    }

    /** double配列の累積和を長さn+1のdouble配列で返す。 */
    static double[] prefixSum(double[] a) {
        double[] prefix = new double[a.length + 1];
        for (int i = 0; i < a.length; i++) prefix[i + 1] = prefix[i] + a[i];
        return prefix;
    }

    /** 2次元int配列の各行を同じ値で埋める（元配列を変更）。 */
    static void fill(int[][] a, int value) {
        for (int[] row : a) java.util.Arrays.fill(row, value);
    }

    /** 2次元long配列の各行を同じ値で埋める（元配列を変更）。 */
    static void fill(long[][] a, long value) {
        for (long[] row : a) java.util.Arrays.fill(row, value);
    }

    /** 2次元double配列の各行を同じ値で埋める（元配列を変更）。 */
    static void fill(double[][] a, double value) {
        for (double[] row : a) java.util.Arrays.fill(row, value);
    }

    /** 2次元オブジェクト配列の各行を同じ参照で埋める（元配列を変更）。 */
    static <T> void fill(T[][] a, T value) {
        for (T[] row : a) java.util.Arrays.fill(row, value);
    }
}
