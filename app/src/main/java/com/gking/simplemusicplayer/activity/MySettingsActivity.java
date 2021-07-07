/*
 */

package com.gking.simplemusicplayer.activity;

import android.app.Activity;
import android.os.Bundle;
import java.io.File;
import java.io.IOException;

import gtools.GLibrary;
import gtools.managers.GLibraryManager;

public class MySettingsActivity extends Activity {
    public static final String DEFAULT_LIST ="defaultlist";
    public static final String LOCKEDNOTIFICATIONSHOW="lockednotificationshow";
    public static final String WINDOW_COLOR="windowcolor";
    public static final String DEFAULT_WINDOW_SHOW="defaultwindow";
    public static final String login="login";
    public static final File SettingsFile =new File("/data/user/0/com.gkingswq.simplemusicplayer/files/Settings");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    static GLibrary library= new GLibrary(SettingsFile,true);
    public static String get(String key){
        return library.get(key);
    }
    public static void set(String key,String v){
        library.add(key,v,GLibrary.TYPE_STRING);
        try {
            library.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    static {
        if(!SettingsFile.exists()){
            try {
                SettingsFile.createNewFile();
                library= new GLibrary(SettingsFile.getName(), SettingsFile);
//				lib.create(true);
                library.connect();
                library.add(DEFAULT_LIST,"null",GLibrary.TYPE_STRING);
//				lib.add(playflag,FLAG_SOLO,GLibrary.TYPE_STRING);
                library.add(LOCKEDNOTIFICATIONSHOW,false,GLibrary.TYPE_STRING);
                library.add(WINDOW_COLOR,"0xffff0000",GLibrary.TYPE_STRING);
                library.add(DEFAULT_WINDOW_SHOW,false,GLibrary.TYPE_STRING);
                library.add("debugroot",true,GLibrary.TYPE_STRING);
                library.save();
                //GFileUtil.CopyFile("/sdcard/SETTINGS",_SETTINGS);
            } catch (IOException e) {
            }
        }
        library= new GLibrary(SettingsFile,true);
    }
}
