package com.gkingswq.simplemusicplayer.base.base1;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;

import com.gkingswq.simplemusicplayer.base.BaseActivity;

public abstract class Activity1 extends BaseActivity {
    @Override
    public ComponentName startService(Intent intent) {
        stopService(intent);
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            return super.startForegroundService(intent);
        }else{
            return super.startService(intent);
        }
    }
}
