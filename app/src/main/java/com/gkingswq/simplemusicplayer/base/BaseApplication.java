package com.gkingswq.simplemusicplayer.base;
import android.app.*;
import android.content.*;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import com.gkingswq.simplemusicplayer.FW;

public abstract class BaseApplication extends Application
{
	private MyContextManager myContextManager=new MyContextManager();
	public void pushContext(Context context){
		myContextManager.push(context);
	}
	public void removeContext(Context context){
		myContextManager.remove(context);
	}
	public Context getContext(Class clazz){
		return myContextManager.get(clazz);
	}
	private class MyContextManager {
		private Map<Class,Context> Contexts=new Hashtable<>();
		public void push(Context context){
			Contexts.put(context.getClass(),context);
		}
		public void remove(Context context){
			remove(context.getClass());
		}
		public void remove(Class clazz){
			if(Contexts.containsKey(clazz))
				Contexts.remove(clazz);
		}
		public Context get(Class clazz){
			if(Contexts.containsKey(clazz))
				return Contexts.get(clazz);
			return null;
		}
	}
	private class MyExceptionCatcher implements Thread.UncaughtExceptionHandler{
		
		@Override
		public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
			Log.e("Exception",t+" "+e);
			FW.w(new Date()+"\n");
			FW.w(t+"\n");
			FW.w(e);
		}
		FileWriter fw;

	}
	@Override
	public void onCreate()
	{
		super.onCreate();
		Thread.setDefaultUncaughtExceptionHandler(new MyExceptionCatcher());
		initRecourse();
	}
	public abstract void initRecourse();
	public abstract void exit();
}
