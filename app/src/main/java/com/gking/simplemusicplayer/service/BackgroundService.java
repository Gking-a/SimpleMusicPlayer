package com.gking.simplemusicplayer.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.gking.simplemusicplayer.impl.MusicPlayer;
import com.gking.simplemusicplayer.impl.MyApplicationImpl;

import static com.gking.simplemusicplayer.service.BackgroundService.Type.*;

public class BackgroundService extends Service {
    MusicPlayer musicPlayer;
    @Override
    public void onCreate() {
        super.onCreate();
        musicPlayer= ((MyApplicationImpl) getApplication()).mMusicPlayer;
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int type;
        if(intent!=null&&(type=intent.getIntExtra(Type,-1))>=0){
            if(type==Pause)musicPlayer.pause();
            if(type==Next)musicPlayer.next(null);
            if(type==Last)musicPlayer.last(null);
            if(type==Window)window();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void window() {

    }

    public static class Type{
        public static final String Type="type";
        public static final int Pause=0;
        public static final int Last=1;
        public static final int Next=2;
        public static final int Window=3;
    }
}