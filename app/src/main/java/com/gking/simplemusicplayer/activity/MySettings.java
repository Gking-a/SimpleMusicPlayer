/*
 */

package com.gking.simplemusicplayer.activity;

import android.app.Activity;
import android.os.Bundle;
import java.io.File;

import gtools.GLibrary;
import gtools.managers.GLibraryManager;

public class MySettings extends Activity {
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
    public void set(String key,String v){
        library.add(key,v,GLibrary.TYPE_STRING);
    }
}
