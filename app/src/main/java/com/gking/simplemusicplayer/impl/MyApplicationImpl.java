package com.gking.simplemusicplayer.impl;

import gtools.GLibrary;
import gtools.managers.GLibraryManager;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import android.app.Application;
import android.media.MediaPlayer;
import android.util.Log;
import androidx.annotation.NonNull;
import com.gking.simplemusicplayer.util.FW;
import com.gking.simplemusicplayer.util.GFile;

import java.util.Date;
import static com.gking.simplemusicplayer.activity.MySettings.*;
public class MyApplicationImpl extends Application
{
	public static final File CoverImg=new File("/data/user/0/com.gkingswq.simplemusicplayer/files/CoverImg/");
	public static final File Playlists=new File("/data/user/0/com.gkingswq.simplemusicplayer/files/Playlists/");
	public static final File Cookies=new File("/data/user/0/com.gkingswq.simplemusicplayer/files/Cookies/");
	MediaPlayer mMusicPlayer=new MediaPlayer();
    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionCatcher());
        loadSettings();
    }
    
	private void loadSettings() {
		if(!getFilesDir().exists())
			getFilesDir().mkdirs();

		GFile.createDirs(CoverImg,Playlists, Cookies);
		for (File file: Objects.requireNonNull(getFilesDir().listFiles())){
			if(file.isFile())
				GLibraryManager.add(new GLibrary(file,true));
		}
	}
    private class MyExceptionCatcher implements Thread.UncaughtExceptionHandler{

        @Override
        public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
            Log.e("Exception",t+" "+e);
            FW.w(new Date()+"\n");
            FW.w(t+"\n");
            FW.w(e);
        }
	}
}

