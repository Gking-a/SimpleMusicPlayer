package com.gking.simplemusicplayer.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;

public class MusicService extends Service {
    public static final String TAG="service.MusicService";
    public MusicService() {
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        PowerManager.WakeLock wakeLock= ((PowerManager) getSystemService(POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,TAG);
        wakeLock.acquire();
        return super.onStartCommand(intent, flags, startId);
    }

}