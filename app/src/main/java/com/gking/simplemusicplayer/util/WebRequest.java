package com.gking.simplemusicplayer.util;

import com.gking.simplemusicplayer.impl.MyCookieJar;
import com.google.gson.JsonObject;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;


public class WebRequest {
    public static void cellphone(String phone, String password, Callback callback) {
        JsonObject jsonObject = new JsonObject();
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("md5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] b = md5.digest(password.getBytes(StandardCharsets.UTF_8));
        password = new BigInteger(1, b).toString(16);
        jsonObject.addProperty("phone", phone);
        jsonObject.addProperty("password", password);
        jsonObject.addProperty("rememberLogin", true);
        jsonObject.addProperty("countrycode", 86);
        post(URLs.login_cellphone, jsonObject, "os=pc", callback);
    }
    public static void user_playlist(String uid,int limit,int offset,String cookie,Callback callback){
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("uid",Long.parseLong(uid));
        jsonObject.addProperty("limit",limit);
        jsonObject.addProperty("offset",offset);
        jsonObject.addProperty("includeVideo",true);
        post(URLs.user_playlist,jsonObject,cookie,callback);
    }
    public static void user_playlist(String uid, String cookie,Callback callback){
        user_playlist(uid,100,0,cookie,callback);
    }
    static HashMap<String,String> en=new HashMap<>();
    static {
        en.put("+","%2B");
        en.put("/","%2F");
        en.put("=","%3D");
    }
    public static void post(String url, JsonObject params, String cookie, Callback callback){
        params.addProperty("csrf_token", "");
        HashMap<String, String> data = MyCrypto.encrypt(params.toString());
        OkHttpClient client=new OkHttpClient.Builder()
                .cookieJar(new MyCookieJar())
                .build();
        Headers headers=new Headers.Builder()
                .add("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:80.0) Gecko/20100101 Firefox/80.0")
                .add("Content-Type","application/x-www-form-urlencoded")
                .add("Referer","https://music.163.com")
                .add("Cookie",cookie)
                .build();
        FormBody.Builder builder =new FormBody.Builder();
        for(String key:data.keySet()){
            builder.add(key,data.get(key));
        }
        Request request=new Request.Builder()
                .url(url)
                .headers(headers)
                .post(builder.build())
                .build();
        client.newCall(request)
                .enqueue(callback);
    }
}
