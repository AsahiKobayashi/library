# Competitive Programming Library — Java

Java 17 用の競技プログラミングライブラリ。**import不要・日本語Javadoc・ジェネリクス重視**。
モノイドや作用は**演算ごとにフォルダを分けた独立クラス**として管理しています。

## 方針

- 標準ライブラリは完全修飾名で参照し、`import` は書かない
- Javaの`package`宣言は置かない。必要なファイルの内容を `Main` の外側にコピーする
- 0-indexed、区間は原則 `[l, r)`（左端を含み、右端を含まない）
- 演算の定義はデータ構造とは別ファイルに置く
- `FenwickTree` はシンプルな `long` 加算・区間和専用とする

## ディレクトリ構成

```text
monoid/
├── interface/
│   ├── Monoid.java
│   ├── CommutativeMonoid.java
│   └── MonoidAction.java
└── template/
    ├── commutative/
    │   ├── sum/             # IntegerSumMonoid, LongSumMonoid
    │   ├── product/
    │   ├── min/
    │   ├── max/
    │   ├── gcd/
    │   ├── xor/
    │   ├── and/
    │   └── or/
    └── action/
        ├── common/          # SumLen, IntSumLen
        ├── range-add-sum/
        └── range-assign-sum/
```

`interface/` は演算の共通ルール、`template/` はその具体的な実装です。各Javaファイルのクラス名や実装は移動前と同じで、`import` も不要です。

## モノイドのインターフェース

| インターフェース | 用途 |
| --- | --- |
| [Monoid.java](monoid/interface/Monoid.java) | `e()`：単位元、`op(a,b)`：結合演算 |
| [CommutativeMonoid.java](monoid/interface/CommutativeMonoid.java) | 交換法則を満たす `Monoid`（逆元は不要） |
| [MonoidAction.java](monoid/interface/MonoidAction.java) | 遅延更新用の `id()`、`mapping(f,x)`、`composition(f,g)`（先にg、次にf） |

## 定番モノイド

すべて独立ファイルなので、使いたい演算だけコピーできます。

### 可換モノイド

同じ演算フォルダに `Integer` 版と `Long` 版を置いています。

| 演算 | Integer版 | Long版 | 単位元 |
| --- | --- | --- | --- |
| 加算 | [IntegerSumMonoid](monoid/template/commutative/sum/IntegerSumMonoid.java) | [LongSumMonoid](monoid/template/commutative/sum/LongSumMonoid.java) | `0` / `0L` |
| 乗算 | [IntegerProductMonoid](monoid/template/commutative/product/IntegerProductMonoid.java) | [LongProductMonoid](monoid/template/commutative/product/LongProductMonoid.java) | `1` / `1L` |
| 最小値 | [IntegerMinMonoid](monoid/template/commutative/min/IntegerMinMonoid.java) | [LongMinMonoid](monoid/template/commutative/min/LongMinMonoid.java) | 各型の最大値 |
| 最大値 | [IntegerMaxMonoid](monoid/template/commutative/max/IntegerMaxMonoid.java) | [LongMaxMonoid](monoid/template/commutative/max/LongMaxMonoid.java) | 各型の最小値 |
| 最大公約数（非負整数） | [IntegerGcdMonoid](monoid/template/commutative/gcd/IntegerGcdMonoid.java) | [LongGcdMonoid](monoid/template/commutative/gcd/LongGcdMonoid.java) | `0` / `0L` |
| XOR | [IntegerXorMonoid](monoid/template/commutative/xor/IntegerXorMonoid.java) | [LongXorMonoid](monoid/template/commutative/xor/LongXorMonoid.java) | `0` / `0L` |
| ビットAND | [IntegerAndMonoid](monoid/template/commutative/and/IntegerAndMonoid.java) | — | `-1` |
| ビットOR | [IntegerOrMonoid](monoid/template/commutative/or/IntegerOrMonoid.java) | — | `0` |
| 論理AND | — | [BooleanAndMonoid](monoid/template/commutative/and/BooleanAndMonoid.java) | `true` |
| 論理OR | — | [BooleanOrMonoid](monoid/template/commutative/or/BooleanOrMonoid.java) | `false` |

## モノイド作用（Lazy Segment Tree）

| 演算 | Integer版 | Long版 |
| --- | --- | --- |
| 区間加算・区間和 | [IntRangeAddSumAction](monoid/template/action/range-add-sum/IntRangeAddSumAction.java) | [RangeAddSumAction](monoid/template/action/range-add-sum/RangeAddSumAction.java) |
| 区間代入・区間和 | [IntRangeAssignSumAction](monoid/template/action/range-assign-sum/IntRangeAssignSumAction.java) | [RangeAssignSumAction](monoid/template/action/range-assign-sum/RangeAssignSumAction.java) |

区間和と要素数は、`Integer` 用の [IntSumLen](monoid/template/action/common/IntSumLen.java) または `long` 用の [SumLen](monoid/template/action/common/SumLen.java) で管理します。区間代入の更新値 `null` は「更新なし」です。

## データ構造

| 実装 | 内容 | 必要な演算インターフェース |
| --- | --- | --- |
| [DSU](data-structures/DSU.java) | Union-Find | なし |
| [BinaryTrie](data-structures/BinaryTrie.java) | 重複する非負整数の管理・XOR最小最大・順位取得・一括XOR | なし |
| [AccessibleDeque<T>](data-structures/AccessibleDeque.java) | 両端操作＋任意の添字へのO(1)アクセス | なし |
| [Multiset<T>](data-structures/Multiset.java) | HashMap型の汎用多重集合（重複を保持） | なし |
| [SortedMultiset<T>](data-structures/SortedMultiset.java) | AVL木による整列多重集合・順位・k番目 | なし |
| [FenwickTree](data-structures/FenwickTree.java) | `long` 点加算・区間和 | なし |
| [SegTree<T>](data-structures/SegTree.java) | 点代入・区間集約 | `Monoid<T>` |
| [LazySegTree<S,F>](data-structures/LazySegTree.java) | 点代入・区間更新・区間集約・境界探索 | `MonoidAction<S,F>` |

グラフ： [BFS](graph/BFS.java) / [Dijkstra](graph/Dijkstra.java)  
数学： [ModMath](math/ModMath.java)

## Multiset・SortedMultiset

どちらも **ジェネリクス対応**で重複を保持し、`import` や外部インターフェースは必要ありません。

### Multiset<T>（順序なし）

```java
Multiset<String> bag = new Multiset<>();
bag.add("apple");
bag.add("apple", 2);  // 2個追加
bag.add("orange");

System.out.println(bag.count("apple")); // 3
System.out.println(bag.size());         // 4（重複込み）
bag.remove("apple");                    // 1個削除
System.out.println(bag.distinctSize()); // 2
```

`count`、`contains`、`add`、`remove`、`removeAll`、`elementSet`、`clear`、重複を含む拡張for文に対応。**平均O(1)** の追加・削除・個数取得です。要素比較には `equals` と `hashCode` を使い、`null` も格納できます。要素数・個数の戻り値は `long` です。

### SortedMultiset<T>（順序あり）

```java
SortedMultiset<Integer> sorted = new SortedMultiset<>();
sorted.add(5);
sorted.add(2);
sorted.add(5);
sorted.add(9);

System.out.println(sorted.kth(2));            // 5（0-indexed）
System.out.println(sorted.countLessThan(5));  // 1
System.out.println(sorted.floor(7));          // 5
System.out.println(sorted.ceiling(7));        // 9
System.out.println(sorted.first());           // 2

// 降順の比較器も指定できる
SortedMultiset<Integer> reverse =
    new SortedMultiset<>(java.util.Comparator.reverseOrder());
reverse.add(1);
reverse.add(3);
System.out.println(reverse.kth(0)); // 3
```

`kth`、`countLessThan`、`countLessThanOrEqual`、`lower`、`floor`、`ceiling`、`higher`、`first`、`last`、`pollFirst`、`pollLast` などに対応。**AVL木**を使い、追加・削除・順位検索・k番目取得は最悪 `O(log U)`（Uは異なる要素の種類数）。反復は昇順で重複も含みます。

自然順序（`Comparable`）のほか、`Comparator<? super T>` を指定できます。**比較結果が0の値は同じ種類**とみなします。要素の `null` は不可。見つからない `floor` 等は `null` を返し、空集合の `first` / `last` は例外になります。

複数個の追加は `add(value, amount)`、複数個の削除は `remove(value, amount)`（実際の削除個数を返す）。どちらも1ファイルのみで動作します。

## BinaryTrie

`BinaryTrie` は重複を許す非負整数の多重集合。ビット数は1～63で指定でき、操作は基本的に **O(bitWidth)**、全要素XORは **O(1)** です。

```java
BinaryTrie trie = new BinaryTrie(31); // [0, 2^31) の整数
trie.add(5);
trie.add(5);
trie.add(10);

System.out.println(trie.count(5)); // 2
System.out.println(trie.kth(1));  // 5（0-indexed、重複含む）
System.out.println(trie.minXor(6)); // 3 = 5 xor 6
trie.xorAll(3);                    // 全要素をXORで更新
trie.erase(6);                    // 更新後の現在値から1個削除
```

`minXor(x)` / `maxXor(x)` は **XOR結果**、`argMinXor(x)` / `argMaxXor(x)` は **その結果を与える要素** を返します。
`countLessThan(x)`、`lowerBound(x)`、`min()`、`max()`、`clear()` も使用できます。空集合からの値取得は例外です。

引数は非負整数（63ビットなら `0` ～ `Long.MAX_VALUE`）。負数や設定ビット数を超えた値は扱いません。値の型は `long` なので `int` からも渡せます。

**必要なファイルは `BinaryTrie.java` のみ。**

## AccessibleDeque

`AccessibleDeque<T>` は `get(i)` / `set(i, x)` が **O(1)** のDeque。
リングバッファ方式で、両端の追加・削除も **償却O(1)** です。

```java
AccessibleDeque<Integer> deque = new AccessibleDeque<>();
deque.pushBack(20);
deque.pushFront(10);
deque.pushBack(30);
System.out.println(deque.get(1)); // 20
deque.set(1, 99);
System.out.println(deque.popFront()); // 10
System.out.println(deque.peekLast()); // 30
```

`addFirst` / `addLast`、`removeFirst` / `removeLast`、`peekFirst` / `peekLast` にも対応。
空Dequeからの削除は例外、`peekFirst` / `peekLast` は `null` を返します。**格納する要素の `null` は不可**。
`Iterable<T>` に対応しているため拡張for文も使えます。

**必要なファイルは `AccessibleDeque.java` のみ。**

## 文字列アルゴリズム

| ファイル | 説明 |
| --- | --- |
| [RollingHash.java](string/rolling-hash/RollingHash.java) | 2つの素数法で区間ハッシュ、結合、繰り返し、区間一括加算に対応 |
| [LongestCommonSubsequence.java](string/lcs/LongestCommonSubsequence.java) | **LCS：最長共通部分列**（非連続でもよい）。長さ・復元・ジェネリックなリスト版 |
| [LongestCommonSubstring.java](string/lcs/LongestCommonSubstring.java) | **最長共通部分文字列**（連続区間）。Rolling Hashで長さと位置を探索、文字列の直接照合も実施 |

### Rolling Hash（変更なしの文字列）

```java
RollingHash a = new RollingHash("abracadabra");
RollingHash b = new RollingHash("cad");
System.out.println(a.same(4, 7, b, 0, 3)); // true
System.out.println(a.lcp(4, b, 0));         // 3
```

`RollingHash.java` のみ必要です。文字列はJavaのUTF-16 `char` 単位で扱い、`char + 1` をハッシュ化します。ハッシュ衝突の可能性はゼロではありません。

### SegTreeと組み合わせる（点更新）

```java
String s = "abcd";
RollingHash rh = new RollingHash(s.length());
java.util.ArrayList<RollingHash.Hash> chars = new java.util.ArrayList<>();
for (int i = 0; i < s.length(); i++) chars.add(rh.ofChar(s.charAt(i)));

SegTree<RollingHash.Hash> seg = new SegTree<>(chars, new RollingHashMonoid(rh));
seg.set(1, rh.ofChar('x'));
System.out.println(seg.prod(0, 4).equals(new RollingHash("axcd").hash(0, 4))); // true
```

必要：`RollingHash.java`、`RollingHashMonoid.java`、`Monoid.java`、`SegTree.java`。

### LazySegTreeと組み合わせる（区間代入）

```java
String s = "abcd";
RollingHash rh = new RollingHash(s.length());
java.util.ArrayList<RollingHash.Hash> chars = new java.util.ArrayList<>();
for (int i = 0; i < s.length(); i++) chars.add(rh.ofChar(s.charAt(i)));

LazySegTree<RollingHash.Hash, Long> lazy =
    new LazySegTree<>(chars, new RangeAssignHashAction(rh));
lazy.apply(1, 3, (long) 'Z' + 1); // "aZZd"：文字の場合はchar + 1
System.out.println(lazy.allProd().equals(new RollingHash("aZZd").hash(0, 4))); // true
```

必要：`RollingHash.java`、`RangeAssignHashAction.java`、`Monoid.java`、`MonoidAction.java`、`LazySegTree.java`。

整数列の区間加算には `RangeAddHashAction.java` を使えます。更新値は `Long` で、更新前後の値が整数オーバーフローしない用途を想定しています。いずれの更新でも **RollingHashの最大長は配列長以上** にしてください。動的なハッシュは `LazySegTree` の区間集約値 `RollingHash.Hash` として扱います。

### LCS（最長共通部分列）と最長共通部分文字列

```java
System.out.println(LongestCommonSubsequence.length("ABCBDAB", "BDCABA")); // 4
System.out.println(LongestCommonSubsequence.one("abcde", "ace"));          // ace
System.out.println(LongestCommonSubstring.one("abcXYZdef", "123XYZ456")); // XYZ
```

`LongestCommonSubsequence.java` は単独で利用できます。`LongestCommonSubstring.java` は `RollingHash.java` を併せてコピーしてください。**部分列は飛び飛びでもOK、部分文字列は連続が必須**という違いがあります。

## 使用例

### モノイドによるセグメント木

```java
SegTree<Long> seg = new SegTree<>(
    java.util.Arrays.asList(4L, 1L, 7L),
    new LongMinMonoid()
);
System.out.println(seg.prod(0, 3)); // 1
```

必要なファイル：`Monoid.java`、`CommutativeMonoid.java`、`LongMinMonoid.java`、`SegTree.java`。

### Integerで使う場合

```java
SegTree<Integer> seg = new SegTree<>(
    java.util.Arrays.asList(1, 2, 3),
    new IntegerSumMonoid()
);
System.out.println(seg.prod(0, 3)); // 6
```

必要なファイル：`Monoid.java`、`CommutativeMonoid.java`、`IntegerSumMonoid.java`、`SegTree.java`。

```java
LazySegTree<IntSumLen, Integer> lazy = new LazySegTree<>(
    java.util.Arrays.asList(
        IntSumLen.leaf(1), IntSumLen.leaf(2), IntSumLen.leaf(3)
    ),
    new IntRangeAddSumAction()
);
lazy.apply(0, 2, 4);
System.out.println(lazy.prod(0, 3).sum); // 14
```

必要なファイル：`Monoid.java`、`MonoidAction.java`、`IntSumLen.java`、`IntRangeAddSumAction.java`、`LazySegTree.java`。区間代入なら `IntRangeAssignSumAction.java` に変更してください。

**注意：`Integer` の和・積・区間和は `int` の上限・下限を超えるとオーバーフローします。** 和や積が大きくなりそうな問題では、従来の `Long` 版を使ってください。

### 区間加算・区間和の遅延セグメント木

```java
LazySegTree<SumLen, Long> lazy = new LazySegTree<>(
    java.util.Arrays.asList(
        SumLen.leaf(1), SumLen.leaf(2), SumLen.leaf(3)
    ),
    new RangeAddSumAction()
);
lazy.apply(0, 2, 4L);
System.out.println(lazy.prod(0, 3).sum); // 14
```

必要なファイル：`Monoid.java`、`MonoidAction.java`、`SumLen.java`、`RangeAddSumAction.java`、`LazySegTree.java`。
区間代入なら `RangeAssignSumAction.java` に置き換えます。いずれも `import` 不要です。

### Fenwick Tree は単体で利用

```java
FenwickTree fw = new FenwickTree(5);
fw.add(0, 4L);
fw.add(2, -1L);
System.out.println(fw.sum(0, 3)); // 3
```

## テスト

JDK 17 以上。Linux/macOS のリポジトリルートから実行：

```sh
mkdir -p out
javac -d out $(find monoid data-structures graph math string tests -name '*.java')
java -cp out LibraryTest
java -cp out LazySegTreeTest
java -cp out IntegerLibraryTest
java -cp out RollingHashTest
java -cp out LCSTest
java -cp out BinaryTrieTest
java -cp out AccessibleDequeTest
java -cp out MultisetTest
```

Windows PowerShell では次のようにコンパイルできます：

```powershell
$files = Get-ChildItem monoid,data-structures,graph,math,string,tests -Recurse -Filter *.java | ForEach-Object FullName
New-Item -ItemType Directory -Force out | Out-Null
javac -d out $files
java -cp out LibraryTest
java -cp out LazySegTreeTest
```

**注意**：ファイルの配置はカテゴリ分け用です。Javaのパッケージは使っていないため、単体コンパイル時は必要な依存クラスとともにコンパイルしてください。
