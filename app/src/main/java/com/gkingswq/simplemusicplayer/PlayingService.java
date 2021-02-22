package com.gkingswq.simplemusicplayer;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.gkingswq.simplemusicplayer.Interface.OnGetNameCompile;
import com.gkingswq.simplemusicplayer.impl.MyApplicationImpl;
//import com.rey.material.widget.Slider;

import java.io.File;
import java.util.ArrayList;

import GTools.Collections.MyCollection1;
import GTools.GLibrary;
import GTools.GLibraryManager;
import com.gkingswq.simplemusicplayer.util.GSong;
import com.gkingswq.simplemusicplayer.util.JSON;


import static com.gkingswq.simplemusicplayer.impl.MyApplicationImpl.mA;
import static com.gkingswq.simplemusicplayer.impl.MyApplicationImpl.mB;
import static com.gkingswq.simplemusicplayer.impl.MyApplicationImpl.mC;
import static com.gkingswq.simplemusicplayer.impl.MyApplicationImpl.mNotificationManager;
import static com.gkingswq.simplemusicplayer.impl.MyApplicationImpl.seekBar;
import static com.gkingswq.simplemusicplayer.impl.MyApplicationImpl.t1;
import static com.gkingswq.simplemusicplayer.impl.MyApplicationImpl.t2;
import static com.gkingswq.simplemusicplayer.impl.MyApplicationImpl.t3;
import static com.gkingswq.simplemusicplayer.impl.MyApplicationImpl.t4;
import static com.gkingswq.simplemusicplayer.Value.Flags.*;
import static com.gkingswq.simplemusicplayer.Value.IntentKeys.*;
import static com.gkingswq.simplemusicplayer.Value.StringPool.*;
import static com.gkingswq.simplemusicplayer.Value.Actions.*;
import static com.gkingswq.simplemusicplayer.Value.Settings.*;
import static com.gkingswq.simplemusicplayer.Value.Files.*;
import static com.gkingswq.simplemusicplayer.impl.MyApplicationImpl.mHandler;
public class PlayingService extends Service {
    public static MediaPlayer mp = new MediaPlayer();
    private Thread LoadMusic;
    static String id;
    static ArrayList<String> ids;
    static int mflag;
    static int randomposition;
    PowerManager.WakeLock wakeLock;
    static Notification n;
    public static RemoteViews remoteViews;
    public static MediaPlayer.OnCompletionListener myOnCompletionListener;
    @Override
    public IBinder onBind(Intent paramIntent) {
        return null;
    }
    @Override
    public void onDestroy() {
        stopService(new Intent(PlayingService.this, MiniWindow.class));
        stopThread(TimeThread);
        stopThread(MainThread);
        mHandler.post(new MainActivity.MyRunnable1(""));
        seekBar.setOnSeekBarChangeListener(null);
//        slider.setOnPositionChangeListener(null);
        mp.stop();
        mp.reset();
        mp=null;
        stopForeground(true);
        this.wakeLock.release();
        mNotificationManager.cancel(3);
        n=null;
        remoteViews=null;
        myOnCompletionListener=null;
        super.onDestroy();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        stopService(new Intent(this, ServiceA.class));
        stopService(new Intent(this, MiniWindow.class));
        if (mp == null) {
            mp = new MediaPlayer();
            mp.setLooping(false);
        }
        mp.stop();
        wakeLock = ((PowerManager) getSystemService(POWER_SERVICE)).newWakeLock(1, "SimpleMusicPlayer:tag1");
        wakeLock.acquire();
        if(intent.getExtras()==null)
            stopSelf();
        mflag = intent.getExtras().getInt(playflag);
        randomposition = intent.getExtras().getInt(randomindex, 0);
        id = intent.getExtras().getString(playid);
        ids = intent.getExtras().getStringArrayList(playids);
        if(id==null&&ids==null)
            stopSelf();
        if (this.mflag == FLAG_RANDOM) {
            id = ids.get(this.randomposition);
        }
        Log.i("id",id);
        LoadMusic = createLoadMusicThread(id);
        LoadMusic.start();
        if(remoteViews==null){
            remoteViews = new RemoteViews(this.getPackageName(), R.layout.notification2);
        }
        createTimeThread(remoteViews);
        MainThread =new Thread(){
            @Override
            public void run() {
                myRemoteViewsAction(remoteViews);
            }
        };
        MainThread.start();
        return START_NOT_STICKY;
    }
    Thread MainThread;
    private void myRemoteViewsAction(final RemoteViews view) {
        Intent pauseIntent = new Intent(ACTION_PAUSE);
        Intent nextIntent = new Intent(ACTION_NEXT);
        nextIntent.putExtra(playids, (ids));
        nextIntent.putExtra(randomindex, randomposition);
        nextIntent.putExtra(playflag, mflag);
        Intent lastIntent = new Intent(ACTION_LAST);
        lastIntent.putExtra(playids, (ids));
        lastIntent.putExtra(randomindex, randomposition);
        lastIntent.putExtra(playflag, mflag);
        Intent loopIntent = new Intent(ACTION_LOOP);
        Intent windowIntent=new Intent(ACTION_WINDOW);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 1, pauseIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(getBaseContext(), 1, lastIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent pendingIntent3 = PendingIntent.getBroadcast(getBaseContext(), 1, nextIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent pendingIntent4 = PendingIntent.getBroadcast(getBaseContext(), 1, loopIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent pendingIntent5 = PendingIntent.getBroadcast(getBaseContext(),1,windowIntent,PendingIntent.FLAG_CANCEL_CURRENT);
        view.setOnClickPendingIntent(R.id.nB, pendingIntent);
        view.setOnClickPendingIntent(R.id.nA, pendingIntent2);
        view.setOnClickPendingIntent(R.id.nC, pendingIntent3);
        view.setOnClickPendingIntent(R.id.nD,pendingIntent4);
        view.setOnClickPendingIntent(R.id.nE,pendingIntent5);
        GSong.getName(id, new OnGetNameCompile() {
            @Override
            public void onCompile(String id, String name) {
                view.setTextViewText(R.id.nTitle, name);
                mHandler.post(new MainActivity.MyRunnable1(t3,name));
            }
        });
//        if(!id.contains("@")){
//        view.setImageViewBitmap(R.id.nImage, GSong.getSquareIcon(id, 300));
//        view.setTextViewText(R.id.nAuthor, GSong.getAuthor(id));}
        view.setImageViewResource(R.id.nImage,R.drawable.gnzbky);
        changeViewLoop();
        changeViewPause();
        view.setOnClickPendingIntent(R.id.notification_exit,PendingIntent.getBroadcast(this,2,new Intent(ACTION_STOPSERVICE),PendingIntent.FLAG_CANCEL_CURRENT));
        if(mA!=null){
            mA.setOnClickListener(new MyListener(lastIntent));
            mB.setOnClickListener(new MyListener(pauseIntent));
            mC.setOnClickListener(new MyListener(nextIntent));
        }
        layout_clickEvent(view);
        myNotificationAction(view);
    }
    public static void changeViewPause() {
        if(mp.isLooping()){
            remoteViews.setTextViewText(R.id.notificationpause,"暂停中");
        }else {
            remoteViews.setTextViewText(R.id.notificationpause,"");
        }
    }
    public static void changeViewLoop() {
        if(mp.isLooping()){
            remoteViews.setTextViewText(R.id.notificationloop,"循环中");
            mp.setOnCompletionListener(null);
        }else {
            remoteViews.setTextViewText(R.id.notificationloop,"不循环");
            mp.setOnCompletionListener(myOnCompletionListener);
        }
    }
    private class MyListener implements View.OnClickListener{
        Intent i;
        public MyListener(Intent i){
            this.i=i;
        }
        @Override
        public void onClick(View v) {
            sendBroadcast(i);
        }
    }
    private void layout_clickEvent(RemoteViews view) {
        PendingIntent intent=PendingIntent.getActivity(this,2,new Intent(this,MainActivity.class),0);
        view.setOnClickPendingIntent(R.id.clickable_layout1,intent);
        view.setOnClickPendingIntent(R.id.clickable_layout2,intent);
        view.setOnClickPendingIntent(R.id.clickable_layout3,intent);
        view.setOnClickPendingIntent(R.id.clickable_layout4,intent);
        view.setOnClickPendingIntent(R.id.clickable_layout5,intent);
        view.setOnClickPendingIntent(R.id.nImage,intent);
    }
    private Thread createLoadMusicThread(final String id) {
        mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return true;
            }
        });
        LoadMusic = new Thread() {
            public void run() {
                mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        try {
                            TimeThread.start();
                        } catch (Exception e) {
                        }
                    }
                });
                try {
                    if (mflag == FLAG_RANDOM) {
                        myOnCompletionListener = new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                TimeThread.interrupt();
                                mNotificationManager.cancel(3);
                                Intent intent = new Intent(PlayingService.this, ServiceA.class);
                                intent.putExtra("ids", (ids));
                                int i = randomposition + 1;
                                intent.putExtra("randomplayposition", i);
                                intent.putExtra("mflag", mflag);
                                startService(intent);
                            }
                        };
                    } else if (mflag == FLAG_SOLO) {
                        myOnCompletionListener=new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                mNotificationManager.cancel(3);
                                stopSelf();
                            }
                        };
                    }
                    if(id.contains("@")){
//                        String path= GLibraryManager.getLib(_LINK).get(id);
                        File song=new File(_LOCATION,id);
                        if(song.exists()){
                            mp.setDataSource(song.getAbsolutePath());
                        }else {
                            myOnCompletionListener.onCompletion(mp);
                        }
                    }
                    else if (!id.equals(1359058327 + "")) {
                        mp.setDataSource(Outer + id);
                    } else {
                        mp.setDataSource(new File("/mnt/sdcard/Android/data", "水上灯（赤绫）（Cover：Braska） - 溱绫西陌,乐正绫,赤羽.mp3").getAbsolutePath());
                    }
                    mp.prepareAsync();
                    mp.setOnCompletionListener(myOnCompletionListener);
                } catch (Exception exception) {
                    Log.e("e", exception.toString());
                }
            }
        };
        return LoadMusic;
    }
    private void myNotificationAction(RemoteViews view) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            Notification.Builder nb = new Notification.Builder(this)
                    .setTicker(GSong.getName(id)+"is playing.")
                    .setSmallIcon(R.drawable.playicon)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setVisibility(Notification.VISIBILITY_PUBLIC)
//                    .setTicker("SimpleMusicPlayer")
//                    .setContentTitle("SimpleMusicPlayer")
//                    .setContentText("byGking 2665241312");
                    .setContent(view);
					//.setCustomContentView(view)
					//.setCustomBigContentView(view);
            n = nb.build();
            n.flags=Notification.FLAG_ONGOING_EVENT;
            n.bigContentView = view;
            n.contentView = view;
            startForeground(1, n);
        } else {
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            String ci = "cccc";
            NotificationChannel nc = new NotificationChannel(ci, "strrrrrrr", NotificationManager.IMPORTANCE_HIGH);
            nm.createNotificationChannel(nc);
            Notification.Builder nb = new Notification.Builder(this, ci).
                    //setContentTitle(name).
                    //setContentText(sid.substring(sid.indexOf("=") + 1)).
             setSmallIcon(R.drawable.playicon)
                    .setChannelId(ci)
                    .setCustomBigContentView(view)
                    .setCustomContentView(view);
            //setContentIntent(pendingIntent).
            //setLargeIcon(li);
            n=nb.build();
            startForeground(1, n);
        }
    }
    Thread TimeThread;
	private Thread createTimeThread(final RemoteViews view) {
		TimeThread =new Thread(){
			@Override
			public void run() {
			    Looper.prepare();
			    startService(new Intent(PlayingService.this, MiniWindow.class));
			    try {
                    Notification.Builder lockedNotifyBuilder =
                            new Notification.Builder(PlayingService.this)
                                    .setContentTitle(GSong.getName(id))
                                    .setSmallIcon(getApplicationInfo().icon)
                                    .setContentText("")
                                    .setContentIntent(
                                            PendingIntent.getActivity(PlayingService.this, 3, new Intent(PlayingService.this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));
                    KeyguardManager km= (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
                    GLibrary lib= GLibraryManager.getLib(_SETTINGS);
			        boolean isLockedNotificationShow=Boolean.parseBoolean(lib.get(LOCKEDNOTIFICATIONSHOW));
			        long timestart=System.currentTimeMillis();
                    while (n == null) {
                        if(System.currentTimeMillis()-timestart>8000){
                            timestart=System.currentTimeMillis();
                            mp.setOnCompletionListener(null);
                            Intent intent=new Intent(PlayingService.this,ServiceA.class);
                            intent.putExtra(playid,PlayingService.id);
                            intent.putExtra(playids,PlayingService.ids);
                            intent.putExtra(randomindex,PlayingService.randomposition);
                            intent.putExtra(playflag,PlayingService.mflag);
                            startService(intent);
                        }
                    }
                    mp.start();
                    Handler handler=mHandler;
                    final int timeall = mp.getDuration() / 1000;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            seekBar.setMax(timeall);
//                            seekBar.setValueRange(0,timeall,true);
                        }
                    });
                    String stringall = "/";
                    if (timeall % 60 < 10) {
                        stringall += timeall / 60 + ":0" + timeall % 60;
                    } else {
                        stringall += timeall / 60 + ":" + timeall % 60;
                    }
                    view.setTextViewText(R.id.notificationtotal,stringall);
                    handler.post(new MainActivity.MyRunnable1(t2, stringall.substring(1)));
                    int lyricindex = 0;
                    int lasttime = 0, nexttime = mp.getDuration();
                    MyCollection1 mc = JSON.getlyric(new MyCollection1(id));
                    ArrayList<String> lyrics = null;
                    ArrayList<Integer> times = null;
                    boolean hasLyric = false;
                    if (mc != null) {
                        times = mc.getTime();
                        lyrics = mc.getLyric();
                        hasLyric = true;
                        times.add(mp.getDuration());
                    }
                    boolean changed = false;
                    boolean changed2 = false;
                    String stringtime, stringtime2 = null;
                    MainActivity.MyRunnable1 timeChange=new MainActivity.MyRunnable1(t1);
                    MainActivity.MyRunnable1 lyricChange=new MainActivity.MyRunnable1(t4);
                    MainActivity.MyRunnable1 lyricChange2=new MainActivity.MyRunnable1(MyApplicationImpl.LyricTextView);
                    MainActivity.MyRunnable2 seekBarChange=new MainActivity.MyRunnable2();
                    seekBarChange.setI(0);
                    interrupted();
                    while (!isInterrupted()) {
                        if(isLockedNotificationShow&&!km.inKeyguardRestrictedInputMode()){
                            mNotificationManager.cancel(3);
                        }
                        int timenow = mp.getCurrentPosition() / 1000;
                        if (timenow % 60 < 10) {
                            stringtime = timenow / 60 + ":0" + timenow % 60;
                        } else {
                            stringtime = timenow / 60 + ":" + timenow % 60;
                        }
                        if (!stringtime.equals(stringtime2)) {
                            stringtime2 = stringtime;
                            view.setTextViewText(R.id.notificationnow, stringtime);
                            timeChange.setS(stringtime);
                            handler.post(timeChange);
                            seekBarChange.setI(timenow);
                            handler.post(seekBarChange);
                            changed = true;
                        }
                        timenow = mp.getCurrentPosition();
                        if (hasLyric) {
                            if (lyricindex <= lyrics.size() - 2) {
                                lasttime = times.get(lyricindex);//8
                                nexttime = times.get(lyricindex + 1);//16
                            } else if (lyricindex == lyrics.size() - 1) {
                                lasttime = times.get(lyricindex);
                                nexttime = mp.getDuration();
                            }
                            while (timenow<lasttime){
                                if(lyricindex==0){
                                    break;
                                }
                                lyricindex--;
                                lasttime=times.get(lyricindex);
                            }
                            if (lasttime <= timenow && timenow < nexttime) {
                                if (!changed2) {
                                    String lyric = lyrics.get(lyricindex);
                                    lockedNotifyBuilder.setContentText(lyric);
                                    view.setTextViewText(R.id.notificationlyric,lyric);
                                    lyricChange.setS(lyric);
                                    handler.post(lyricChange);
                                    lyricChange2.setS(lyric);
                                    mHandler.post(lyricChange2);

                                    changed = true;
                                    changed2 = true;
                                }
                            } else if (nexttime <= timenow) {
                                lyricindex++;
                                changed2 = false;
                            }
                        }
                        if (changed) {
                            n.bigContentView = view;
                            n.contentView = view;
                            startForeground(1, n);
                            if(isLockedNotificationShow&&km.inKeyguardRestrictedInputMode()){
                                mNotificationManager.notify(3,lockedNotifyBuilder.build());
                            }
                        }
                        changed = false;
                        sleep(10);
                    }
                }catch (Exception e){
                    Log.e("e",e.toString(),e);
                }
			}
		};
		return TimeThread;
	}
    private static void stopThread(Thread thread){
        try {
            if (thread != null) {
                thread.interrupt();
                thread.stop();
            }
        }catch (Exception e){
        }
    }
}
