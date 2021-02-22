package com.gkingswq.simplemusicplayer;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.IBinder;
import android.view.Gravity;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import GTools.GLibrary;
import GTools.GLibraryManager;

import static com.gkingswq.simplemusicplayer.Value.Files._SETTINGS;
import static com.gkingswq.simplemusicplayer.Value.Settings.DEFAULT_WINDOW_SHOW;
import static com.gkingswq.simplemusicplayer.Value.Settings.WINDOW_COLOR;
import static com.gkingswq.simplemusicplayer.impl.MyApplicationImpl.LyricTextView;
import static com.gkingswq.simplemusicplayer.impl.MyApplicationImpl.mWindowManager;

public class MiniWindow extends Service {
    static boolean isShowing=false;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBind();
    }
    public class MyBind extends Binder {
        public void change(String lyric){
            MiniWindow.this.change(lyric);
        }
    }
    public static void show(){
        if(isShowing){
            mWindowManager.removeViewImmediate(LyricTextView);
        }else {
            WindowManager.LayoutParams params=new WindowManager.LayoutParams();
            params.width= WindowManager.LayoutParams.MATCH_PARENT;
            params.height= 300;
            params.type= WindowManager.LayoutParams.TYPE_PHONE;
            params.flags=  WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
            params.format= PixelFormat.RGBA_8888;
            params.gravity= Gravity.BOTTOM;
            mWindowManager.addView(LyricTextView,params);
        }
        isShowing=!isShowing;
    }
    public static void change(String lyric){
        LyricTextView.setText(lyric);
    }
    @Override
    public void onCreate() {
        super.onCreate();
        GLibrary lib= GLibraryManager.getLib(_SETTINGS);
        int color=(int) Long.parseLong(lib.get(WINDOW_COLOR).substring(2), 16);
        LyricTextView.setTextSize(19);
        LyricTextView.setTextColor(color);
        if(Boolean.parseBoolean(GLibraryManager.getLib(_SETTINGS).get(DEFAULT_WINDOW_SHOW)))
            show();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
    @Override
    public void onDestroy() {
        if(isShowing)
             mWindowManager.removeViewImmediate(LyricTextView);
        isShowing=false;
        LyricTextView.setText("");
        super.onDestroy();
    }
}
