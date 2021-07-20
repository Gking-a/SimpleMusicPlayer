package com.gking.simplemusicplayer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.gking.simplemusicplayer.R;
import com.gking.simplemusicplayer.activity.SongActivity;
import com.gking.simplemusicplayer.activity.SettingsActivity;
import com.gking.simplemusicplayer.impl.MusicPlayer;
import com.gking.simplemusicplayer.impl.MyApplicationImpl;
import com.gking.simplemusicplayer.manager.LyricBean;
import com.gking.simplemusicplayer.manager.LyricManager;
import com.gking.simplemusicplayer.manager.SongBean;
import com.gking.simplemusicplayer.util.ControlableThread;
import com.gking.simplemusicplayer.util.Util;

import static com.gking.simplemusicplayer.service.BackgroundService.Type;
import static com.gking.simplemusicplayer.service.BackgroundService.isShowing;
import static com.gking.simplemusicplayer.activity.SettingsActivity.Params.PLAY_MODE;
import static java.lang.Thread.sleep;

public class SongService extends Service {
    //Design as SongActivity
    public static final String TAG="service.SongService";
    public static final int NOTIFICATION_ID=0x454640;
    public MusicPlayer musicPlayer;
    RemoteViews smallView;
    SongBean song;
    private Notification notification;
    private RemoteViews bigView;
    private TimeRunnable timeRunnable;
    private MusicPlayer.OnSongBeanChangeListener onSongBeanChangeListener;
    private ControlableThread controlableThread;

    @Override
    public void onCreate() {
        super.onCreate();
        MyApplicationImpl application = (MyApplicationImpl) getApplication();
        registerReceiver(application.myBroadcastReceiver,application.intentFilter);
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
        timeRunnable = new TimeRunnable();
        controlableThread = new ControlableThread(timeRunnable);
        controlableThread.setSuspend(true);
        controlableThread.start();
        onSongBeanChangeListener=new MusicPlayer.OnSongBeanChangeListener() {
            boolean p=false,l=false;
            @Override
            public void onSongBeanChange(MusicPlayer musicPlayer, SongBean songBean) {
                song=songBean;
            }
            @Override
            public void onPrepared(MusicPlayer musicPlayer) {
                loadView1();
                loadView2();
                p=true;
                controlableThread.setSuspend(p&&l);
            }
            @Override
            public void onLyricLoaded(MusicPlayer musicPlayer, LyricBean lyricBean, LyricManager lyricManager) {
                l=true;
                controlableThread.setSuspend(p&&l);
            }
            @Override
            public void onFinish(MusicPlayer musicPlayer) {
                p=false;
                l=false;
                controlableThread.setSuspend(p&&l);
            }
        };
        musicPlayer.addOnSongBeanChangeListener(onSongBeanChangeListener);
        //巨坑！如果在配置之前startForeground,就会出现Pending Intent无效的现象
        startForeground(NOTIFICATION_ID, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        PowerManager.WakeLock wakeLock= ((PowerManager) getSystemService(POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,TAG);
//        wakeLock.acquire();
//        String id=intent.getStringExtra("id");
        musicPlayer.notify(song,onSongBeanChangeListener);
        if(intent.getStringExtra("changeMode")!=null){
            changeMode();
        }
        return super.onStartCommand(intent, flags, startId);
    }
    class TimeRunnable implements Runnable{
        Runnable lyricRunnable = () -> {
            String lyric = LyricManager.Instance.getLyric(musicPlayer.getCurrentPosition());
            if(lyric==null)return;
            TextView textView= ((MyApplicationImpl) getApplication()).windowView.findViewById(R.id.window_lyric);
            textView.setText(lyric);
        };
        @Override
        public final void run() {
            try {
                sleep(150);
            } catch (InterruptedException e) {
            }
            if (song != null) {
                bigView.setTextViewText(R.id.notification_time, time2str(musicPlayer.getCurrentPosition()));
                bigView.setProgressBar(R.id.notification_progress, musicPlayer.getDuration(), musicPlayer.getCurrentPosition(), false);
                changeModeView();
                NotificationManager manager = ((NotificationManager) getSystemService(NOTIFICATION_SERVICE));
                manager.notify(NOTIFICATION_ID, notification);
                if (isShowing) {
                    MyApplicationImpl.handler.post(lyricRunnable);
                }
            }
        }
    }
    private void loadView0() {
        Intent i=new Intent(this,BackgroundService.class);
        i.putExtra(Type.Type,Type.Pause);
        smallView.setOnClickPendingIntent(R.id.notification_pause,PendingIntent.getService(this,0,i,PendingIntent.FLAG_CANCEL_CURRENT));
        bigView.setOnClickPendingIntent(R.id.notification_pause,PendingIntent.getService(this,0,i,PendingIntent.FLAG_CANCEL_CURRENT));
        Intent i2=new Intent(this,BackgroundService.class);
        i2.putExtra(Type.Type,Type.Window);
        smallView.setOnClickPendingIntent(R.id.notification_window,PendingIntent.getService(this,1,i2,PendingIntent.FLAG_CANCEL_CURRENT));
        bigView.setOnClickPendingIntent(R.id.notification_window,PendingIntent.getService(this,1,i2,PendingIntent.FLAG_CANCEL_CURRENT));
        Intent i3=new Intent(this,BackgroundService.class);
        i3.putExtra(Type.Type,Type.Last);
        bigView.setOnClickPendingIntent(R.id.notification_last,PendingIntent.getService(this,2,i3,PendingIntent.FLAG_CANCEL_CURRENT));
        Intent i4=new Intent(this,BackgroundService.class);
        i4.putExtra(Type.Type,Type.Next);
        bigView.setOnClickPendingIntent(R.id.notification_next,PendingIntent.getService(this,3,i4,PendingIntent.FLAG_CANCEL_CURRENT));
        i=new Intent(this,SongService.class);
        i.putExtra("chageMode"," ");
        bigView.setOnClickPendingIntent(R.id.notification_mode,PendingIntent.getService(this,4,i,PendingIntent.FLAG_CANCEL_CURRENT));
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
        changeModeView();
        bigView.setTextViewText(R.id.notification_duration,time2str(musicPlayer.getDuration()));
        bigView.setProgressBar(R.id.notification_progress,musicPlayer.getDuration(),0,true);
        NotificationManager manager= ((NotificationManager) getSystemService(NOTIFICATION_SERVICE));
        manager.notify(NOTIFICATION_ID,notification);
    }
    public static String time2str(int msec){
        return SongActivity.time2str(msec);
    }
    public void changeMode(){
        String mode=SettingsActivity.get(SettingsActivity.Params.play_mode);
        if(mode.equals(PLAY_MODE.ORDER))SettingsActivity.set(SettingsActivity.Params.play_mode,0);
        else SettingsActivity.set(SettingsActivity.Params.play_mode,Integer.parseInt(mode)+1);
        changeModeView();
    }
    public void changeModeView(){
        String mode=SettingsActivity.get(SettingsActivity.Params.play_mode);
        int bitmap_res = R.drawable.close;
        if(mode.equals(PLAY_MODE.NONE))bitmap_res=R.drawable.close;
        if(mode.equals(PLAY_MODE.LOOP))bitmap_res=R.drawable.loop;
        if(mode.equals(PLAY_MODE.RANDOM))bitmap_res=R.drawable.random;
        if(mode.equals(PLAY_MODE.ORDER))bitmap_res=R.drawable.order;
        bigView.setImageViewResource(R.id.notification_mode,bitmap_res);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(((MyApplicationImpl) getApplication()).myBroadcastReceiver);
        controlableThread.interrupt();
        musicPlayer.removeOnSongBeanChangeListener(onSongBeanChangeListener);
        stopForeground(true);
    }
}
