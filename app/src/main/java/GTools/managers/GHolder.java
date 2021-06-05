package gtools.managers;
import java.util.HashMap;
import java.util.Set;
public class GHolder<Index,Content> implements GManageable<Index,Content>
{
    public GHolder(){}
    public static final GHolder standardInstance=new GHolder();
    public GHolder getI(){
        return standardInstance;
    }
    private final HashMap<Index,Content> container=new HashMap<>();
    public Content get(Index index){
        return container.get(index);
    }
    public void replace(Index i,Content c){
        if(container.keySet().contains(i)){
            container.replace(i,c);
        }else{
            container.put(i,c);
        }
    }
    public void remove(Index i){
        container.remove(i);
    }
    public Content getremove(Index i){
        Content temp=new Getter<Content>().get(i);
        remove(i);
        return temp;
    }
    public void add(Index i,Content c){
        if(!container.keySet().contains(i))
            container.put(i,c);
    }
    public Set<Index> getIds(){
        return container.keySet();
    }
    public class Getter<T>{
        public T get(Object index){
            return (T)GHolder.standardInstance.get(index);
        }
    }
}
