//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package cn.gking.gtools.model;

import cn.gking.gtools.managers.GHolder;
import java.util.ArrayList;
import java.util.Iterator;

public class ModelManager<T extends GHolder> {
    private ArrayList<T> mModelHolder = new ArrayList();

    public ModelManager() {
    }

    public void add(T m) {
        this.mModelHolder.add(m);
    }

    private ArrayList<T> search(String s, Object c, ArrayList<T> a, boolean contain) {
        ArrayList<T> r = new ArrayList();
        Iterator<T> var6 = a.iterator();

        while(var6.hasNext()) {
            T t = var6.next();
            if (contain) {
                String b = (String)c;
                if (((String)t.get(s)).contains(b)) {
                    r.add(t);
                }
            } else if (t.get(s).equals(c)) {
                r.add(t);
            }
        }

        return r;
    }

    public ArrayList<T> searchEqual(String s, Object c) {
        return this.search(s, c, this.mModelHolder, false);
    }

    public ArrayList<T> searchEqual(GHolder m) {
        return this.search(m, false);
    }

    public ArrayList<T> searchContain(String s, Object c) {
        return this.search(s, c, this.mModelHolder, true);
    }

    public ArrayList<T> searchContain(GHolder m) {
        return this.search(m, true);
    }

    private ArrayList<T> search(GHolder m, boolean contains) {
        Object[] s = m.getIds().toArray();
        ArrayList<T> r = new ArrayList();

        for(int i = 0; i < s.length; ++i) {
            r = this.search((String)s[i], m.get(s[i]), r, contains);
        }

        return r;
    }

    public int size() {
        return this.mModelHolder.size();
    }

    public void removeEquale(T m) {
    }

    public void removeEquale(String s, Object c) {
    }

    public void removeContain(T m) {
    }

    public void removeContain(String s, String c) {
    }

    private void remove(String s, Object c, boolean contain, boolean keep) {
        ArrayList<Integer> p = new ArrayList();

        for(int i = 0; i < this.mModelHolder.size(); ++i) {
            String b;
            Object v;
            if (keep) {
                if (contain) {
                    b = (String)c;
                    v = this.mModelHolder.get(i);
                    if (((String)v).contains(b)) {
                        p.add(i);
                    }
                } else if (!c.equals(this.mModelHolder.get(i))) {
                    p.add(i);
                }
            } else if (contain) {
                b = (String)c;
                v = this.mModelHolder.get(i);
                if (((String)v).contains(b)) {
                    p.add(i);
                }
            } else if (c.equals(this.mModelHolder.get(i))) {
                p.add(i);
            }
        }

        Iterator var9 = p.iterator();

        while(var9.hasNext()) {
            Integer i = (Integer)var9.next();
            this.mModelHolder.remove(i);
        }

    }

    public ModelManager<T> cloneContent() {
        ModelManager<T> mm = new ModelManager();
        Iterator<T> var2 = this.mModelHolder.iterator();

        while(var2.hasNext()) {
            T m = var2.next();
            mm.add(m);
        }

        return mm;
    }
}
