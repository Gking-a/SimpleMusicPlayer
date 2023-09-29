package com.gking.simplemusicplayer.util;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class Cookies implements CookieJar {
    public static HashMap<HttpUrl,List<Cookie>> cookies=new HashMap<>();
    public static String cookie="";
    public static String csrf=null;
    public static String qrkey=null;
    static List<Cookie> lastlist=null;
    @NotNull
    @Override
    public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
        System.err.println(httpUrl);
        if (httpUrl.toString().equals(URLs.login_qr_check))return new ArrayList<>();
        return cookies.get(httpUrl)!=null?cookies.get(httpUrl): new ArrayList<>();
    }
    @Override
    public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {
        System.err.println("save from"+httpUrl+" = ");
        for (Cookie c :
                list) {
            System.out.println(c.toString());
        }
        cookies.put(httpUrl, list);
        lastlist=list;
    }
    public static List<Cookie> getLastCookie(){
        return lastlist;
    }
    public static void storeCookie(List<Cookie> list) {
        cookie= StringUtils.join(list,"; ");
        cookie+="; os=pc";
        try{

            for(Cookie _csrf:list){
                String text=_csrf.toString();
                if(text.contains("_csrf"))csrf=StringUtils.substringBetween(text,"=",";");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static String getLoginCookie(){
        return cookie;
    }
    public static String getCsrf(){
        if (csrf == null) {
            return "";
        }
        return csrf;
    }
}
