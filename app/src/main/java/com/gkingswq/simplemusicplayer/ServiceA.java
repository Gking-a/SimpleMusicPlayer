package com.gkingswq.simplemusicplayer;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Build;


import static com.gkingswq.simplemusicplayer.Value.Flags.*;
import static com.gkingswq.simplemusicplayer.Value.IntentKeys.*;
public class ServiceA extends Service {
	@Override
	public IBinder onBind(Intent p1) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Intent i=new Intent();
		i.setClass(this, PlayingService.class);
		stopService(i);
		i.putExtra(playid,intent.getStringExtra(playid));
		i.putExtra(randomindex,intent.getIntExtra(randomindex,1));
		i.putExtra(playids,intent.getStringArrayListExtra(playids));
		i.putExtra(playflag,intent.getIntExtra(playflag,0));
		startService(i);
		//stopSelf();
		return super.onStartCommand(intent, flags, startId);
	}
	@Override
	public android.content.ComponentName startService(Intent intent){
		if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
			return super.startForegroundService(intent);
		}else{
			return super.startService(intent);
		}
	}
	
}
