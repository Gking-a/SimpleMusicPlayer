package com.gking.simplemusicplayer.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;

import com.gking.simplemusicplayer.impl.MusicPlayer;

public class MusicService extends Service {
    public static final String TAG="service.MusicService";
    public static MusicPlayer musicPlayer;
    public MusicService() {
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    //intent传入id
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        PowerManager.WakeLock wakeLock= ((PowerManager) getSystemService(POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,TAG);
//        wakeLock.acquire();
//        String id=intent.getStringExtra("id");
        return super.onStartCommand(intent, flags, startId);
    }
}