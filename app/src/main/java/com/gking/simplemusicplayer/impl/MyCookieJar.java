package com.gking.simplemusicplayer.impl;

import com.gking.simplemusicplayer.util.FW;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class MyCookieJar implements CookieJar {
    public static HashMap<HttpUrl,List<Cookie>> cookies=new HashMap<>();
    static String cookie="";
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
        if(list.size()==1) {
            cookie = list.get(0).toString();
            return;
        }
        for (int i = 0; i < list.size() - 1; i++) {
            cookie+=list.get(i).toString()+"; ";
        }
        cookie+=list.get(list.size()-1).toString();
    }
    public static String getLoginCookie(){
        return cookie;
    }
}
