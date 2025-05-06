package com.gking.simplemusicplayer.util;

import static com.gking.simplemusicplayer.activity.SettingsActivity.Params.MUSIC_A_T;
import static com.gking.simplemusicplayer.activity.SettingsActivity.Params.NMTID;
import static com.gking.simplemusicplayer.activity.SettingsActivity.Params.__csrf;

import com.gking.simplemusicplayer.activity.SettingsActivity;
import com.gking.simplemusicplayer.manager.PlaylistBean;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public final class WebRequest {
    public static void user_record(Callback callback,String uid,int type){
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("uid",uid);
        jsonObject.addProperty("type",type);
        post(URLs.user_record,jsonObject,callback);
    }
    public static void user_account(Callback callback){
        JsonObject jsonObject=new JsonObject();
        post(URLs.user_account,jsonObject, callback);
    }
    public static void logout(){
        JsonObject jsonObject=new JsonObject();
        post(URLs.logout,jsonObject, null);
    }
    public static void login_qr_check(String key,Callback callback){
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("key",key);
        jsonObject.addProperty("type",1);
        post(URLs.login_qr_check,jsonObject.toString(),callback,false,false,"cookie: os=pc");
    }
    public static void login_qr_key(Callback callback) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type","1");
        post(URLs.login_qr_key,jsonObject, callback);
    }
    public static void check_music(String id,Callback callback){
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("ids","["+id+"]");
        jsonObject.addProperty("br",320000);
        post(URLs.check_music,jsonObject, callback);
    }
    public static void playlist_tracks_add(String pid,String[] trackIds,Callback callback){
        playlist_tracks("add",pid,trackIds,callback);
    }
    public static void playlist_tracks_delete(String pid,String[] trackIds,Callback callback){
        playlist_tracks("del",pid,trackIds,callback);
    }
    public static void playlist_tracks(String op,String pid,String[] trackIds,Callback callback){
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("op",op);
        jsonObject.addProperty("pid",pid);
        jsonObject.addProperty("trackIds","[\""+StringUtils.join(trackIds,"\",\"")+"\"]");
        jsonObject.addProperty("imme",true);
        System.out.println(jsonObject.toString());
        post(URLs.playlist_tracks,jsonObject, callback);
    }
    public static void playlist_unsubscribe(String id,Callback callback){
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("id",id);
        post(URLs.playlist_unsubscribe,jsonObject, callback);
    }
    public static void playlist_subscribe(String id,Callback callback){
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("id",id);
        post(URLs.playlist_subscribe,jsonObject, callback);
    }
    public static void playlist_delete(String id,Callback callback){
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("ids","["+id+"]");
        post(URLs.playlist_delete,jsonObject, callback);
    }
    public static void playlist_create(String name,int privacy,String type,Callback callBack){
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("name",name);
        if(privacy!=0)
            jsonObject.addProperty("privacy",privacy+"");
        jsonObject.addProperty("type",type);
        post(URLs.playlist_create,jsonObject,callBack);
    }
    public static void recommend_songs(Callback callback){
        post(URLs.recommend_songs,new JsonObject(),callback);
    }
    public static void recommend_resource(Callback callback){
        post(URLs.recommend_resource,new JsonObject(),callback);
    }
    public static void cloudsearch(String keyword, int type,  Callback callback){
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("s",keyword);
        jsonObject.addProperty("type",type);
        jsonObject.addProperty("limit",100);
        jsonObject.addProperty("offset",0);
        jsonObject.addProperty("total",true);
        post(URLs.cloudsearch,jsonObject,callback);
    }
    public static void playlist_order_update1(List<PlaylistBean> beans,  Callback callback){
        List<String> list=new ArrayList<>(beans.size());
        for(PlaylistBean b:beans)
            list.add(b.id);
        playlist_order_update(list,callback);
    }
    public static void playlist_order_update(List<String> ids,Callback callback){
        if(ids==null||ids.size()==0)return;
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("ids","["+ StringUtils.join(ids,",")+"]");
        post(URLs.playlist_order_update,jsonObject,callback);
    }
    public static void lyric(String id,Callback callback){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id",id);
        jsonObject.addProperty("kv",-1);
        jsonObject.addProperty("lv",-1);
        jsonObject.addProperty("tv",-1);
        post(URLs.lyric,jsonObject,callback);
    }
    public static void song_detail(List<String> ids,Callback callback){
        JsonObject jsonObject=new JsonObject();
        JsonArray array=new JsonArray();
        for(String id:ids){
            JsonObject object=new JsonObject();
            object.addProperty("id",Long.parseLong(id));
            array.add(object);
        }
        jsonObject.add("c",array);
        jsonObject.addProperty("csrf_token", MyCookies.get__csrf());
        String s=jsonObject.toString();
        s=s.replaceAll("\"id\"","\\\\\"id\\\\\"");
        s=s.replace("[","\"[");
        s=s.replace("]","]\"");
        post(URLs.song_detail,s,callback);
    }
    public static void playlist_detail(String id,Callback callback){
        JsonObject object=new JsonObject();
        object.addProperty("id",id);
        post(URLs.playlist_detail,object,callback);
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
//        jsonObject.addProperty("rememberLogin", true);
        jsonObject.addProperty("countrycode", 86);
        post(URLs.login_cellphone, jsonObject, callback);
    }
    public static void user_playlist(String uid,int limit,int offset,Callback callback){
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("uid",Long.parseLong(uid));
        jsonObject.addProperty("limit",limit);
        jsonObject.addProperty("offset",offset);
        jsonObject.addProperty("includeVideo",true);
        post(URLs.user_playlist,jsonObject,callback);
    }
    public static void user_playlist(String uid, Callback callback){
        user_playlist(uid,100,0,callback);
    }
    //auto add param csrf_token
    public static void post(String url, JsonObject params, Callback callback){
        params.addProperty("csrf_token", MyCookies.get__csrf());
        post(url, params.toString(), callback);
    }

    public static MyCookies cookieJar = new MyCookies();
    public static void post(String url,String params,Callback callback){
        post(url,params,callback,true,true);
    }
    //only post
    public static void post(String url, String params, Callback callback,boolean csrf_param ,boolean useStroageCookie,String ...cookies){
        OkHttpClient client=new OkHttpClient.Builder()
                .build();
        Headers.Builder headerBuilder=new Headers.Builder()
                .add("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:80.0) Gecko/20100101 Firefox/80.0")
                .add("Content-Type","application/x-www-form-urlencoded")
                .add("Referer","https://music.163.com")
                .add("accept","*/*");
        if(useStroageCookie){
            if(MyCookies.init){
                headerBuilder
                        .add(smallcookiefunc(SettingsActivity.Params.__csrf,MyCookies.__csrf))
                        .add(smallcookiefunc(SettingsActivity.Params.MUSIC_A_T,MyCookies.MUSIC_A_T))
                        .add(smallcookiefunc(SettingsActivity.Params.MUSIC_R_T,MyCookies.MUSIC_R_T))
                        .add(smallcookiefunc(SettingsActivity.Params.NMTID,MyCookies.NMTID))
                        .add(smallcookiefunc(SettingsActivity.Params.MUSIC_U,MyCookies.MUSIC_U))
                ;
            }
        }
        for (String ecookie : cookies) {
            headerBuilder.add(ecookie);
        }
        FormBody.Builder builder =new FormBody.Builder();
        if(csrf_param)url=url+"?csrf_token="+MyCookies.__csrf;
        Request.Builder requestBuilder=new Request.Builder()
                .url(url)
                .headers(headerBuilder.build());
        if(params!=null){
            HashMap<String, String> data = WebCrypto.encrypt(params);
            for (String key:data.keySet()) {
                builder.add(key,data.get(key));
            }
            requestBuilder=requestBuilder.post(builder.build());
        }

        Request request=requestBuilder.build();
        Call call = client.newCall(request);
        if (callback != null) call.enqueue(callback);
    }
    public static String smallcookiefunc(String k,String v){
        return "cookie: "+k+"="+v;
    }
}
