# 競技プログラミング向けライブラリ

Javaで実装した、競技プログラミング用のデータ構造・代数的インターフェース集です。

## ライブラリ一覧

### データ構造

| 種類 | 実装 | 主な用途 |
| --- | --- | --- |
| セグメント木 | [SegmentTree](data_structures/segment_tree/SegmentTree.java) | 点更新・区間集約 |
| 遅延セグメント木 | [LazySegtree](data_structures/segment_tree/LazySegmenetTree.java) | 区間更新・区間集約 |
| 2次元セグメント木 | [SegmentTree2D](data_structures/segment_tree/SegmentTree2D.java) | 2次元の範囲集約 |
| Union-Find | [UnionFindTree](data_structures/union_find_tree/UnionFindTree.java) | 連結成分の管理 |
| Binary Trie | [BinaryTrie](data_structures/trie/BinaryTrie.java) | 整数の集合・順序統計 |

### 共通インターフェース

| 名前 | 用途 |
| --- | --- |
| [Monoid](data/monoid/Monoid.java) | 単位元と二項演算の定義 |
| [MonoidAction](data/monoid/MonoidAction.java) | 遅延評価用の演算・作用の定義 |

## 使用方法

必要な実装を競技プログラミングの提出コードにコピーして使用してください。

- セグメント木と2次元セグメント木では `Monoid` を併用します。
- 遅延セグメント木では `MonoidAction` を併用します。
- ソースのクラスは基本的にパッケージ宣言のない構成です。
- 提出先の仕様に合わせてクラス名や可視性を調整してください。

## ディレクトリ構成

```text
.
├── data/
│   └── monoid/
│       ├── Monoid.java
│       └── MonoidAction.java
├── data_structures/
│   ├── segment_tree/
│   │   ├── SegmentTree.java
│   │   ├── SegmentTree2D.java
│   │   └── LazySegmenetTree.java
│   ├── trie/
│   │   └── BinaryTrie.java
│   └── union_find_tree/
│       └── UnionFindTree.java
└── README.md
```

## メモ

各実装は個別のコード片として管理しています。利用前に、提出環境でのコンパイル・動作確認を行ってください。

特に `LazySegmenetTree.java` のファイル名と `LazySegtree` のクラス名は異なります。ファイル名・APIの一括変更は既存コードへの影響を避けるため行っていません。
