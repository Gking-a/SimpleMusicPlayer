package com.gking.simplemusicplayer.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonUtil {
    public static String getAsString(JsonObject json, String... names){
        for (int i = 0; i < names.length - 1; i++) {
            json=json.getAsJsonObject(names[i]);
        }
        return json.get(names[names.length-1]).getAsString();
    }
    public static JsonObject getAsJsonObject(JsonObject json, String... names){
        for (String name : names) {
            json=json.getAsJsonObject(name);
        }
        return json;
    }
    public static JsonArray getAsJsonArray(JsonObject json, String... names){
        for (int i = 0; i < names.length - 1; i++) {
            json=json.getAsJsonObject(names[i]);
        }
        return json.get(names[names.length-1]).getAsJsonArray();
    }
    public static JsonElement getAsJsonElement(JsonObject json, String... names){
        for (int i = 0; i < names.length - 1; i++) {
            json=json.getAsJsonObject(names[i]);
        }
        return json.get(names[names.length-1]);
    }
}
