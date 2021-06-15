/*
 */

package com.gking.simplemusicplayer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ReceiveService extends Service {
    
    @Override
    public IBinder onBind(Intent intent) {
        
        return null;
    }
    
}