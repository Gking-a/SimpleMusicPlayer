package com.gking.simplemusicplayer.impl;

import android.media.MediaPlayer;

import java.io.IOException;

public class MusicPlayer extends MediaPlayer {
    public static final String Outer="http://music.163.com/song/media/outer/url?id=";
    public MusicPlayer player=this;
    public void start(String id,OnPreparedListener onPreparedListener){
        new Thread(){
            @Override
            public void run() {
                try {
                    try{ player.stop(); }catch (Exception e){}
                    try{ reset(); }catch (Exception e){}
                    setDataSource(Outer+id);
                    setOnPreparedListener(onPreparedListener);
                    prepare();
                    player.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
