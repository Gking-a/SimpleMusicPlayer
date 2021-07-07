package com.gking.simplemusicplayer.impl;

import com.gking.simplemusicplayer.R;
import com.kongzue.dialogx.DialogX;
import gtools.GLibrary;
import gtools.managers.GHolder;
import gtools.managers.GLibraryManager;

import java.io.File;
import java.util.Objects;
import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Process;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import com.gking.simplemusicplayer.util.FW;
import com.gking.simplemusicplayer.util.GFile;
import com.google.gson.JsonObject;

import java.util.Date;

public class MyApplicationImpl extends Application
{
    public static Handler handler=new Handler();
    public static MyApplicationImpl myApplication;
	public static final File CoverImg=new File("/data/user/0/com.gkingswq.simplemusicplayer/files/CoverImg/");
	public static final File Playlists=new File("/data/user/0/com.gkingswq.simplemusicplayer/files/Playlists/");
	public static final File Cookies=new File("/data/user/0/com.gkingswq.simplemusicplayer/files/Cookies/");
    public MusicPlayer getMusicPlayer() {
        return mMusicPlayer;
    }
    public GHolder<String, Bitmap> songCover=new GHolder<>();
    public GHolder<String, JsonObject> getSongInfo() {
        return songInfo;
    }
    public GHolder<String, JsonObject> songInfo=new GHolder<>();
    public GHolder<String, Bitmap> getSongCover() {
        return songCover;
    }
    public MusicPlayer mMusicPlayer=new MusicPlayer();
    public View controlPanel;
    @Override
    public void onCreate() {
        super.onCreate();
        myApplication=this;
        DialogX.init(this);
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionCatcher());
        load();
        loadSettings();
    }

    private void load() {
        controlPanel =LayoutInflater.from(this).inflate(R.layout.control,null);
        loadControlPanel();
    }
    public ImageView cover;
    public TextView Name,Author;
    public void loadControlPanel() {
        controlPanel.setBackgroundColor(0xFFffFFff);
        cover = controlPanel.findViewById(R.id.c_song_cover);
        Name = controlPanel.findViewById(R.id.c_song_name);
        Author = controlPanel.findViewById(R.id.c_song_author);
        ImageButton next = controlPanel.findViewById(R.id.c_song_next),
                last = controlPanel.findViewById(R.id.c_song_last),
                pause = controlPanel.findViewById(R.id.c_song_pause);
        next.setOnClickListener(v -> getMusicPlayer().next(null));
        last.setOnClickListener(v -> getMusicPlayer().last(null));
        pause.setOnClickListener(v -> getMusicPlayer().pause());
        controlPanel.setVisibility(View.GONE);
    }


    class MyRunnable implements Runnable{
        String id;
        String name;
        String au;

        public MyRunnable(String id, String name, String au) {
            this.id = id;
            this.name = name;
            this.au = au;
        }

        @Override
        public void run() {
            Bitmap bitmap= getSongCover().get(id);
            Name.setText(name);
            Author.setText(au);
            if(bitmap!=null)cover.setImageBitmap(bitmap);
            else {
                new Thread(){
                    @Override
                    public void run() {
                        do {
                            try {
                                sleep(10);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }while (getSongCover().get(id)==null);
                        Bitmap bitmap= getSongCover().get(id);
                        handler.post(()->cover.setImageBitmap(bitmap));
                    }
                }.start();
            }
        }
    }
    public void setControlInfo(String id, String name, String au, Handler handler){
        handler.post(new MyRunnable(id,name,au));
    }
    private void loadSettings() {
		if(!getFilesDir().exists())
			getFilesDir().mkdirs();
		GFile.createDirs(CoverImg,Playlists, Cookies);
		for (File file: Objects.requireNonNull(getFilesDir().listFiles())){
			if(file.isFile())
				GLibraryManager.add(new GLibrary(file,true));
		}
        GHolder<String, JsonObject> songs=new GHolder<>();
		GHolder.standardInstance.add("songs",songs);
		mMusicPlayer.setAuto(true);
	}
    private class MyExceptionCatcher implements Thread.UncaughtExceptionHandler{

        @Override
        public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
            Log.e("Exception",t+" "+e);
            FW.w(new Date()+"\n");
            FW.w(t+"\n");
            FW.w(e);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
            Process.killProcess(Process.myPid());
        }
	}
	public static void l(Object o){
        Log.e("app",o.toString());
        System.out.println(o.toString());
        FW.w(o.toString());

    }
}

