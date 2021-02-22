package com.gkingswq.simplemusicplayer.util;

import com.gkingswq.simplemusicplayer.util.GSong;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.util.HashMap;

public class GLists {
    private static final File filedir=new File("/data/data/com.gkingswq.simplemusicplayer/files/");
    private GLists(){};
    static{if(!filedir.exists()){
       try{
           filedir.createNewFile();
       }catch(Exception e){}
    }}
    public static Map<String,String> getName(List<String> ids){
        Map<String ,String > names=new HashMap<>();
        for(String id:ids){
            names.put(id, GSong.getName(id));
        }
        return names;
    }
    public static Map<String,String> getName(List<String> ids,String match){
        Map<String ,String > names=getName(ids);
        Map<String ,String > mnames=new HashMap<>();
        for(String id:names.keySet()){
            String name=names.get(id);
            if(!names.get(id).contains(match)){
//                Log.i(match,name);
                continue;
            }
            mnames.put(id,name);
        }
        return mnames;
    }
    private static Map<String,String> reallidmap(){
        final Map<String,String> idmap=new HashMap<>();
        try {
            for (File eachfile:getlists()) {
                String line = null;
                BufferedReader br = new BufferedReader(new FileReader(eachfile));
                while ((line = br.readLine()) != null) {
                    if (line.contains("[Gking:id]*")) {
                        idmap.put(line.substring(11),GSong.getName(line.substring(11)));
                    }
                }
            }
        } catch (IOException e) {}
        return idmap;
    }
    private static Map<String,String> staticidmap;
    private static Map<String,String> staticnamemap;

    public static Map<String, String> getStaticidmap() {
        return staticidmap;
    }

    public static Map<String, String> getStaticnamemap() {
        return staticnamemap;
    }

    static {
        staticidmap= reallidmap();
        staticnamemap= reallnamemap();
    }
    public static void reMap(){
        staticidmap= reallidmap();
        staticnamemap= reallnamemap();
    }
//    public static Map<String,String> getallnamemap(){ }
    private static Map<String,String> reallnamemap(){
        Map<String,String> namemap=new HashMap<>(),idmap= reallidmap();
        for(String k:idmap.keySet()){
            namemap.put(idmap.get(k),k);
        }
        return namemap;
    }
    public static List<File> getlists(){
        return Arrays.asList(filedir.listFiles());
    }
}
