package com.gking.simplemusicplayer.util;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Callback;


public class WebRequest {
    public static void cellphone(String phone, String password, Callback callback) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("phone", phone);
        jsonObject.addProperty("password", password);
        jsonObject.addProperty("rememberLogin", true);
        jsonObject.addProperty("countrycode", 86);
        jsonObject.addProperty("csrf_token", "");
        HashMap<String, String> data = Web163.encrypt(jsonObject.toString());
        replace(data, "params", encode(data.get("params")));
        Web163.post(URLs.login_cellphone, data, "os=pc", callback);
    }
    static HashMap<String,String> en=new HashMap<>();
    static {
        en.put("+","%2B");
        en.put("/","%2F");
        en.put("=","%3D");
    }
    public static String encode(String text){
        text=text.replaceAll("\\+",en.get("+"));
        text=text.replaceAll("=",en.get("="));
        text=text.replaceAll("/",en.get("/"));
        return text;
    }
    public static void replace(Map map,String key,String value){
        map.remove(key);
        map.put(key,value);
    }
}
