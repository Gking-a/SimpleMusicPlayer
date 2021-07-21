package com.gking.simplemusicplayer.impl;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class MyCookieJar implements CookieJar {
    public static HashMap<HttpUrl,List<Cookie>> cookies=new HashMap<>();
    static String cookie="";
    static String csrf=null;
    @NotNull
    @Override
    public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
        return cookies.get(httpUrl)!=null?cookies.get(httpUrl): new ArrayList<>();
    }
    @Override
    public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {
        cookies.put(httpUrl, list);
        if(httpUrl.url().toString().contains("cellphone"))storeCookie(list);
    }
    private void storeCookie(List<Cookie> list) {
        cookie= StringUtils.join(list,"; ");
        cookie+="; os=pc";
        for(Cookie _csrf:list){
            String text=_csrf.toString();
            if(text.contains("_csrf"))csrf=StringUtils.substringBetween(text,"=",";");
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
