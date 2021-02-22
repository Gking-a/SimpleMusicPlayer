package GTools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Looper;

import com.gkingswq.simplemusicplayer.Interface.OnGetNameCompile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.gkingswq.simplemusicplayer.Value.Files._NAME;
import static com.gkingswq.simplemusicplayer.Value.StringPool.*;

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
                        URL url=new URL(Detail + id);
                        BufferedReader reader=new BufferedReader(new InputStreamReader(url.openStream()));
                        String line=reader.readLine();
                       
                        String la=line;
                        Pattern pa=Pattern.compile(",\"name\":\"(.*?)\",\"");
                        Matcher ma=pa.matcher(la);
                        String author = "";
                        while (ma.find()){
                            author+="/"+ma.group(1);
                            la=la.substring(la.indexOf(ma.group(0))+ma.group(0).length());
                            ma=pa.matcher(la);
                        }
                        author=author.substring(1);
                        authormap.put(id,author);
                        
                        String si="\"picUrl\":";
                        String li=line.substring(line.indexOf(si));
                        Pattern pi= Pattern.compile("http(.*?)jpg");
                        Matcher mi=pi.matcher(li);
                        mi.find();
                        li = mi.group(0);
                        url = new URL(li);
                        Bitmap bitmap=BitmapFactory.decodeStream(url.openStream());
                        iconmap.put(id,bitmap);
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
