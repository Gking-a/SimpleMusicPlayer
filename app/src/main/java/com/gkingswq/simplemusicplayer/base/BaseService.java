package com.gkingswq.simplemusicplayer.base;

import android.app.Service;

public abstract class BaseService extends Service {
    
	 @Override
     public void onCreate() {
        super.onCreate();
		//((BaseApplication)getApplication()).pushContext(this);
    }

	@Override
	public void onDestroy(){
		super.onDestroy();
		//((BaseApplication)getApplication()).removeContext(this);
	}
    
}
