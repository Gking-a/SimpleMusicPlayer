/*
 */

package com.gking.simplemusicplayer.activity;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;

import com.gking.simplemusicplayer.MyResources;
import com.gking.simplemusicplayer.R;
import com.gking.simplemusicplayer.base.BaseActivity;
import com.gking.simplemusicplayer.util.FW;
import com.gking.simplemusicplayer.util.GMath;
import android.content.Context;

public class SplashActivity extends BaseActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Thread(() -> {
            loadResources();
            Intent intent=new Intent(getContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }).start();
    }
    private void loadResources(){
        MyResources.nav_header_bg=new BitmapDrawable(getResources(),
            cutPicture(BitmapFactory.decodeResource(getResources(),R.drawable.zsjlnj)));
//        FW.w("d"+MyResources.nav_header_bg.getIntrinsicWidth()+" "+MyResources.nav_header_bg.getIntrinsicHeight());
    }

    private Bitmap cutPicture(Bitmap source){
        DisplayMetrics dm=getResources().getDisplayMetrics();
        int height=dm.heightPixels,
            width=(int)(280*dm.density);
        double whW=GMath.getDec(width,height),
            whB=GMath.getDec(source.getWidth(),source.getHeight());
        FW.w("w"+width+" "+height+" "+whW);
        FW.w("sw"+source.getWidth()+" "+source.getHeight()+" "+whB);
        Bitmap result;
        if(whW==whB)return Bitmap.createScaledBitmap(source,width,height,true);
        if(whW<whB){
            source=Bitmap.createScaledBitmap(source,(int)(source.getWidth()*GMath.getDec(height,source.getHeight())),height,true);
            int cutStart=(source.getWidth()-width)/2;
            result=Bitmap.createBitmap(source,cutStart,0,width,height);
        }
        else{
            source=Bitmap.createScaledBitmap(source,width,(int)(source.getHeight()*GMath.getDec(height,source.getHeight())),true);
            int cutStart=(source.getHeight()-height)/2;
            result=Bitmap.createBitmap(source,0,cutStart,width,height);
        }
        FW.w("r"+result.getWidth()+" "+result.getHeight()+" "+GMath.getDec(result.getWidth(),result.getHeight()));
        return result;
    }
    public Activity getContext(){
        return this;
    }
}
