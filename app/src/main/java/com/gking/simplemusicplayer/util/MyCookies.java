package com.gking.simplemusicplayer.util;

import java.util.HashMap;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

public class MyCookies {
    public static HashMap<HttpUrl,List<Cookie>> cookies=new HashMap<>();
    public static String cookie="";
    public static String __csrf ="",MUSIC_A_T,MUSIC_R_T,NMTID,MUSIC_U;
    public static String qrkey=null;
    public static boolean init;
    static List<Cookie> lastlist=null;
    public static String get__csrf(){
        if (__csrf == null) {
            return "";
        }
        return __csrf;
    }
}
