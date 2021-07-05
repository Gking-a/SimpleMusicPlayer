package com.gking.simplemusicplayer.impl;

import android.media.MediaPlayer;

import com.gking.simplemusicplayer.manager.SongBean;

import java.io.IOException;

public class MusicPlayer extends MediaPlayer {
    public static final String Outer="http://music.163.com/song/media/outer/url?id=";
    public MusicPlayer player=this;
    private boolean prepared=false;
    private boolean auto=false;
    private SongBean musicBean=new SongBean(null);
    MusicPlayer(){
        super();
        setOnCompletionListener((mp -> {
            if(auto)next(null);
        }));
    }
    public SongBean getMusicBean() {
        return musicBean;
    }

    public void setAuto(boolean auto) {
        this.auto = auto;
    }

    public void start(SongBean songBean,OnPreparedListener listener){
        start(songBean);
    }
    public void start(SongBean musicBean){
        this.musicBean=musicBean;
        new Thread(){
            @Override
            public void run() {
                try {
                    try{ player.stop(); }catch (Exception e){}
                    try{ reset(); }catch (Exception e){}
                    System.out.println(musicBean.id);
                    setDataSource(Outer+musicBean.id);
                    setOnPreparedListener(mp -> {
                        prepared=true;
                        player.start();
                    });
                    prepareAsync();
                    MyApplicationImpl myApplication=MyApplicationImpl.myApplication;
                    myApplication.setControlInfo(musicBean.id,musicBean.name,musicBean.author,myApplication.handler);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    public void operateAfterPrepared(OnPreparedListener listener){
        Thread thread=new Thread(){
            @Override
            public void run() {
                while (!prepared){
                    try {
                        sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if(listener!=null)
                    listener.onPrepared(MusicPlayer.this);
            }
        };
        thread.start();

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
}
