# Competitive Programming Library — Java

競技プログラミングで**そのまま貼り付ける**ための Java コード集。

## 方針
- Java 17
- **import 不要**（必要な標準ライブラリは `java.util.*` のような完全修飾名で参照）
- 各ファイルは package 宣言なし・他のライブラリファイルへの依存なし
- `Main.java` に必要なクラスだけコピーして使う
- 添字は 0-indexed、区間は原則 `[l, r)`
- 読みやすさよりも短く使いやすい実装を優先。ただし境界条件を省かない

## Data Structures
| ファイル | 内容 |
| --- | --- |
| [DSU.java](data-structures/DSU.java) | Union-Find：経路圧縮・サイズ併合 |
| [FenwickTree.java](data-structures/FenwickTree.java) | long の点加算・区間和 |
| [SegTree.java](data-structures/SegTree.java) | long の点代入・区間和 |

## Graph
| ファイル | 内容 |
| --- | --- |
| [BFS.java](graph/BFS.java) | 重みなしグラフの最短距離 |
| [Dijkstra.java](graph/Dijkstra.java) | 非負重み付きグラフの最短距離 |

## Math
| ファイル | 内容 |
| --- | --- |
| [ModMath.java](math/ModMath.java) | mod 累乗・逆元（素数 mod） |

## 使い方

提出する `Main.java` の `Main` クラスの**外側**に必要な実装を貼り付けます。ほかのファイルの取り込みや import は不要です。

例：

```java
public class Main {
    public static void main(String[] args) {
        DSU uf = new DSU(4);
        uf.merge(0, 1);
        System.out.println(uf.same(0, 1)); // true
    }
}

// ここに DSU.java の内容を貼り付ける
```

## 注意
- `FenwickTree` の `sum(r)` は `[0,r)`、`sum(l,r)` は `[l,r)`
- `SegTree` は現時点で **long の区間和専用**
- `BFS.dist` の到達不能は `-1`
- `Dijkstra.dist` の到達不能は `Dijkstra.INF`。すべての辺重みは非負、距離は INF 未満で使用
- `ModMath.inv` は mod が素数かつ a が mod の倍数でない場合のみ

## テスト

JDK 17 以上でリポジトリのルートから：

```sh
javac data-structures/*.java graph/*.java math/*.java tests/LibraryTest.java
java -cp tests:data-structures:graph:math LibraryTest
```

Windows のクラスパス区切り文字は `:` ではなく `;` を使います。

旧ファイルは現行ツリーから除きました。過去の Git 履歴に残っています。
