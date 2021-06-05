package com.gkingswq.simplemusicplayer.impl;

import gtools.GLibrary;
import gtools.managers.GLibraryManager;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
//import com.rey.material.widget.Slider;
import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import com.gkingswq.simplemusicplayer.util.FW;
import java.util.Date;
import static com.gkingswq.simplemusicplayer.MySettings.*;
public class MyApplicationImpl extends Application
{

    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionCatcher());
        loadSettings();
    }
    
	private void loadSettings() {
		if(!getFilesDir().exists())
			getFilesDir().mkdirs();
		if(!SettingsFile.exists()){
			try {
				SettingsFile.createNewFile();
				GLibrary lib= new GLibrary(SettingsFile.getName(), SettingsFile);
//				lib.create(true);
				lib.connect();
				lib.add(DEFAULT_LIST,"null",GLibrary.TYPE_STRING);
//				lib.add(playflag,FLAG_SOLO,GLibrary.TYPE_STRING);
				lib.add(LOCKEDNOTIFICATIONSHOW,false,GLibrary.TYPE_STRING);
				lib.add(WINDOW_COLOR,"0xffff0000",GLibrary.TYPE_STRING);
				lib.add(DEFAULT_WINDOW_SHOW,false,GLibrary.TYPE_STRING);
                lib.add("debugroot",true,GLibrary.TYPE_STRING);
				lib.close(true);
				//GFileUtil.CopyFile("/sdcard/SETTINGS",_SETTINGS);
			} catch (IOException e) {
            }
		}
		
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

