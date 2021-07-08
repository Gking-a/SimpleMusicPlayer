package com.gking.simplemusicplayer.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.widget.RemoteViews;

import com.gking.simplemusicplayer.R;
import com.gking.simplemusicplayer.impl.MusicPlayer;

public class SongService extends Service {
    //Design as SongActivity
    public static final String TAG="service.SongService";
    public MusicPlayer musicPlayer;
    RemoteViews remoteViews;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        PowerManager.WakeLock wakeLock= ((PowerManager) getSystemService(POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,TAG);
//        wakeLock.acquire();
//        String id=intent.getStringExtra("id");
        remoteViews=new RemoteViews(getPackageName(), R.layout.notification);
        return super.onStartCommand(intent, flags, startId);
    }
}