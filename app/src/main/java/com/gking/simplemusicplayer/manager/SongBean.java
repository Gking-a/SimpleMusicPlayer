package com.gking.simplemusicplayer.manager;

import com.gking.simplemusicplayer.util.JsonUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class SongBean implements Serializable {
    public SongBean(String pid,JsonObject song) {
        this.pid=pid;
        if (song == null) return;
        id = JsonUtil.getAsString(song, "id");
        name = JsonUtil.getAsString(song, "name");
        List<String> ars = new LinkedList<>();
        JsonArray ar = JsonUtil.getAsJsonArray(song, "ar");
        for (int i = 0; i < ar.size(); i++) {
            ars.add(JsonUtil.getAsString(ar.get(i).getAsJsonObject(), "name"));
        }
        author = StringUtils.join(ars, "/");
        coverUrl = JsonUtil.getAsString(song, "al", "picUrl");
    }

    public SongBean(String id, String name, String author) {
        this.id = id;
        this.name = name;
        this.author = author;
    }
    public SongBean(String id, String name, String author, SongBean next, SongBean last) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.next = next;
        this.last = last;
    }
    public String id, name, author,pid;
    public String coverUrl;
    public SongBean next, last;
    public transient LyricBean lyric;
}
