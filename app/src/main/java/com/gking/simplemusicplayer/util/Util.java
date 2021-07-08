package com.gking.simplemusicplayer.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Util {
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
        getCover(url,50,50,onBitmapLoaded);
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
