package com.gking.simplemusicplayer.impl;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class MyCookieJar implements CookieJar {
    static Map<HttpUrl,List<Cookie>> cookies=new HashMap<>();
    @NotNull
    @Override
    public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
        return cookies.get(httpUrl);
    }
    @Override
    public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {
        System.out.println(list.size());
        for (Cookie c :
                list) {
            System.out.println(c.toString());
        }
        cookies.put(httpUrl, list);
    }
    public Cookie getLoginCookie(){
        return cookies.get("").get(0);
    }
}
