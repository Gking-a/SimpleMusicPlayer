package com.gking.simplemusicplayer.impl;

import android.media.MediaPlayer;
import android.view.View;

import com.gking.simplemusicplayer.activity.SettingsActivity;
import com.gking.simplemusicplayer.manager.LyricBean;
import com.gking.simplemusicplayer.manager.LyricManager;
import com.gking.simplemusicplayer.manager.SongBean;
import com.gking.simplemusicplayer.manager.SongManager;
import com.gking.simplemusicplayer.util.Util;
import com.gking.simplemusicplayer.util.WebRequest;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.gking.simplemusicplayer.activity.SettingsActivity.get;
import static com.gking.simplemusicplayer.impl.MyApplicationImpl.handler;
import static com.gking.simplemusicplayer.impl.MyApplicationImpl.myApplication;
import static com.gking.simplemusicplayer.activity.SettingsActivity.Params;
import static com.gking.simplemusicplayer.activity.SettingsActivity.Params.PLAY_MODE;

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
            autoNext();
        }));
    }
    private void autoNext() {
        boolean auto=Boolean.parseBoolean(SettingsActivity.get(Params.auto_next));
        if(auto&&musicBean!=null){
            String pm = get(Params.play_mode);
            if(pm.equals(PLAY_MODE.NONE)){}
            else if (pm.equals(PLAY_MODE.LOOP)){start(musicBean,true);}
            else if (pm.equals(PLAY_MODE.RANDOM)){start(SongManager.getInstance().getRandomSong(musicBean.id).next,true);}
            else if (pm.equals(PLAY_MODE.ORDER)){next(null);}
        }
    }
    public void notify(SongBean songBean,OnSongBeanChangeListener onSongBeanChangeListener){
        if(getMusicBean()!=songBean)
            onSongBeanChangeListener.onSongBeanChange(this,getMusicBean());
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
        if (musicBean == null)return;
        if (!focus&&this.musicBean != null) {
            if(musicBean.id.equals(this.musicBean.id))return;
        }
        this.musicBean=musicBean;
        for (OnSongBeanChangeListener listener:onSongBeanChangeListenerList) {
            if(listener!=null) listener.onSongBeanChange(player,musicBean);
        }
        Runnable runnable = () -> {
            try {
                try {
                    player.stop();
                } catch (Exception e) {
                }
                try {
                    reset();
                } catch (Exception e) {
                }
                System.out.println(musicBean.id);
                WebRequest.check_music(musicBean.id, new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String body = response.body().string();
                        JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
                        JsonElement jsonElement = jsonObject.get("data").getAsJsonArray().get(0).getAsJsonObject().get("url");
                        System.out.println(musicBean.id);
                        System.out.println(musicBean.next.id);
                        if (jsonElement.isJsonNull()) {
                            System.out.println("is null");
                            autoNext();
                        } else {
                                setDataSource(jsonElement.getAsString());
                                prepareAsync();
                            WebRequest.lyric(musicBean.id, MyCookieJar.getLoginCookie(), new Callback() {
                                @Override
                                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                }

                                @Override
                                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                    String body = response.body().string();
                                    JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
                                    JsonElement noLyric = jsonObject.get("nolyric");
                                    LyricBean lyricBean;
                                    if (noLyric != null) {
                                        lyricBean = new LyricBean();
                                    } else {
                                        lyricBean = new LyricBean(jsonObject);
                                    }
                                    LyricManager instance = LyricManager.getInstance(lyricBean);
                                    for (OnSongBeanChangeListener listener : onSongBeanChangeListenerList) {
                                        if(listener!=null)listener.onLyricLoaded(player, lyricBean, instance);
                                    }
                                }
                            });
                        }
                    }
                });
                setOnPreparedListener(mp -> {
                    for (OnSongBeanChangeListener listener : onSongBeanChangeListenerList) {
                        if(listener!=null)listener.onPrepared(player);
                    }
                    player.start();
                    handler.post(() -> myApplication.controlPanel.setVisibility(View.VISIBLE));
                });
                MyApplicationImpl myApplication = MyApplicationImpl.myApplication;
                handler.post(() -> {
                    myApplication.Name.setText(musicBean.name);
                    myApplication.Author.setText(musicBean.author);
                });
                Util.getCover(musicBean.coverUrl, bitmap -> handler.post(() -> myApplication.Cover.setImageBitmap(bitmap)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        new Thread(runnable).start();
    }
    @Override
    public int getDuration() {
        try{
            return super.getDuration();
        }catch (Exception e){
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
        void onPrepared(MusicPlayer musicPlayer);
        void onLyricLoaded(MusicPlayer musicPlayer, LyricBean lyricBean, LyricManager lyricManager);
    }
}
