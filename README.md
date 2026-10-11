# Competitive Programming Library — Java

Java 17 向けの **import 不要・必要な箇所はジェネリクスで汎用化**した競プロライブラリです。各メソッドには日本語のJavadocを付けています。

## 設計方針
- 標準ライブラリは完全修飾名で参照し、`import` を書かない
- セグメント木の演算は `algebra/Monoid.java` に分離。Fenwick Tree は `long` の加算専用
- 必要なクラスを `Main` の外側に貼り付ける（パッケージ宣言なし）
- 0-indexed、区間は原則 `[l, r)`
- 使うデータ構造に応じて、必要なインターフェースもコピーする

## Algebra
| ファイル | 説明 |
| --- | --- |
| [Monoid.java](algebra/Monoid.java) | モノイド：単位元 `e()`、結合演算 `op(a,b)` |

## Data Structures
| ファイル | 説明 | 依存 |
| --- | --- | --- |
| [DSU.java](data-structures/DSU.java) | Union-Find | なし |
| [SegTree.java](data-structures/SegTree.java) | 任意のモノイドによる区間集約 | `Monoid.java` |
| [FenwickTree.java](data-structures/FenwickTree.java) | `long` の点加算・区間和 | なし |

## Graph
| ファイル | 説明 |
| --- | --- |
| [BFS.java](graph/BFS.java) | 重みなしグラフの最短距離 |
| [Dijkstra.java](graph/Dijkstra.java) | 非負重みグラフの最短距離 |

## Math
| ファイル | 説明 |
| --- | --- |
| [ModMath.java](math/ModMath.java) | mod累乗・素数modの逆元 |

## 使用例

まず、用途に応じた演算を定義します。

```java
Monoid<Long> sum = new Monoid<>() {
    public Long e() { return 0L; }
    public Long op(Long a, Long b) { return a + b; }
};
SegTree<Long> seg = new SegTree<>(
    java.util.Arrays.asList(1L, 2L, 3L), sum
);
System.out.println(seg.prod(0, 3)); // 6
```

`SegTree` を使うときは **`Monoid.java` と `SegTree.java` の両方**をコピーしてください。

Fenwick Tree は演算の定義なしで使えます。

```java
FenwickTree fw = new FenwickTree(5);
fw.add(0, 4L);
fw.add(2, -1L);
System.out.println(fw.sum(0, 3)); // 3
```

`FenwickTree.java` だけをコピーすれば利用できます。

`Monoid` は交換法則を必要としないので、文字列結合などにも利用できます。

## テスト

JDK 17以上でリポジトリのルートから：

```sh
javac algebra/*.java data-structures/*.java graph/*.java math/*.java tests/LibraryTest.java
java -cp tests:algebra:data-structures:graph:math LibraryTest
```

Windows ではクラスパスの区切りを `;` に変更してください。

**補足:** `SegTree<T>` は汎用型、`FenwickTree` は加算専用の `long` 型です。
