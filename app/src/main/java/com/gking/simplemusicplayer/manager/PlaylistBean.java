package com.gking.simplemusicplayer.manager;

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
        coverImgUrl = playlist.get("coverImgUrl").getAsString();
        name = playlist.get("name").getAsString();
    }
}
