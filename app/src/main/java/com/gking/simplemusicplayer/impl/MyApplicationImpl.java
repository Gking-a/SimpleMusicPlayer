package com.gking.simplemusicplayer.impl;

import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.gking.simplemusicplayer.MyBroadcastReceiver;
import com.gking.simplemusicplayer.R;
import com.gking.simplemusicplayer.activity.SettingsActivity;
import com.gking.simplemusicplayer.service.SongService;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;

import gtools.GLibrary;
import gtools.managers.GLibraryManager;

import static com.gking.simplemusicplayer.activity.SettingsActivity.Params.auto_next;
import static com.gking.simplemusicplayer.activity.SettingsActivity.Params.play_mode;
import static com.gking.simplemusicplayer.activity.SettingsActivity.Params.window_color;
import static com.gking.simplemusicplayer.activity.SettingsActivity.SettingsFile;
import static com.gking.simplemusicplayer.activity.SettingsActivity.library;

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
    File exceptionFile;
    @Override
    public void onCreate() {
        super.onCreate();
        myApplication=this;
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionCatcher());
        loadView();
        loadSettings();
        startService(new Intent(this, SongService.class));
    }
    class MyExceptionCatcher implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(@NonNull @NotNull Thread t, @NonNull @NotNull Throwable e) {
            e.printStackTrace();
            exceptionFile=new File(getFilesDir(),"e");
            if(!exceptionFile.exists()) {
                try {
                    exceptionFile.createNewFile();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
            try {
                PrintWriter printWriter=new PrintWriter(new FileWriter(exceptionFile));
                e.printStackTrace(printWriter);
                printWriter.flush();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
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
//    private class MyExceptionCatcher implements Thread.UncaughtExceptionHandler{
//
//        @Override
//        public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
//            e.printStackTrace();
//            FW.w(new Date()+"\n");
//            FW.w(t+"\n");
//            FW.w(e);
////            Process.killProcess(Process.myPid());
//        }
//	}
}

