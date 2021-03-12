package com.gkingswq.simplemusicplayer.util;

import GTools.GLibrary;
import GTools.GLibraryManager;
import android.graphics.Bitmap;
import android.os.Looper;
import com.gkingswq.simplemusicplayer.Interface.OnGetNameCompile;
import com.gkingswq.simplemusicplayer.util.JSON;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import org.json.JSONObject;

import static com.gkingswq.simplemusicplayer.Value.StringPool.*;
import static com.gkingswq.simplemusicplayer.Value.Files._NAME;
import android.graphics.BitmapFactory;
public class GSong {
    
    private GSong(){}
    public static Map<String,String> staticmap=new Hashtable<>();
    static{
        GLibrary _lib= GLibraryManager.getLib(_NAME);
        staticmap=_lib.getStringMap();
    }
    //2020 07 14
    private static final Map<String,Bitmap> iconmap=new Hashtable<>();
    public static Bitmap getIcon(String id){
        if(iconmap.containsKey(id)){
            return iconmap.get(id);
        }
        if(iconmap.size()>5){
            Set<String> mkeyset=new HashSet<>();
            for(String temp:iconmap.keySet()){
                mkeyset.add(temp);
            }
            for(String key:mkeyset){
                iconmap.remove(key);
            }
        }
        load(id);
        while(iconmap.get(id)==null){
            try{Thread.sleep(1);}catch(Exception e){}
        }
        return iconmap.get(id);
    }
    public static Bitmap getIcon(String id,int width,int height){
        return Bitmap.createScaledBitmap(getIcon(id),width,height,true);
    }
    //2020 07 16,中考
    public static Bitmap getSquareIcon(String id,int scale){
        Bitmap mbitmap=getIcon(id,scale);
        int width=mbitmap.getWidth() ,height=mbitmap.getHeight();
        if(width==height){
            return mbitmap;
        }
        mbitmap=width>height?
        Bitmap.createBitmap(mbitmap,(width-height)/2,0,height,height):
        Bitmap.createBitmap(mbitmap,0,(height-width)/2,width,width);
        return mbitmap;
    }
    //2021.3.4,看到当时中考的痕迹，感慨颇多啊。也说不出什么来。只能说时间太快了。甚至说，快要迎来一周年生日了
    //好像是中考回来中午写的代码，而不是晚上。
    //其实感觉当时写的代码已经不错了，但是当时的耦合性太强了，如果用现在的思想去写的话，应该会更好。
    public static Bitmap getSquareIcon(Bitmap source,int scale){
        Bitmap mbitmap=source;
        int width=mbitmap.getWidth() ,height=mbitmap.getHeight();
        if(width==height){
            return mbitmap;
        }
        mbitmap=width>height?
            Bitmap.createBitmap(mbitmap,(width-height)/2,0,height,height):
            Bitmap.createBitmap(mbitmap,0,(height-width)/2,width,width);
        return mbitmap;

    }
    public static Bitmap getIcon(String id,int scale){
        Bitmap mbitmap=getIcon(id);
        int width=mbitmap.getWidth(),
            height=mbitmap.getHeight();
        if(width==height){
            return getIcon(id,scale,scale);
        }
//        Log.i("bitmap",width+":"+height+":"+(width*scale)/height+":"+(height*scale)/width);
        mbitmap=width>height?
        getIcon(id,(width*scale)/height,scale):getIcon(id,scale,(height*scale)/width);
        return mbitmap;
    }
    //2020 07 14
    private static final Map<String,String> authormap=new HashMap<>();
    public static String getAuthor(String id){
        if(authormap.containsKey(id)){
            return authormap.get(id);
        }
        load(id);
        while(authormap.get(id)==null){
            try{Thread.sleep(1);}catch(Exception e){}
        }
        return authormap.get(id);
    }
    private static void load(final String id){
        new Thread(
            new Runnable(){
                @Override
                public void run() {
                    try {
                        Looper.prepare();
                        URL url=new URL("https://y.music.163.com/m/song?id="+id);
                        HttpURLConnection c=(HttpURLConnection)url.openConnection();
                        c.setDoInput(true);
                        c.setDoOutput(true);
                        c.setUseCaches(false);
                        c.setRequestMethod("GET");
                        BufferedReader r=new BufferedReader(new InputStreamReader(c.getInputStream()));
                        String l="";
                        while((l=r.readLine())!=null){
                            if(l.contains("window.REDUX_STATE")){
                                l=l.substring(l.indexOf("=")+1);
                                JSONObject o=new JSONObject(l);
                                String ar="";
                                for (int i = 0; i < o.getJSONObject("Song").getJSONArray("ar").length(); i++) {
                                    JSONObject o2=o.getJSONObject("Song").getJSONArray("ar").getJSONObject(i);
                                    ar+="/"+o2.getString("name");
                                }
                                authormap.put(id,ar);
                                String s1=o.getJSONObject("Song").getJSONObject("al").getString("picUrl");
                                Bitmap b=BitmapFactory.decodeStream(new URL(s1).openStream());
                                iconmap.put(id,b);
                            }
                        }
//                        URL url=new URL(Detail + id);
//                        BufferedReader reader=new BufferedReader(new InputStreamReader(url.openStream()));
//                        String line=reader.readLine();
//                       
//                        String la=line;
//                        Pattern pa=Pattern.compile(",\"name\":\"(.*?)\",\"");
//                        Matcher ma=pa.matcher(la);
//                        String author = "";
//                        while (ma.find()){
//                            author+="/"+ma.group(1);
//                            la=la.substring(la.indexOf(ma.group(0))+ma.group(0).length());
//                            ma=pa.matcher(la);
//                        }
//                        author=author.substring(1);
//                        authormap.put(id,author);
//                        
//                        String si="\"picUrl\":";
//                        String li=line.substring(line.indexOf(si));
//                        Pattern pi= Pattern.compile("http(.*?)jpg");
//                        Matcher mi=pi.matcher(li);
//                        mi.find();
//                        li = mi.group(0);
//                        url = new URL(li);
//                        Bitmap bitmap=BitmapFactory.decodeStream(url.openStream());
//                        iconmap.put(id,bitmap);
                    } catch (Exception e) {
                    }
                }
            }
        ){}.start();
    }
    public static String getName(final String id,OnGetNameCompile listener){
        String temp=null;
        if(staticmap.containsKey(id)){
            if (listener != null) {
                temp=staticmap.get(id);
            }else {
                return staticmap.get(id);
            }
        }
        new JSON().getName(id,temp, listener);
        return UnExistName;
    }
    public static String getName(String id){
        return getName(id,null);
    }
}
