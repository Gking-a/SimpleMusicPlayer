package com.gking.simplemusicplayer.impl;

import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;

import com.gking.simplemusicplayer.activity.SettingsActivity;
import com.gking.simplemusicplayer.manager.LyricBean;
import com.gking.simplemusicplayer.manager.SongBean;
import com.gking.simplemusicplayer.manager.SongManager;
import com.gking.simplemusicplayer.util.MyCookies;
import com.gking.simplemusicplayer.util.Util;
import com.gking.simplemusicplayer.util.WebRequest;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.gking.simplemusicplayer.activity.SettingsActivity.Params.local_download;
import static com.gking.simplemusicplayer.activity.SettingsActivity.get;
import static com.gking.simplemusicplayer.impl.MyApplicationImpl.handler;
import static com.gking.simplemusicplayer.impl.MyApplicationImpl.application;
import static com.gking.simplemusicplayer.activity.SettingsActivity.Params;
import static com.gking.simplemusicplayer.activity.SettingsActivity.Params.PLAY_MODE;

public class MusicPlayer extends MediaPlayer{
    public static final String Outer="http://music.163.com/song/media/outer/url?id=";
    ScheduledExecutorService scheduledExecutorService= Executors.newScheduledThreadPool(1);
    public MusicPlayer player=this;
    private boolean lockProgress=false;
    public void setLockProgress(boolean lockProgress) {
        this.lockProgress = lockProgress;
    }
    private SongBean musicBean=null;
    private boolean prepared=false,lyricLoaded=false;
    int lyricPosition=0;
    String lyricString;
    MusicPlayer(){
        super();
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if(musicBean==null)return;
                LyricBean lyricBean=musicBean.lyric;
                if(lyricBean==null)return;
                int progress=getCurrentPosition();
                int p=getPosition(progress,lyricBean);
                if(lyricPosition==p)return;
                lyricPosition=p;
                lyricString = this.getLyric(p, lyricBean);
                try{
                    for (OnSongBeanChangeListener listener:onSongBeanChangeListenerList) {
                        if(listener!=null) listener.onLyricChange(player,p,lyricString);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
            private int getPosition(int msec,LyricBean lyricBean){
                if(lyricBean==null)return -1;
                if(lyricBean.nolyric)return -1;
                LinkedList<Integer> time = lyricBean.time;
                for (int i = 0; i < time.size(); i++) {
                    if(i==time.size()-1)return i;
                    int last=time.get(i);
                    int next=time.get(i+1);
                    if(last<=msec&&msec<=next)return i-1;
                }
                return time.size();
            }
            private String getLyric(int position,LyricBean lyricBean){
                if(lyricBean==null)return null;
                if(lyricBean.nolyric)return null;
                if(position<0)return null;
                if (position>=lyricBean.lyric.size())return null;
                return lyricBean.lyric.get(position);
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
        setOnErrorListener((mp, what, extra) -> true);
        setOnCompletionListener((mp -> {
            //可以尝试解耦,将music player提取出来
            for (OnSongBeanChangeListener listener:onSongBeanChangeListenerList) {
                if(listener!=null) listener.onFinish(player);
            }
            autoNext();
        }));
        application.requestFocus();
    }
    public void forceLast(){
        String pm = get(Params.play_mode);
        if(pm.equals(PLAY_MODE.NONE)){}
        else if (pm.equals(PLAY_MODE.LOOP)){start(musicBean,true);}
        else if (pm.equals(PLAY_MODE.RANDOM)){start(SongManager.getInstance().getRandomSong0(musicBean.id).last,true); }
        else if (pm.equals(PLAY_MODE.ORDER)){start(SongManager.getInstance().getLastOrderSong(musicBean.id),true);}
    }
    public void forceNext(){
        String pm = get(Params.play_mode);
        Log.e("smode",pm);
        Log.e("next",musicBean.next.name);
        if(pm.equals(PLAY_MODE.NONE)){}
        else if (pm.equals(PLAY_MODE.LOOP)){start(musicBean,true);}
        else if (pm.equals(PLAY_MODE.RANDOM)){
            start(SongManager.getInstance().getRandomSong0(musicBean.id).next,true);
        }
        else if (pm.equals(PLAY_MODE.ORDER)){start(SongManager.getInstance().getNextOrderSong(musicBean.id),true);}
    }
    public void autoNext() {
        boolean auto=Boolean.parseBoolean(SettingsActivity.get(Params.auto_next));
        if(auto&&musicBean!=null){
            String pm = get(Params.play_mode);
            start(SongManager.songManager.nextValidSong(pm,musicBean),true);
        }
    }
    public void notify(SongBean songBean,OnSongBeanChangeListener onSongBeanChangeListener){
        if(musicBean==null)return;
        if(getMusicBean()!=songBean)
            onSongBeanChangeListener.onSongBeanChange(this,getMusicBean());
        if(prepared)onSongBeanChangeListener.onPrepared(this);
        if(musicBean.lyric!=null)onSongBeanChangeListener.onLyricLoaded(player,musicBean.lyric);
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
                stop();
                reset();
                System.out.println(musicBean.id);
                File file = new File(SettingsActivity.get(local_download), musicBean.id + ".mp3");
                if(file.exists()){
                    setDataSource(file.getAbsolutePath());
                    setOnPreparedListener(mp -> {
                        for (OnSongBeanChangeListener listener : onSongBeanChangeListenerList) {
                            if(listener!=null)listener.onPrepared(player);
                        }
                        prepared=true;
                        player.start();
                        handler.post(() -> application.controlPanel.setVisibility(View.VISIBLE));
                    });
                    prepareAsync();
                    requestLyric(musicBean);
                    return;
                }
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
                        if (jsonElement.isJsonNull()) {
                            System.out.println("is NULL");
                            SongManager.getInstance().markReachable(musicBean.id, false);
                            autoNext();
                        } else {
                            SongManager.getInstance().markReachable(musicBean.id, true);
                            String urlwithhouzhui = jsonElement.getAsString();
                            Matcher matcher = Pattern.compile("(.*?)\\.mp3?(.*?)").matcher(urlwithhouzhui);
                            matcher.find();
                            System.out.println(matcher.group(1)+".mp3");
                            setDataSource((matcher.group(1)+".mp3"));
                            prepareAsync();
                            requestLyric(musicBean);
                        }
                    }
                });
                setOnPreparedListener(mp -> {
                    for (OnSongBeanChangeListener listener : onSongBeanChangeListenerList) {
                        if(listener!=null)listener.onPrepared(player);
                    }
                    prepared=true;
                    player.start();
                    handler.post(() -> application.controlPanel.setVisibility(View.VISIBLE));
                });
                MyApplicationImpl myApplication = MyApplicationImpl.application;
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

    private void requestLyric(SongBean musicBean) {
        WebRequest.lyric(musicBean.id,  new Callback() {
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
                musicBean.lyric = lyricBean;
                for (OnSongBeanChangeListener onSongBeanChangeListener : onSongBeanChangeListenerList) {
                    if (onSongBeanChangeListener != null)
                        onSongBeanChangeListener.onLyricLoaded(player, lyricBean);
                }
            }
        });
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
        prepared=false;
        lyricLoaded=false;
        super.reset();
        MyApplicationImpl.handler.post(()->{
            MyApplicationImpl myApplication= MyApplicationImpl.application;
            myApplication.controlPanel.setVisibility(View.GONE);
        });
    }
    @Override
    public void stop(){
        try { super.stop(); }catch (Exception e){ }
    }
    @Override
    public void start(){
        try { super.start();}catch (Exception e){ }
    }
    @Override
    public int getCurrentPosition() {
        try {
            return super.getCurrentPosition();
        }catch (Exception e){
            return 0;
        }
    }
    public void play(){
        if(isPlaying()){
            super.pause();
        }else {
            start();
        }
    }
    public void changeP(){
        if(isPlaying())pause();
        else play();
    }
    @Override
    public void pause() throws IllegalStateException {
        try {
            super.pause();
        }catch (Exception e){}
    }
    @Override
    public void seekTo(int msec) throws IllegalStateException {
        try {
            if(!lockProgress)
                super.seekTo(msec);
        }catch (Exception e){ }
    }
    @Deprecated
    public void next(OnPreparedListener onPreparedListener) {
        if(musicBean!=null)
            start(musicBean.next,true);
    }
    @Deprecated
    public void next(OnPreparedListener onPreparedListener,boolean focus){

    }
    @Deprecated
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
        void onFinish(MusicPlayer musicPlayer);
        void onLyricLoaded(MusicPlayer musicPlayer,LyricBean lyricBean);
        void onLyricChange(MusicPlayer musicPlayer,int position,String lyric);
    }

    public int getLyricPosition() {
        return lyricPosition;
    }

    public String getLyricString() {
        return lyricString;
    }
}