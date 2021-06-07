/*
 */

package com.gkingswq.simplemusicplayer;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import com.gkingswq.simplemusicplayer.util.FW;
import com.gkingswq.simplemusicplayer.util.GMath;

public class Launcher extends Activity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher);
        loadResources();
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }
    private void loadResources(){
        MyResources.nav_header_bg=new BitmapDrawable(
            cutPicture(BitmapFactory.decodeResource(getResources(),R.drawable.nav_header_bg)));
    }
    private Bitmap cutPicture(Bitmap source){
        DisplayMetrics dm=getResources().getDisplayMetrics();
        int height=dm.heightPixels,
            width=(int)(480*dm.density);
        double whW=GMath.getDec(width,height),
            whB=GMath.getDec(source.getWidth(),source.getHeight());
        FW.w("w"+width+" "+height+" "+whW);
        FW.w("sw"+source.getWidth()+" "+source.getHeight()+" "+whB);
        Bitmap result;
        if(whW==whB)return Bitmap.createScaledBitmap(source,width,height,true);
        if(whW<whB){
            int cutWidth=(int)(height*whB);
            int cutStart=(source.getWidth()-cutWidth)/2;
            result=Bitmap.createBitmap(source,cutStart,0,cutWidth,source.getHeight());
        }
        else{
            int cutHeight=(int)GMath.getDec(width,whB);
            int cutStart=(source.getHeight()-cutHeight)/2;
            result=Bitmap.createBitmap(source,0,cutStart,source.getWidth(),cutHeight);
        }
        FW.w("whw"+whW);
        result=Bitmap.createScaledBitmap(result,width,height,true);
        FW.w("h"+height);
        FW.w("sh"+result.getHeight());
        FW.w("whb"+whB);
        return result;
    }
}
