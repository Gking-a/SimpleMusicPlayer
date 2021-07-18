package com.gking.simplemusicplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gking.simplemusicplayer.activity.SongActivity;

public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, SongActivity.class));
    }
}
