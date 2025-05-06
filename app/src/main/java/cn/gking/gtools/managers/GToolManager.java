//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package cn.gking.gtools.managers;

public class GToolManager<I, C> implements GManageable<I, GManageable> {
    private static final GToolManager<Object, GManageable> i = new GToolManager();
    private static GHolder<Object, GManageable> h = new GHolder();

    private GToolManager() {
    }

    public static GToolManager<Object, GManageable> getI() {
        return i;
    }

    public GManageable get(I index) {
        return (GManageable)h.get(index);
    }

    public void remove(I i) {
        h.remove(i);
    }

    public void add(I i, GManageable c) {
        h.add(i, c);
    }

    public void replace(I i, GManageable c) {
        h.replace(i, c);
    }

    static enum managerable {
        GLibraryManager;

        private managerable() {
        }
    }

    public static class Getter {
        public Getter() {
        }

        public static GManageable getManager(managerable t) {
            switch (t) {
                case GLibraryManager:
                    return GToolManager.getI().get(GToolManager.managerable.GLibraryManager);
                default:
                    return null;
            }
        }
    }
}
