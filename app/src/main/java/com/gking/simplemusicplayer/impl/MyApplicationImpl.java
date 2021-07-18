package com.gking.simplemusicplayer.impl;

import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.gking.simplemusicplayer.MyBroadcastReceiver;
import com.gking.simplemusicplayer.R;
import com.gking.simplemusicplayer.service.SongService;
import com.gking.simplemusicplayer.util.FW;

import java.io.File;
import java.util.Date;
import java.util.Objects;

import gtools.GLibrary;
import gtools.managers.GLibraryManager;

public class MyApplicationImpl extends Application
{
    public static Handler handler=new Handler();
    public static MyApplicationImpl myApplication;
    public MyBroadcastReceiver myBroadcastReceiver;
    public IntentFilter intentFilter;

    public MusicPlayer getMusicPlayer() {
        return mMusicPlayer;
    }
    public MusicPlayer mMusicPlayer=new MusicPlayer();
    public View controlPanel;
    public View windowView;
    @Override
    public void onCreate() {
        super.onCreate();
        myApplication=this;
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionCatcher());
        loadView();
        loadSettings();
        startService(new Intent(this, SongService.class));

    }
    private void loadView() {
        controlPanel =LayoutInflater.from(this).inflate(R.layout.control,null);
        controlPanel.setBackgroundColor(0xFFffFFff);
        Cover = controlPanel.findViewById(R.id.c_song_cover);
        Name = controlPanel.findViewById(R.id.c_song_name);
        Author = controlPanel.findViewById(R.id.c_song_author);
        Author.setTextColor(0xff000000);
        ImageButton next = controlPanel.findViewById(R.id.c_song_next),
                last = controlPanel.findViewById(R.id.c_song_last),
                pause = controlPanel.findViewById(R.id.c_song_pause);
        next.setOnClickListener(v -> getMusicPlayer().next(null));
        last.setOnClickListener(v -> getMusicPlayer().last(null));
        pause.setOnClickListener(v -> getMusicPlayer().pause());
        controlPanel.setVisibility(View.GONE);
        windowView=LayoutInflater.from(this).inflate(R.layout.window,null);;
    }
    public ImageView Cover;
    public TextView Name,Author;
    private void loadSettings() {
		if(!getFilesDir().exists())
			getFilesDir().mkdirs();
		for (File file: Objects.requireNonNull(getFilesDir().listFiles())){
			if(file.isFile())
				GLibraryManager.add(new GLibrary(file,true));
		}
        myBroadcastReceiver = new MyBroadcastReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
	}
    private class MyExceptionCatcher implements Thread.UncaughtExceptionHandler{

        @Override
        public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
            e.printStackTrace();
            FW.w(new Date()+"\n");
            FW.w(t+"\n");
            FW.w(e);
//            Process.killProcess(Process.myPid());
        }
	}
	public static void l(Object o){
        Log.e("app",o.toString());
        System.out.println(o.toString());
        FW.w(o.toString());

    }
}

