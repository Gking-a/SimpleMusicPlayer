package com.gking.simplemusicplayer.impl;

import android.graphics.Bitmap;
import android.media.MediaPlayer;

import com.gking.simplemusicplayer.util.JsonUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;

public class MusicPlayer extends MediaPlayer {
    public static final String Outer="http://music.163.com/song/media/outer/url?id=";
    public MusicPlayer player=this;
    private boolean prepared=false;
    private boolean auto=false;
    private MusicBean musicBean=new MusicBean(null);
    MusicPlayer(){
        super();
        setOnCompletionListener((mp -> {
            if(auto)next(null);
        }));
    }
    public MusicBean getMusicBean() {
        return musicBean;
    }

    public void setAuto(boolean auto) {
        this.auto = auto;
    }

    public void start(MusicBean musicBean, OnPreparedListener onPreparedListener){
        this.musicBean=musicBean;
        new Thread(){
            @Override
            public void run() {
                try {
                    try{ player.stop(); }catch (Exception e){}
                    try{ reset(); }catch (Exception e){}
                    setDataSource(Outer+musicBean.id);
                    setOnPreparedListener(onPreparedListener);
                    prepare();
                    prepared=true;
                    player.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    @Override
    public void reset() {
        super.reset();
        prepared=false;
    }

    @Override
    public void stop() throws IllegalStateException {
        try { super.stop(); }catch (Exception e){ }
    }
    @Override
    public void start() throws IllegalStateException {
        try { super.start(); }catch (Exception e){ }
    }

    @Override
    public void pause() throws IllegalStateException {
        try {
            if(isPlaying()){
                super.pause();
            }else {
                start();
            }
        }catch (Exception e){ }
    }

    @Override
    public void seekTo(int msec) throws IllegalStateException {
        try { super.seekTo(msec); }catch (Exception e){ }
    }

    public void next(OnPreparedListener onPreparedListener) {
        start(musicBean.next,onPreparedListener);
    }
    public void last(OnPreparedListener onPreparedListener) {
        start(musicBean.last,onPreparedListener);
    }
    public class MusicBean{
        public MusicBean(JsonObject song) {
            if(song==null)return;
            id=JsonUtil.getAsString(song,"id");
            name= JsonUtil.getAsString(song,"name");
            StringBuilder sb=new StringBuilder();
            JsonArray ar = JsonUtil.getAsJsonArray(song, "ar");
            for (int i = 0; i < ar.size(); i++) {
                sb.append(ar.get(i).getAsJsonObject().get("name").getAsString()).append("/");
            }
            author=sb.substring(0,sb.length()-1);
        }
        public String id,name,author;
        public Bitmap cover;
        public MusicBean next,last;
    }
}
