package com.gking.simplemusicplayer.beans;

import com.gking.simplemusicplayer.util.JsonUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class SongBean implements Serializable {
    public SongBean(){}
    public SongBean(String playlistId, JsonObject song) {
        this.playlistId = playlistId;
        if (song == null) return;
        id = JsonUtil.getAsString(song, "id");
        name = JsonUtil.getAsString(song, "name");
        List<String> ars = new LinkedList<>();
        JsonArray ar = JsonUtil.getAsJsonArray(song, "ar");
        for (int i = 0; i < ar.size(); i++) {
            JsonObject asJsonObject = ar.get(i).getAsJsonObject();
            if(asJsonObject==null|| asJsonObject.isJsonNull())continue;
            try{
                ars.add(JsonUtil.getAsString(asJsonObject, "name"));
            }catch (Exception exception){continue;}
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
    public String id, name, author, playlistId;
    public String coverUrl;
    public transient SongBean next, last,rn,rl;
    public transient LyricBean lyric;

    @Override
    public String toString() {
        return "SongBean{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", playlistId='" + playlistId + '\'' +
                ", coverUrl='" + coverUrl + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SongBean songBean = (SongBean) o;
        return Objects.equals(id, songBean.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
