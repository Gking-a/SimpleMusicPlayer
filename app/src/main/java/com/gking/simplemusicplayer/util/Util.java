package com.gking.simplemusicplayer.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.LruCache;

import com.gking.simplemusicplayer.activity.SettingsActivity;
import com.gking.simplemusicplayer.beans.SongBean;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Util {
    public static <T> boolean in(T e,T ...total){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Arrays.stream(total).allMatch(e::equals);
        }else {
            throw new RuntimeException("No Stream API Support,check the Android Version==23?");
        }
    }
    public static void downloadSong(SongBean song){
        String id=song.id;
        File p=new File(SettingsActivity.get(SettingsActivity.Params.local_download));
        File file = new File(p, song.name+"-"+song.author+".mp3");
        try {
            if(!file.getParentFile().exists())file.getParentFile().mkdirs();
            file.createNewFile();
            FileOutputStream fileOutputStream=new FileOutputStream(file);
            BufferedInputStream inputStream=new BufferedInputStream(new URL(getRedirectUrl("https://music.163.com/song/media/outer/url?id="+id+".mp3")).openStream(),1024*1024);
            byte[] b=new byte[1024];
            int r;
            while ((r=inputStream.read(b))>0){
                fileOutputStream.write(b,0,r);
            }
            fileOutputStream.flush();
            fileOutputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String getRedirectUrl(String path) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(path)
                    .openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        conn.setInstanceFollowRedirects(false);
        return conn.getHeaderField("Location");
    }
    static LruCache<String,Bitmap> cache;
    static Map<String, List<OnBitmapLoaded>> wait=new HashMap<>();
    static{
        int maxSize= (int) (Runtime.getRuntime().maxMemory()/2);
        cache=new LruCache<String, Bitmap>(maxSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }
    public static void getCover(String url,OnBitmapLoaded onBitmapLoaded){
        getCover(url,70,70,onBitmapLoaded);
    }
    public static void getCover(String url, int x, int y,OnBitmapLoaded onBitmapLoaded){
        Bitmap bitmap=cache.get(url);
        if (bitmap != null) {
            onBitmapLoaded.onBitmapLoaded(bitmap);
            return;
        }
        synchronized (Util.class){
            if(wait.get(url)==null){
                List<OnBitmapLoaded> list=new ArrayList<>();
                list.add(onBitmapLoaded);
                wait.put(url,list);
                new Thread(() -> {
                    try {
                        Bitmap cover1 = BitmapFactory.decodeStream(new URL(url + "?param=" + x + "y" + y).openStream());
                        cache.put(url,cover1);
                        for (OnBitmapLoaded listener : wait.get(url)) {
                            listener.onBitmapLoaded(cover1);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            }else {
                if(wait.get(url)!=null)
                    wait.get(url).add(onBitmapLoaded);
                bitmap=cache.get(url);
                if (bitmap != null) {
                    onBitmapLoaded.onBitmapLoaded(bitmap);
                }
            }

        }
    }
    public interface OnBitmapLoaded{
        void onBitmapLoaded(Bitmap bitmap);
    }
}
