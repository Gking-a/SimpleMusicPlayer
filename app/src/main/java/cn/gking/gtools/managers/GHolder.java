//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package cn.gking.gtools.managers;

import java.util.HashMap;
import java.util.Set;

public class GHolder<Index, Content> implements GManageable<Index, Content> {
    public static final GHolder standardInstance = new GHolder();
    private final HashMap<Index, Content> container = new HashMap();

    public GHolder() {
    }

    public GHolder getI() {
        return standardInstance;
    }

    public Content get(Index index) {
        return this.container.get(index);
    }

    public void replace(Index i, Content c) {
        if (this.container.keySet().contains(i)) {
            this.container.remove(i);
            this.container.put(i, c);
        } else {
            this.container.put(i, c);
        }

    }

    public void remove(Index i) {
        this.container.remove(i);
    }

    public Content getremove(Index i) {
        Content temp = (new Getter<Content>()).get(i);
        this.remove(i);
        return temp;
    }

    public void add(Index i, Content c) {
        if (!this.container.keySet().contains(i)) {
            this.container.put(i, c);
        }

    }

    public Set<Index> getIds() {
        return this.container.keySet();
    }

    public class Getter<T> {
        public Getter() {
        }

        public T get(Index index) {
            return (T) GHolder.standardInstance.get(index);
        }
    }
}
