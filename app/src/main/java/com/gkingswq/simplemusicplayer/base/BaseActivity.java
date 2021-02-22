package com.gkingswq.simplemusicplayer.base;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.lang.reflect.Method;

public abstract class BaseActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		((BaseApplication)getApplication()).pushContext(this);
    }
	@Override
	protected void onDestroy(){
		super.onDestroy();
		((BaseApplication)getApplication()).removeContext(this);
	}
	public void requestPermission(Activity context,String[] permissions, int requestCode){
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
			int hasWriteStoragePermission = ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
			if (!(hasWriteStoragePermission == PackageManager.PERMISSION_GRANTED)){
				ActivityCompat.requestPermissions(context,permissions, requestCode);
			}
		}
	}
	public void requestAlertWindowPermission() {
		Boolean result = false;
		if (Build.VERSION.SDK_INT >= 23) {
			try {
				Class clazz = Settings.class;
				Method canDrawOverlays = clazz.getDeclaredMethod("canDrawOverlays", Context.class);
				result = (Boolean) canDrawOverlays.invoke(null, getApplication());
			} catch (Exception e) {
			}
		}
		if(result)
			return;
		Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
		intent.setData(Uri.parse("package:" + getPackageName()));
		startActivityForResult(intent, 1);
	}
}
