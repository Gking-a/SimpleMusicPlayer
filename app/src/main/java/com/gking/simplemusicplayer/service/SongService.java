package com.gking.simplemusicplayer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.widget.RemoteViews;

import com.gking.simplemusicplayer.R;
import com.gking.simplemusicplayer.activity.SongActivity;
import com.gking.simplemusicplayer.impl.MusicPlayer;
import com.gking.simplemusicplayer.impl.MyApplicationImpl;
import com.gking.simplemusicplayer.manager.SongBean;
import com.gking.simplemusicplayer.util.Util;

import static com.gking.simplemusicplayer.service.BackgroundService.Type;

public class SongService extends Service {
    //Design as SongActivity
    public static final String TAG="service.SongService";
    public static final int NOTIFICATION_ID=0x454640;
    public MusicPlayer musicPlayer;
    RemoteViews smallView;
    SongBean song;
    private Notification notification;
    private RemoteViews bigView;
    private Thread timeThread;

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
        bigView = new RemoteViews(getPackageName(), R.layout.notification_big);
        notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContent(smallView)
                .setPriority(Notification.PRIORITY_MAX)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .build();
        notification.contentView=smallView;
        notification.bigContentView=bigView;
        musicPlayer= ((MyApplicationImpl) getApplication()).mMusicPlayer;
        loadView0();
        musicPlayer.addOnSongBeanChangeListener((musicPlayer, songBean) -> {
            this.song=songBean;
            loadView1();
            loadView2();
        });
        timeThread = new TimeThread();
        timeThread.start();
        //巨坑！如果在配置之前startForeground,就会出现Pending Intent无效的现象
        startForeground(NOTIFICATION_ID, notification);
        return super.onStartCommand(intent, flags, startId);
    }
    class TimeThread extends Thread{
        @Override
        public final void run() {
            while (!interrupted()){
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    break;
                }
                if (song != null) {
                    bigView.setTextViewText(R.id.notification_time,time2str(musicPlayer.getCurrentPosition()));
                    bigView.setProgressBar(R.id.notification_progress,musicPlayer.getDuration(),musicPlayer.getCurrentPosition(),false);
                    NotificationManager manager= ((NotificationManager) getSystemService(NOTIFICATION_SERVICE));
                    manager.notify(NOTIFICATION_ID,notification);
                }
            }
        }
    }
    private void loadView0() {
        Intent i=new Intent(this,BackgroundService.class);
        i.putExtra(Type.Type,Type.Pause);
        smallView.setOnClickPendingIntent(R.id.notification_pause,PendingIntent.getService(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT));
        bigView.setOnClickPendingIntent(R.id.notification_pause,PendingIntent.getService(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT));
        Intent i2=new Intent(this,BackgroundService.class);
        i2.putExtra(Type.Type,Type.Window);
        smallView.setOnClickPendingIntent(R.id.notification_window,PendingIntent.getService(this,1,i2,PendingIntent.FLAG_UPDATE_CURRENT));
        bigView.setOnClickPendingIntent(R.id.notification_window,PendingIntent.getService(this,1,i2,PendingIntent.FLAG_UPDATE_CURRENT));
        Intent i3=new Intent(this,BackgroundService.class);
        i3.putExtra(Type.Type,Type.Last);
        bigView.setOnClickPendingIntent(R.id.notification_last,PendingIntent.getService(this,2,i3,PendingIntent.FLAG_UPDATE_CURRENT));
        Intent i4=new Intent(this,BackgroundService.class);
        i4.putExtra(Type.Type,Type.Next);
        bigView.setOnClickPendingIntent(R.id.notification_next,PendingIntent.getService(this,3,i4,PendingIntent.FLAG_UPDATE_CURRENT));
    }
    private void loadView1() {
        Util.getCover(song.coverUrl, bitmap -> {
            smallView.setImageViewBitmap(R.id.notification_cover, bitmap);
            bigView.setImageViewBitmap(R.id.notification_cover, bitmap);
        });
        smallView.setTextViewText(R.id.notification_title,song.name);
        smallView.setTextViewText(R.id.notification_author,song.author);
        bigView.setTextViewText(R.id.notification_title,song.name);
        bigView.setTextViewText(R.id.notification_author,song.author);
    }
    private void loadView2() {
        bigView.setTextViewText(R.id.notification_duration,time2str(musicPlayer.getDuration()));
        bigView.setProgressBar(R.id.notification_progress,musicPlayer.getDuration(),0,true);
        NotificationManager manager= ((NotificationManager) getSystemService(NOTIFICATION_SERVICE));
        manager.notify(NOTIFICATION_ID,notification);
    }
    public static String time2str(int msec){
        return SongActivity.time2str(msec);
    }

}