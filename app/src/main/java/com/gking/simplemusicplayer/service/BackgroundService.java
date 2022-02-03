package com.gking.simplemusicplayer.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.gking.simplemusicplayer.R;
import com.gking.simplemusicplayer.activity.SettingsActivity;
import com.gking.simplemusicplayer.impl.MusicPlayer;
import com.gking.simplemusicplayer.impl.MyApplicationImpl;

import static com.gking.simplemusicplayer.service.BackgroundService.Type.*;

public class BackgroundService extends Service {
    MusicPlayer musicPlayer;
    private WindowManager.LayoutParams layoutParams;
    private MyApplicationImpl application;
    private View windowView;

    @Override
    public void onCreate() {
        super.onCreate();
        application = (MyApplicationImpl) getApplication();
        musicPlayer= application.mMusicPlayer;
        windowView=application.windowView;
        layoutParams = new WindowManager.LayoutParams();
        layoutParams.width= WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height= WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.flags= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        layoutParams.format= PixelFormat.RGBA_8888;
        layoutParams.gravity= Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;
        layoutParams.type= WindowManager.LayoutParams.TYPE_PHONE;
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int type=-1;
        if(intent!=null&&(type=intent.getIntExtra(Type,-1))>=0){
            if(type==Pause)musicPlayer.play();
            if(type==Next)musicPlayer.next(null);
            if(type==Last)musicPlayer.last(null);
            if(type==Window)window();
        }
        System.out.println(type);
        return super.onStartCommand(intent, flags, startId);
    }
    public static boolean isShowing=false;
    private void window() {
        WindowManager windowManager= (WindowManager) getSystemService(WINDOW_SERVICE);
        ((TextView) application.windowView.findViewById(R.id.window_lyric)).setTextColor(SettingsActivity.getWindowColor());
        if(isShowing){
            isShowing=false;
            windowManager.removeViewImmediate(windowView);
        }else {
            windowManager.addView(windowView, layoutParams);
            isShowing = true;
        }
    }
    public static class Type{
        public static final String Type="type";
        public static final int Pause=0;
        public static final int Last=1;
        public static final int Next=2;
        public static final int Window=3;
    }
}