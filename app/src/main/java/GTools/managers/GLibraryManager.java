package gtools.managers;

import gtools.GLibrary;

import java.io.File;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

public class GLibraryManager<I,C> implements GManageable<I,GLibrary> {
    private static Map<String, GLibrary> map=new Hashtable();
    private GHolder<I,GLibrary> map2=new GHolder<>();
    public static Collection<GLibrary> getAllLibs(){
        return map.values();
    }
    public static GLibrary getLib(String path){
        if(map.containsKey(path)){
            return map.get(path);
        }
        return null;
    }
    public static GLibrary getLib(File file){
        return getLib(file.getAbsolutePath());
    }
    public static GLibrary getLib(String name,File file){
        return getLib(name,file.getAbsolutePath());
    }
    public static GLibrary getLib(String name,String path){
        if(getLib(path)!=null)
            return getLib(path);
        GLibrary lib=new GLibrary(name,path);
        add(lib);
        return lib;
    }
    public static GLibrary getLib(String name,GLibrary father){
        if(father==null&&father.getLibrary()==null){
            return null;
        }
        String path=father.getLibrary().getAbsolutePath()+"$"+name;
        return getLib(name,path);
    }
    public static void add(GLibrary lib){
        if(!map.containsKey(lib.getLibrary().getAbsolutePath()))
            map.put(lib.getLibrary().getAbsolutePath(),lib);
    }
    @Override
    public GLibrary get(I o) {
        return map2.get(o);
    }
    @Override
    public void remove(I i) {
        map2.remove(i);
    }
    @Override
    public void add(I i, GLibrary c) {
        map2.add(i,c);
    }
    @Override
    public void replace(I i, GLibrary c) {
        map2.replace(i,c);
    }
}
