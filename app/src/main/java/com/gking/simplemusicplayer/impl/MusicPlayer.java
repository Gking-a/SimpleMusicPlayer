package com.gking.simplemusicplayer.impl;

import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;

import com.gking.simplemusicplayer.activity.MySettingsActivity;
import com.gking.simplemusicplayer.manager.SongBean;
import com.gking.simplemusicplayer.manager.SongManager;
import com.gking.simplemusicplayer.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.gking.simplemusicplayer.activity.MySettingsActivity.get;
import static com.gking.simplemusicplayer.impl.MyApplicationImpl.handler;
import static com.gking.simplemusicplayer.impl.MyApplicationImpl.myApplication;
import static com.gking.simplemusicplayer.activity.MySettingsActivity.Params;
import static com.gking.simplemusicplayer.activity.MySettingsActivity.Params.PLAY_MODE;

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
            //可以尝试解耦,将music player提取出来
            boolean auto=Boolean.parseBoolean(MySettingsActivity.get(MySettingsActivity.Params.auto_next));
            if(auto&&musicBean!=null){
                String pm = get(Params.play_mode);
                switch (pm){
                    case PLAY_MODE.NONE:break;
                    case PLAY_MODE.LOOP:start(musicBean,true);
                    case PLAY_MODE.RANDOM:start(SongManager.getInstance().getRandomSong(musicBean.id),true);
                    case PLAY_MODE.ORDER:next(null);
                }
            }
        }));
    }
    public SongBean getMusicBean() {
        return musicBean;
    }

    public void start(SongBean songBean,OnPreparedListener listener){
        start(songBean);
    }
    private void start(SongBean musicBean){
        start(musicBean,false);
    }
    public void start(SongBean musicBean,boolean focus){
        if (!focus&&this.musicBean != null) {
            if(musicBean.id.equals(this.musicBean.id))return;
        }
        this.musicBean=musicBean;
        for (OnSongBeanChangeListener listener:onSongBeanChangeListenerList) {
            listener.onSongBeanChange(player,musicBean);
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
    //onSongBeanChange then onAfterPrepare
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
    public int getDuration() {
        try{
            return super.getDuration();
        }catch (Exception e){
            e.printStackTrace();
            return 1;
        }
        
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
    public int getCurrentPosition() {
        try {
            return super.getCurrentPosition();
        }catch (Exception e){
            return 0;
        }
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
        if(musicBean!=null)
        start(musicBean.next,true);
    }
    public void next(OnPreparedListener onPreparedListener,boolean focus){

    }
    public void last(OnPreparedListener onPreparedListener) {
        if(musicBean!=null)
        start(musicBean.last,true);
    }
    private List<OnSongBeanChangeListener> onSongBeanChangeListenerList=new LinkedList<>();

    public void addOnSongBeanChangeListener(OnSongBeanChangeListener onSongBeanChangeListener) {
        onSongBeanChangeListenerList.add(onSongBeanChangeListener);
        if (musicBean != null) {
            onSongBeanChangeListener.onSongBeanChange(this,musicBean);
        }
    }
    public void removeOnSongBeanChangeListener(OnSongBeanChangeListener onSongBeanChangeListener){
        onSongBeanChangeListenerList.remove(onSongBeanChangeListener);
    }
    public interface OnSongBeanChangeListener{
        void onSongBeanChange(MusicPlayer musicPlayer, SongBean songBean);
    }
}
