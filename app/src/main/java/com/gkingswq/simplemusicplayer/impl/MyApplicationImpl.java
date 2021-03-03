package com.gkingswq.simplemusicplayer.impl;

import android.app.NotificationManager;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import static com.gkingswq.simplemusicplayer.Value.Files.*;
import static com.gkingswq.simplemusicplayer.Value.Settings.*;
import static com.gkingswq.simplemusicplayer.Value.Flags.*;
import static com.gkingswq.simplemusicplayer.Value.IntentKeys.*;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.gkingswq.simplemusicplayer.base.BaseApplication;
import java.io.IOException;
import java.io.File;
import GTools.GLibrary;
import java.util.Objects;
import GTools.GLibraryManager;
import GTools.util.GFileUtil;

import android.content.Intent;
import com.gkingswq.simplemusicplayer.PlayingService;
//import com.rey.material.widget.Slider;

public class MyApplicationImpl extends BaseApplication
{
    public static WindowManager mWindowManager;
    public static NotificationManager mNotificationManager;
    public static Handler mHandler;
    public static TextView LyricTextView;
    public static TextView t1,t2,t3,t4;
    public static SeekBar seekBar;
    public static ImageButton mA,mB,mC;
    public static FloatingActionsMenu fab_menu;
    public static FloatingActionButton fab1,fab2;
    @Override
    public void initRecourse()
    {
        mWindowManager=(WindowManager) getSystemService(WINDOW_SERVICE);
        mNotificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        LyricTextView=new TextView(this);
        mHandler=new Handler();
        if(!getFilesDir().exists())
            getFilesDir().mkdirs();
		loadSettings();
    }
	private void loadSettings() {
		if(!getFilesDir().exists())
			getFilesDir().mkdirs();
		if(!_SETTINGS.exists()){
			try {
				_SETTINGS.createNewFile();
				GLibrary lib= new GLibrary(_SETTINGS.getName(), _SETTINGS);
//				lib.create(true);
				lib.connect();
				lib.add(DEFAULT_LIST,"null",GLibrary.TYPE_STRING);
				lib.add(playflag,FLAG_SOLO,GLibrary.TYPE_STRING);
				lib.add(LOCKEDNOTIFICATIONSHOW,false,GLibrary.TYPE_STRING);
				lib.add(WINDOW_COLOR,"0xffff0000",GLibrary.TYPE_STRING);
				lib.add(DEFAULT_WINDOW_SHOW,false,GLibrary.TYPE_STRING);
                lib.add("debugroot",true,GLibrary.TYPE_STRING);
				lib.close(true);
				//GFileUtil.CopyFile("/sdcard/SETTINGS",_SETTINGS);
			} catch (IOException e) {
            }
		}
		if(!_LOCATION.exists()){
			_LOCATION.mkdir();
		}
		if(!_LINK.exists()){
			try {
				_LINK.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
//		if(!_NAME.exists()){
//			try {
//				GFileUtil.CopyFile("/sdcard/NAME",_NAME);
//				_NAME.createNewFile();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		File a=new File(getFilesDir(),"α.GList");
//		if(!a.exists()) {
//			try {
//				a.createNewFile();
//				GFileUtil.CopyFile("/sdcard/α.GList", a);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		a=new File(getFilesDir(),"β.GList");
//			if(!a.exists()){
//				try {
//					a.createNewFile();
//					GFileUtil.CopyFile("/sdcard/β.GList",a);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//		}
		for (File file: Objects.requireNonNull(getFilesDir().listFiles())){
			if(file.isFile())
				GLibraryManager.add(new GLibrary(file,true));
		}
	}

	@Override
	public void exit() {
		stopService(new Intent(this,PlayingService.class));
		stopService(new Intent(this,PlayingService.class));
		try {
			GLibraryManager.getLib(_NAME).save();
			GLibraryManager.getLib(_SETTINGS).save();
			GLibraryManager.getLib(_LINK).save();
		} catch (IOException e) {
			e.printStackTrace();
		}
		android.os.Process.killProcess(android.os.Process.myPid());
	}
}

