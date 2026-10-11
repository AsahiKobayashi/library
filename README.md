# Competitive Programming Library — Java

Java 17 用の競技プログラミングライブラリ。**import不要・日本語Javadoc・ジェネリクス重視**。
モノイドや作用は**演算ごとにフォルダを分けた独立クラス**として管理しています。

## 方針

- 標準ライブラリは完全修飾名で参照し、`import` は書かない
- Javaの`package`宣言は置かない。必要なファイルの内容を `Main` の外側にコピーする
- 0-indexed、区間は原則 `[l, r)`（左端を含み、右端を含まない）
- 演算の定義はデータ構造とは別ファイルに置く
- `FenwickTree` はシンプルな `long` 加算・区間和専用とする

## モノイドのインターフェース

| インターフェース | 用途 |
| --- | --- |
| [Monoid.java](algebra/Monoid.java) | `e()`：単位元、`op(a,b)`：結合演算 |
| [CommutativeMonoid.java](algebra/CommutativeMonoid.java) | 交換法則を満たす `Monoid`（逆元は不要） |
| [MonoidAction.java](algebra/MonoidAction.java) | 遅延更新用の `id()`、`mapping(f,x)`、`composition(f,g)`（先にg、次にf） |

## 定番モノイド

すべて独立ファイルなので、使いたい演算だけコピーできます。

### 可換モノイド

| 演算 | 実装 | 単位元 |
| --- | --- | --- |
| 加算 | [LongSumMonoid](algebra/monoids/commutative/sum/LongSumMonoid.java) | `0L` |
| 乗算 | [LongProductMonoid](algebra/monoids/commutative/product/LongProductMonoid.java) | `1L` |
| 最小値 | [LongMinMonoid](algebra/monoids/commutative/min/LongMinMonoid.java) | `Long.MAX_VALUE` |
| 最大値 | [LongMaxMonoid](algebra/monoids/commutative/max/LongMaxMonoid.java) | `Long.MIN_VALUE` |
| 最大公約数（非負整数） | [LongGcdMonoid](algebra/monoids/commutative/gcd/LongGcdMonoid.java) | `0L` |
| XOR | [LongXorMonoid](algebra/monoids/commutative/xor/LongXorMonoid.java) | `0L` |
| 論理AND | [BooleanAndMonoid](algebra/monoids/commutative/and/BooleanAndMonoid.java) | `true` |
| 論理OR | [BooleanOrMonoid](algebra/monoids/commutative/or/BooleanOrMonoid.java) | `false` |

### 非可換モノイド

| 演算 | 実装 | 単位元 |
| --- | --- | --- |
| 文字列連結 | [StringConcatMonoid](algebra/monoids/noncommutative/concat/StringConcatMonoid.java) | 空文字列 |

## モノイド作用（Lazy Segment Tree）

| 演算 | 実装 | 更新の型 |
| --- | --- | --- |
| 区間加算・区間和 | [RangeAddSumAction](algebra/actions/range-add-sum/RangeAddSumAction.java) | `Long`（加算量） |
| 区間代入・区間和 | [RangeAssignSumAction](algebra/actions/range-assign-sum/RangeAssignSumAction.java) | `Long`（代入値） |

どちらも集約値に [SumLen](algebra/actions/common/SumLen.java)（`sum` と `len`）を使います。
`RangeAssignSumAction` の更新値 `null` は「更新なし」です。

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
文字列連結なら `Monoid.java`、`StringConcatMonoid.java`、`SegTree.java` を使います。

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
javac -d out $(find algebra data-structures graph math tests -name '*.java')
java -cp out LibraryTest
java -cp out LazySegTreeTest
```

Windows PowerShell では次のようにコンパイルできます：

```powershell
$files = Get-ChildItem algebra,data-structures,graph,math,tests -Recurse -Filter *.java | ForEach-Object FullName
javac -d out $files
java -cp out LibraryTest
java -cp out LazySegTreeTest
```

**注意**：ファイルの配置はカテゴリ分け用です。Javaのパッケージは使っていないため、単体コンパイル時は必要な依存クラスとともにコンパイルしてください。
