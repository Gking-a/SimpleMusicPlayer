/*
 */

package com.gking.simplemusicplayer.activity;

import android.app.Activity;
import android.os.Bundle;

import java.io.File;
import java.io.IOException;

import gtools.GLibrary;
import static com.gking.simplemusicplayer.activity.MySettingsActivity.Params.*;

public class MySettingsActivity extends Activity {
    public static final String DEFAULT_LIST ="defaultlist";
    public static final String LOCKEDNOTIFICATIONSHOW="lockednotificationshow";
    public static final String WINDOW_COLOR="windowcolor";
    public static final String DEFAULT_WINDOW_SHOW="defaultwindow";
    public static final File SettingsFile =new File("/data/user/0/com.gkingswq.simplemusicplayer/files/Settings");
    private static int ver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    static GLibrary library;
    public static String get(String key){
        return library.get(key);
    }
    public static boolean getBoolean(String symbol){
        return Boolean.parseBoolean(get(symbol));
    }
    public static int getInt(String symbol){
        return Integer.parseInt(get(symbol));
    }
    public static long getLong(String symbol){
        return Long.parseLong(get(symbol));
    }
    public static String getString(String symbol){
        return get(symbol);
    }
    public static void set(String key,Object v){
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
                library.add(auto_next,true,GLibrary.TYPE_STRING);
                library.add(play_mode,PLAY_MODE.RANDOM,GLibrary.TYPE_STRING);
                library.add(window_color,0xffFF0000,GLibrary.TYPE_STRING);
                library.add(account_phone,"18263610381",GLibrary.TYPE_STRING);
                library.add(account_pw,"gking1980",GLibrary.TYPE_STRING);
                library.add("ver",1,GLibrary.TYPE_STRING);
                library.save();
                //GFileUtil.CopyFile("/sdcard/SETTINGS",_SETTINGS);
            } catch (IOException e) {
            }
        }
        library= new GLibrary(SettingsFile,true);
        ver = 1;
        if(ver >library.getInt("ver")){
            update();
        }
        MySettingsActivity.set("ver",ver);
    }
    private static void update() {

    }

    public static final class Params{
        public static final String account_name = "account_name";
        public static final String account_id = "account_id";
        public static final String account_pw = "account_pw";
        public static final String account_phone = "account_phone";
        public static final String auto_next="auto_next";
        public static final String play_mode="play_mode";
        public static final String window_color="window_color";
        public static final class PLAY_MODE{
            public static final String NONE="0";
            public static final String LOOP="1";
            public static final String RANDOM="2";
            public static final String ORDER="3";
        }
    }
}
