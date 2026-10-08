# Competitive Programming Library — Java

Java 17 向けの **import 不要・コピペ可能・汎用性重視** の競プロライブラリ。

## 設計方針
- 各ファイルは独立したクラス。自作ライブラリ間の依存なし
- 標準ライブラリは `java.util.List` のように完全修飾名で参照（import 不要）
- 汎用化に意味がある演算・値はジェネリクスを使う。頂点番号や添字などは `int` のまま
- 添字は 0-indexed、区間は `[l, r)`
- 提出時に必要なクラスだけ `Main` クラスの外側に貼り付ける

## Data Structures
| ファイル | 内容 |
| --- | --- |
| [DSU.java](data-structures/DSU.java) | Union-Find、経路圧縮・サイズ併合 |
| [FenwickTree.java](data-structures/FenwickTree.java) | `FenwickTree<T>`：可換群の点更新・区間集約 |
| [SegTree.java](data-structures/SegTree.java) | `SegTree<T>`：モノイドの点代入・区間集約 |

## Graph
| ファイル | 内容 |
| --- | --- |
| [BFS.java](graph/BFS.java) | 重みなしグラフの最短距離 |
| [Dijkstra.java](graph/Dijkstra.java) | long 重み、非負辺の最短距離 |

## Math
| ファイル | 内容 |
| --- | --- |
| [ModMath.java](math/ModMath.java) | mod 累乗・素数 mod の逆元 |

## 使い方

例えば和のセグメント木：

```java
SegTree<Long> seg = new SegTree<>(
    java.util.Arrays.asList(1L, 2L, 3L), 0L, Long::sum
);
System.out.println(seg.prod(0, 3)); // 6
```

文字列結合にも同じ実装を使えます（演算順を保持）：

```java
SegTree<String> seg = new SegTree<>(
    java.util.Arrays.asList("a", "b", "c"), "", String::concat
);
System.out.println(seg.prod(0, 3)); // abc
```

Fenwick Tree では**可換群**（結合法則・単位元・逆元があり可換）の演算を渡します：

```java
FenwickTree<Long> fw = new FenwickTree<>(5, 0L, Long::sum, x -> -x);
fw.add(0, 4L);
System.out.println(fw.sum(0, 1)); // 4
```

## 注意
- `SegTree<T>` は任意のモノイドに対応、`FenwickTree<T>` は逆元が必要な可換群に対応
- ジェネリクスは `long` ではなく `Long` のようなラッパー型を使用する
- BFS の到達不能は `-1`、Dijkstra の到達不能は `Dijkstra.INF`
- ModMath の逆元は素数 mod でのみ利用する

## テスト
JDK 17 以上でリポジトリのルートから実行：

```sh
javac data-structures/*.java graph/*.java math/*.java tests/LibraryTest.java
java -cp tests:data-structures:graph:math LibraryTest
```

Windows ではクラスパス区切り文字を `;` に変更してください。
