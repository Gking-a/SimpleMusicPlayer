/*
 */

package com.gking.simplemusicplayer;

import android.app.Activity;
import android.os.Bundle;
import java.io.File;

public class MySettings extends Activity {
    public static final String DEFAULT_LIST ="defaultlist";
    public static final String LOCKEDNOTIFICATIONSHOW="lockednotificationshow";
    public static final String WINDOW_COLOR="windowcolor";
    public static final String DEFAULT_WINDOW_SHOW="defaultwindow";
    public static final File SettingsFile =new File("/data/user/0/com.gkingswq.simplemusicplayer/files/Settings");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    }
    
}
