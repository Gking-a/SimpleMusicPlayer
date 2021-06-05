package gtools.managers;
public class GToolManager<I,C> implements GManageable<I,GManageable>
{
    private GToolManager(){}
    private static final GToolManager<Object,GManageable> i=new GToolManager<>();
    public static GToolManager<Object, GManageable> getI(){
        return i;
    }
    private static GHolder<Object,GManageable> h=new GHolder<>();
    @Override
    public GManageable get(I index) {
        return h.get(index);
    }
    @Override
    public void remove(I i) {
        h.remove(i);
    }
    @Override
    public void add(I i, GManageable c) {
        h.add(i,c);
    }
    @Override
    public void replace(I i, GManageable c) {
        h.replace(i,c);
    }
    public static class Getter{
        public static GManageable getManager(managerable t){
            switch(t){
                case GLibraryManager:return getI().get(managerable.GLibraryManager);
                default :return null;
            }
        }
    }
    enum managerable{
        GLibraryManager
    }
    static{
        GLibraryManager<Object,GLibraryManager> glm=new GLibraryManager<>();
        i.add(managerable.GLibraryManager,glm);
    }
}
