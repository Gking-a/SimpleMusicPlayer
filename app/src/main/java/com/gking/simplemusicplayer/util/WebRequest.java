package com.gking.simplemusicplayer.util;

import com.gking.simplemusicplayer.impl.MyCookieJar;
import com.gking.simplemusicplayer.manager.PlaylistBean;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.BufferedSink;

public final class WebRequest {
    public static void playlist_unsubscribe(String id,int type,Callback callback){
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("id",id);
        post(URLs.playlist_unsubscribe,jsonObject,MyCookieJar.getLoginCookie(),callback);
    }
    public static void playlist_subscribe(String id,int type,Callback callback){
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("id",id);
        post(URLs.playlist_subscribe,jsonObject,MyCookieJar.getLoginCookie(),callback);
    }
    public static void playlist_delete(String id,Callback callback){
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("ids","["+id+"]");
        post(URLs.playlist_delete,jsonObject,MyCookieJar.getLoginCookie(),callback);
    }
    public static void playlist_create(String name,int privacy,String type,String cookie,Callback callBack){
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("name",name);
        if(privacy!=0)
            jsonObject.addProperty("privacy",privacy+"");
        jsonObject.addProperty("type",type);
        post(URLs.playlist_create,jsonObject,cookie,callBack);
    }
    public static void recommend_songs(String cookie,Callback callback){
        post(URLs.recommend_songs,new JsonObject(),cookie,callback);
    }
    public static void recommend_resource(String cookie,Callback callback){
        post(URLs.recommend_resource,new JsonObject(),cookie,callback);
    }
    public static void cloudsearch(String keyword, int type, String cookie, Callback callback){
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("s",keyword);
        jsonObject.addProperty("type",type);
        jsonObject.addProperty("limit",100);
        jsonObject.addProperty("offset",0);
        jsonObject.addProperty("total",true);
        post(URLs.cloudsearch,jsonObject,cookie,callback);
    }
    public static void playlist_order_update1(List<PlaylistBean> beans, String cookie, Callback callback){
        List<String> list=new ArrayList<>(beans.size());
        for(PlaylistBean b:beans)
            list.add(b.id);
        playlist_order_update(list,cookie,callback);
    }
    public static void playlist_order_update(List<String> ids,String cookie,Callback callback){
        if(ids==null||ids.size()==0)return;
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("ids","["+ StringUtils.join(ids,",")+"]");
        post(URLs.playlist_order_update,jsonObject,cookie,callback);
    }
    public static void lyric(String id,String cookie,Callback callback){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id",id);
        jsonObject.addProperty("kv",-1);
        jsonObject.addProperty("lv",-1);
        jsonObject.addProperty("tv",-1);
        post(URLs.lyric,jsonObject,cookie,callback);
    }
    public static void song_detail(List<String> ids,String cookie,Callback callback){
        JsonObject jsonObject=new JsonObject();
        JsonArray array=new JsonArray();
        for(String id:ids){
            JsonObject object=new JsonObject();
            object.addProperty("id",Long.parseLong(id));
            array.add(object);
        }
        jsonObject.add("c",array);
        jsonObject.addProperty("csrf_token", MyCookieJar.getCsrf());
        String s=jsonObject.toString();
        s=s.replaceAll("\"id\"","\\\\\"id\\\\\"");
        s=s.replace("[","\"[");
        s=s.replace("]","]\"");
        post(URLs.song_detail,s,cookie,callback);
    }
    public static void playlist_detail(String id,String cookie,Callback callback){
        JsonObject object=new JsonObject();
        object.addProperty("id",id);
        post(URLs.playlist_detail,object,cookie,callback);
    }
    public static void login_cellphone(String phone, String password, Callback callback) {
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
    //auto add param csrf_token
    public static void post(String url, JsonObject params, String cookie, Callback callback){
        params.addProperty("csrf_token", MyCookieJar.getCsrf());
        post(url, params.toString(), cookie, callback);
    }
    //only post
    public static void post(String url,String params,String cookie,Callback callback){
        HashMap<String, String> data = WebCrypto.encrypt(params);
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
        for (String key:data.keySet()) {
            builder.add(key,data.get(key));
        }
        Request request=new Request.Builder()
                .url(url)
                .headers(headers)
                .post(builder.build())
                .build();
        Call call = client.newCall(request);
        if (callback != null) call.enqueue(callback);
    }
}
