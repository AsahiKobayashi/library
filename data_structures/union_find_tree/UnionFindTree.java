    class UnionFindTree {

        private int [] parents , size;
        private int size_;
        private BiConsumer<Integer,Integer> consumer;

        UnionFindTree(int n) {
            this.parents=new int[n];
            this.size=new int[n];
            this.size_=n;
            Arrays.fill(size , 1);
            Arrays.setAll(parents,i->i);
        }

        UnionFindTree(int n , BiConsumer<Integer,Integer> consumer) {
            this(n);
            this.consumer = consumer;
        }

        public int size(){
            return size_;
        }

        public int count(int x){ 
            return size[root(x)] ; 
        }

        public boolean same(int x,int y){ 
            return root(x)==root(y) ; 
        }

        public int root(int x){
            if(x==parents[x]) return x ;
            else parents[x]=root(parents[x]);
            return parents[x];
        }

        public void unite(int l,int r){
            int x = root(l);
            int y = root(r);
            if(x == y) return ;
            if(x < y) {
                int tmp = x;
                x = y ;
                y = tmp;
            }
            int par=x,ch=y;
            size_--;
            size[par] += size[ch];
            if(Objects.nonNull(consumer)) {
                consumer.accept(par,ch);
            }
            parents[ch] = par;
        }

    }

}
