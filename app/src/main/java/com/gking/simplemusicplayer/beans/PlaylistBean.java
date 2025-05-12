package com.gking.simplemusicplayer.beans;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.List;

public class PlaylistBean implements Serializable {

    public final String id;
    public final String coverImgUrl;
    public final String name;
    public List<String> trackIds;
    public PlaylistBean(JsonObject playlist){
        id = playlist.get("id").getAsString();
        JsonElement jsonElement = playlist.get("coverImgUrl");
        if (jsonElement == null) {
            jsonElement=playlist.get("picUrl");
        }
        coverImgUrl= jsonElement.getAsString();
        name = playlist.get("name").getAsString();
    }
}
