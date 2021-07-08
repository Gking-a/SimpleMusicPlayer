package com.gking.simplemusicplayer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.gking.simplemusicplayer.R;
import com.gking.simplemusicplayer.impl.MusicPlayer;
import com.gking.simplemusicplayer.impl.MyApplicationImpl;
import com.gking.simplemusicplayer.manager.SongBean;
import com.gking.simplemusicplayer.util.Util;

public class SongService extends Service {
    //Design as SongActivity
    public static final String TAG="service.SongService";
    public static final int NOTIFICATION_ID=0x454640;
    public MusicPlayer musicPlayer;
    RemoteViews smallView;
    SongBean song;
    private Notification notification;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        PowerManager.WakeLock wakeLock= ((PowerManager) getSystemService(POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,TAG);
//        wakeLock.acquire();
//        String id=intent.getStringExtra("id");
        smallView =new RemoteViews(getPackageName(), R.layout.notification_small);
        notification = new Notification.Builder(this)
                .setContent(smallView)
                .setPriority(Notification.PRIORITY_MAX)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .build();
        notification.contentView=smallView;
        startForeground(NOTIFICATION_ID, notification);
        musicPlayer= ((MyApplicationImpl) getApplication()).mMusicPlayer;
        musicPlayer.setOnSongBeanChangeListener((musicPlayer, songBean) -> {
            this.song=songBean;
            method1();
        });
        method1();
        return super.onStartCommand(intent, flags, startId);
    }
    private void method1() {
        Util.getCover(song.coverUrl, bitmap -> smallView.setImageViewBitmap(R.id.notification_cover,bitmap));
        smallView.setTextViewText(R.id.notification_title,song.name);
        smallView.setTextViewText(R.id.notification_author,song.author);
        Intent i=new Intent(this,BackgroundService.class);
        i.putExtra("type","pause");
        smallView.setOnClickPendingIntent(R.id.notification_pause,PendingIntent.getService(this,0,i,PendingIntent.FLAG_NO_CREATE));
        NotificationManager manager= ((NotificationManager) getSystemService(NOTIFICATION_SERVICE));
        manager.notify(NOTIFICATION_ID,notification);
    }
}