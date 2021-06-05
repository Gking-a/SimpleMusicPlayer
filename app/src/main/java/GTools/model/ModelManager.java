package gtools.model;

import gtools.managers.GHolder;

import java.util.ArrayList;
import java.util.Set;

public class ModelManager<T extends GHolder> {
    private ArrayList<T> mModelHolder=new ArrayList<>();
    public void add(T m){
        mModelHolder.add(m);
    }
    private ArrayList<T> search(String s,Object c,ArrayList<T> a,boolean contain){
        ArrayList<T> r=new ArrayList<T>();
        for(T t:a){
            if(contain){
                String b=(String)c;
                if(((String)t.get(s)).contains(b))r.add(t);
                continue;
            }
            if(t.get(s).equals(c))r.add(t);
        }
        return r;
    }
    public ArrayList<T> searchEqual(String s,Object c){
        return search(s,c,mModelHolder,false);
    }
    public ArrayList<T> searchEqual(GHolder m){
        return search(m,false);
    }
    public ArrayList<T> searchContain(String s,Object c){
        return search(s,c,mModelHolder,true);
    }
    public ArrayList<T> searchContain(GHolder m){
        return search(m,true);
    }
    private ArrayList<T> search(GHolder m,boolean contains){
        Object[] s=m.getIds().toArray();
        ArrayList<T> r=new ArrayList<T>();
        for(int i=0;i<s.length;i++){
            r=search((String)s[i],m.get(s[i]),r,contains);
        }
        return r;
    }
    public int size(){
        return mModelHolder.size();
    }
    public void removeEquale(T m){
        
    }
    public void removeEquale(String s,Object c){

    }
    public void removeContain(T m){
        
    }
    public void removeContain(String s,String c){

    }
    private void remove(String s,Object c,boolean contain,boolean keep){
        ArrayList<Integer> p=new ArrayList<>();
        for(int i=0;i<mModelHolder.size();i++){
            if(keep){
                if(contain){
                    String b=(String)c;
                    Object v=mModelHolder.get(i);
                    if(((String)v).contains(b)){
                        p.add(i);
                    }
                    continue;
                }
                if(!c.equals(mModelHolder.get(i))){
                    p.add(i);
                }
                continue;
            }
            if(contain){
                String b=(String)c;
                Object v=mModelHolder.get(i);
                if(((String)v).contains(b)){
                    p.add(i);
                }
                continue;
            }
            if(c.equals(mModelHolder.get(i))){
                p.add(i);
            }
        }
        for(Integer i:p){
            mModelHolder.remove(i);
        }
    }
    public ModelManager<T> cloneContent(){
        ModelManager<T> mm=new ModelManager<>();
        for(T m:mModelHolder){
            mm.add(m);
        }
        return mm;
    }
}
