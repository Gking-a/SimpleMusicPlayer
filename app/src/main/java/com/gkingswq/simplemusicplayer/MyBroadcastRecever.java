package com.gkingswq.simplemusicplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.widget.RemoteViews;

import static com.gkingswq.simplemusicplayer.PlayingService.mp;
import static com.gkingswq.simplemusicplayer.Value.Actions.ACTION_LAST;
import static com.gkingswq.simplemusicplayer.Value.Actions.ACTION_LOOP;
import static com.gkingswq.simplemusicplayer.Value.Actions.ACTION_NEXT;
import static com.gkingswq.simplemusicplayer.Value.Actions.ACTION_PAUSE;
import static com.gkingswq.simplemusicplayer.Value.Actions.ACTION_STOPSERVICE;
import static com.gkingswq.simplemusicplayer.Value.Actions.ACTION_WINDOW;
import static com.gkingswq.simplemusicplayer.Value.Flags.FLAG_SOLO;
import static com.gkingswq.simplemusicplayer.Value.IntentKeys.playflag;
import static com.gkingswq.simplemusicplayer.Value.IntentKeys.playids;
import static com.gkingswq.simplemusicplayer.Value.IntentKeys.randomindex;
/**
*@author Gking
*@time 2020.6.15 20:59
*@version 1*/
public class MyBroadcastRecever extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		RemoteViews view = intent.getParcelableExtra("rv");
		String action=intent.getAction();
		if(intent==null)
		    return;
		if(action.equals(ACTION_NEXT)){
		    if(intent.getIntExtra(playflag, FLAG_SOLO)==FLAG_SOLO){
		        return;
            }
            mp.setOnCompletionListener(null);
			Intent i=new Intent(context,ServiceA.class);
			if(intent.getIntExtra(randomindex,0)==intent.getStringArrayListExtra(playids).size()-1){
                i.putExtra(randomindex,0);
            }else{
                i.putExtra(randomindex,intent.getIntExtra(randomindex,1)+1);
            }
			i.putExtra(playids,intent.getStringArrayListExtra(playids));
			i.putExtra(playflag,intent.getIntExtra(playflag,0));
			if(intent.getIntExtra(randomindex,1)+1>=intent.getStringArrayListExtra(playids).size())
			    return;
			context.startService(i);
		}else if (action.equals(ACTION_PAUSE)){
		    MediaPlayer mp= PlayingService.mp;
            if(mp.isPlaying()){
                mp.pause();
            }else{
                mp.start();
            }
            PlayingService.changeViewPause();
		}else if(action.equals(ACTION_LAST)){
            if(intent.getIntExtra(playflag, FLAG_SOLO)==FLAG_SOLO){
                return;
            }
            mp.setOnCompletionListener(null);
            Intent i=new Intent(context,ServiceA.class);
            if(intent.getIntExtra(randomindex,0)==0){
                i.putExtra(randomindex,intent.getStringArrayListExtra(playids).size()-1);
            }else {
                i.putExtra(randomindex, intent.getIntExtra(randomindex, 0) - 1);
            }
            i.putExtra(playids,intent.getStringArrayListExtra(playids));
            i.putExtra(playflag,intent.getIntExtra(playflag,0));
            context.startService(i);
        }else if(action.equals(ACTION_STOPSERVICE)){
		    context.stopService(new Intent(context,PlayingService.class));
        }else if(action.equals(ACTION_LOOP)){
		    mp.setLooping(!mp.isLooping());
		    PlayingService.changeViewLoop();
        }else if(action.equals(ACTION_WINDOW)){
		    MiniWindow.show();
        }
	}
}
