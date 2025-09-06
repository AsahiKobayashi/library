class BinaryTrie {

    private static class Node {
        int[] child = {-1, -1};
        int count = 0;
    }

    private final List<Node> nodes = new ArrayList<>();
    private final int BITLEN;

    public BinaryTrie(int bitlen) {
        this.BITLEN = bitlen;
        nodes.add(new Node());
    }

    public int size() {
        return nodes.get(0).count;
    }

    public void add(int x) {
        int p = 0;
        for (int i = BITLEN - 1; i >= 0; i--) {
            nodes.get(p).count++;
            int b = (x >> i) & 1;
            if (nodes.get(p).child[b] == -1) {
                nodes.get(p).child[b] = nodes.size();
                nodes.add(new Node());
            }
            p = nodes.get(p).child[b];
        }
        nodes.get(p).count++;
    }

    public void delete(int x) {
        if (count(x) == 0) return;
        int p = 0;
        for (int i = BITLEN - 1; i >= 0; i--) {
            nodes.get(p).count--;
            int b = (x >> i) & 1;
            p = nodes.get(p).child[b];
        }
        nodes.get(p).count--;
    }

    public boolean contains(int x) {
        return count(x) > 0;
    }

    public int first() {
        return size() == 0 ? -1 : get(1);
    }
    
    public int last() {
        return size() == 0 ? -1 : get(size());
    }

    public int kthLargest(int k) {
        return get(size() - k + 1);
    }

    public int kthSmallest(int k) {
        return get(k);
    }

    public int count(int x) {
        int p = 0;
        for (int i = BITLEN - 1; i >= 0; i--) {
            int b = (x >> i) & 1;
            if (nodes.get(p).child[b] == -1) return 0;
            p = nodes.get(p).child[b];
        }
        return nodes.get(p).count;
    }

    public int lowerCount(int x) {
        int p = 0, count = 0;
        for (int i = BITLEN - 1; i >= 0; i--) {
            int bit = (x >> i) & 1;
            if (bit == 1) {
                int left = nodes.get(p).child[0];
                if (left != -1) count += nodes.get(left).count;
            }
            p = nodes.get(p).child[bit];
            if (p == -1) break;
        }
        return count;
    }

    public int ceilingCount(int x) {
        return this.size() - lowerCount(x);
    }

    public int higherCount(int x) {
        return this.size() - floorCount(x);
    }
  
    public int rangeCount(int l, int r) {
        return lowerCount(r) - lowerCount(l);
    }

    public int floorCount(int x) {
        return lowerCount(x + 1);
    }

    public int lower(int x) {
        int c = lowerCount(x);
        if (c == 0) return -1;
        return get(c);
    }

    public int floor(int x) {
        int c = floorCount(x);
        if (c == 0) return -1;
        return get(c);
    }

    public int higher(int x) {
        int c = ceilingCount(x + 1);
        if (c == 0) return -1;
        return get(size() - c + 1);
    }

    public int ceiling(int x) {
        int c = ceilingCount(x);
        if (c == 0) return -1;
        return get(size() - c + 1);
    }

    public int mex() {
        int p = 0, res = 0;
        for (int i = BITLEN - 1; i >= 0; i--) {
            int left = nodes.get(p).child[0];
            if (left == -1 || nodes.get(left).count < (1 << i)) {
                p = (left == -1 ? -1 : left);
            } else {
                res |= (1 << i);
                p = nodes.get(p).child[1];
            }
            if (p == -1) break;
        }
        return res;
    }
    
    private int get(int k) {
        if (k <= 0 || k > size()) throw new IllegalArgumentException("Invalid k");
        int p = 0, res = 0;
        for (int i = BITLEN - 1; i >= 0; i--) {
            res <<= 1;
            int left = nodes.get(p).child[0];
            int leftSize = (left == -1 ? 0 : nodes.get(left).count);
            if (leftSize >= k) {
                p = left;
            } else {
                k -= leftSize;
                p = nodes.get(p).child[1];
                res |= 1;
            }
        }
        return res;
    }

}
