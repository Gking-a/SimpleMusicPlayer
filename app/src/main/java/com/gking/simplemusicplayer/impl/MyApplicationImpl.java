package com.gking.simplemusicplayer.impl;

import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.gking.simplemusicplayer.MyBroadcastReceiver;
import com.gking.simplemusicplayer.R;
import com.gking.simplemusicplayer.activity.EmptyActivity;
import com.gking.simplemusicplayer.activity.SettingsActivity;
import com.gking.simplemusicplayer.service.BackgroundService;
import com.gking.simplemusicplayer.service.SongService;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

import static com.gking.simplemusicplayer.activity.SettingsActivity.Params.account_phone;
import static com.gking.simplemusicplayer.activity.SettingsActivity.Params.account_pw;
import static com.gking.simplemusicplayer.activity.SettingsActivity.Params.auto_next;
import static com.gking.simplemusicplayer.activity.SettingsActivity.Params.play_mode;
import static com.gking.simplemusicplayer.activity.SettingsActivity.Params.window_color;
import static com.gking.simplemusicplayer.activity.SettingsActivity.Params.zdkqxfgc;
import static com.gking.simplemusicplayer.activity.SettingsActivity.SettingsFile;
import static com.gking.simplemusicplayer.activity.SettingsActivity.get;
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
    public MusicPlayer mMusicPlayer;
    public View controlPanel;
    public View windowView;
    File exceptionFile;
    @Override
    public void onCreate() {
        super.onCreate();
        myApplication=this;
        mMusicPlayer=new MusicPlayer();
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionCatcher());
        loadView();
        loadSettings();
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, SongService.class));
        }else {
            startService(new Intent(this, SongService.class));
        }
    }
    private final AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {

            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {

            } else {
                mMusicPlayer.pause();
            }
        }
    };
    public void requestFocus(){
        AudioManager audioManager= (AudioManager) getSystemService(AUDIO_SERVICE);
        audioManager.abandonAudioFocus(onAudioFocusChangeListener);
        audioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }
    class MyExceptionCatcher implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(@NonNull @NotNull Thread t, @NonNull @NotNull Throwable e) {
            e.printStackTrace();
            output(e);
            exceptionFile=new File(getFilesDir(),"e");
            if(!exceptionFile.exists()) {
                try {
                    exceptionFile.createNewFile();
                    FileWriter fw=new FileWriter(exceptionFile);
                    fw.write(e.getCause()+e.getMessage());
                    for (int i = 0; i < e.getStackTrace().length; i++) {
                        fw.write(e.getStackTrace()[i].toString());
                    }
                    fw.flush();
                    fw.close();
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
        next.setOnClickListener(v -> getMusicPlayer().forceNext());
        last.setOnClickListener(v -> getMusicPlayer().forceLast());
        pause.setOnClickListener(v -> getMusicPlayer().changeP());
        controlPanel.setVisibility(View.GONE);
        windowView=LayoutInflater.from(this).inflate(R.layout.window,null);
        if(Boolean.parseBoolean(get(zdkqxfgc))){
            Intent intent = new Intent(this,BackgroundService.class);
            intent.putExtra(BackgroundService.Type.Type,BackgroundService.Type.Window);
            startService(intent);
        }
    }
    public ImageView Cover;
    public TextView Name,Author;
    private void loadSettings() {
		if(!getFilesDir().exists())
			getFilesDir().mkdirs();
        myBroadcastReceiver = new MyBroadcastReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
	}
//    private class MyExceptionCatcher implements Thread.UncaughtExceptionHandler{
//
//        @Override
//        public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
//            e.printStackTrace();
//            output(new Date()+"\n");
//            output(t+"\n");
//            output(e);
//            Process.killProcess(Process.myPid());
//        }
//	}
    public static void output(Object ...os){
        for (Object o : os) {
            System.out.println(o);
        }
    }
}

