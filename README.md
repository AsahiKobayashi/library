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
| [FenwickTree](data-structures/FenwickTree.java) | `long` 点加算・区間和 | なし |
| [SegTree<T>](data-structures/SegTree.java) | 点代入・区間集約 | `Monoid<T>` |
| [LazySegTree<S,F>](data-structures/LazySegTree.java) | 点代入・区間更新・区間集約・境界探索 | `MonoidAction<S,F>` |

グラフ： [BFS](graph/BFS.java) / [Dijkstra](graph/Dijkstra.java)  
数学： [ModMath](math/ModMath.java)

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
javac -d out $(find monoid data-structures graph math tests -name '*.java')
java -cp out LibraryTest
java -cp out LazySegTreeTest
java -cp out IntegerLibraryTest
```

Windows PowerShell では次のようにコンパイルできます：

```powershell
$files = Get-ChildItem monoid,data-structures,graph,math,tests -Recurse -Filter *.java | ForEach-Object FullName
New-Item -ItemType Directory -Force out | Out-Null
javac -d out $files
java -cp out LibraryTest
java -cp out LazySegTreeTest
```

**注意**：ファイルの配置はカテゴリ分け用です。Javaのパッケージは使っていないため、単体コンパイル時は必要な依存クラスとともにコンパイルしてください。
