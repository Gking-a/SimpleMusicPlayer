package com.gking.simplemusicplayer.impl;

import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;

import com.gking.simplemusicplayer.activity.MySettingsActivity;
import com.gking.simplemusicplayer.manager.SongBean;
import com.gking.simplemusicplayer.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.gking.simplemusicplayer.impl.MyApplicationImpl.handler;
import static com.gking.simplemusicplayer.impl.MyApplicationImpl.myApplication;

public class MusicPlayer extends MediaPlayer {
    public static final String Outer="http://music.163.com/song/media/outer/url?id=";
    public MusicPlayer player=this;
    private boolean prepared=false;
    private boolean lockProgress=false;
    public void setLockProgress(boolean lockProgress) {
        this.lockProgress = lockProgress;
    }
    private SongBean musicBean=null;
    MusicPlayer(){
        super();
        setOnErrorListener((mp, what, extra) -> true);
        setOnCompletionListener((mp -> {
            boolean auto=Boolean.parseBoolean(MySettingsActivity.get(MySettingsActivity.Params.auto_next));
            if(auto)next(null);
        }));
    }
    public SongBean getMusicBean() {
        return musicBean;
    }

    public void start(SongBean songBean,OnPreparedListener listener){
        start(songBean);
    }
    public void start(SongBean musicBean){
        if (this.musicBean != null) {
            if(musicBean.id.equals(this.musicBean.id))return;
        }
        this.musicBean=musicBean;
        for (OnSongBeanChangeListener listener:onSongBeanChangeListenerList) {
            listener.onSongBeanChange(this,this.musicBean);
        }
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
                        handler.post(() -> myApplication.controlPanel.setVisibility(View.VISIBLE));
                    });
                    prepareAsync();
                    MyApplicationImpl myApplication= MyApplicationImpl.myApplication;
                    handler.post(()->{
                       myApplication.Name.setText(musicBean.name);
                       myApplication.Author.setText(musicBean.author);
                    });
                    Util.getCover(musicBean.coverUrl, bitmap -> handler.post(() -> myApplication.Cover.setImageBitmap(bitmap)));
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
        MyApplicationImpl.handler.post(()->{
            MyApplicationImpl myApplication= MyApplicationImpl.myApplication;
            myApplication.controlPanel.setVisibility(View.GONE);
        });
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
        try {
            if(!lockProgress)
                super.seekTo(msec);
        }catch (Exception e){ }
    }

    public void next(OnPreparedListener onPreparedListener) {
        start(musicBean.next,onPreparedListener);
    }
    public void last(OnPreparedListener onPreparedListener) {
        start(musicBean.last,onPreparedListener);
    }
    private List<OnSongBeanChangeListener> onSongBeanChangeListenerList=new LinkedList<>();

    public void setOnSongBeanChangeListener(OnSongBeanChangeListener onSongBeanChangeListener) {
        onSongBeanChangeListenerList.add(onSongBeanChangeListener);
        if (musicBean != null) {
            onSongBeanChangeListener.onSongBeanChange(this,musicBean);
        }
    }

    public interface OnSongBeanChangeListener{
        void onSongBeanChange(MusicPlayer musicPlayer, SongBean songBean);
    }
}
