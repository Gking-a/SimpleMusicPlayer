/*
 */

package com.gking.simplemusicplayer.base;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

import gtools.managers.GHolder;

import static com.gking.simplemusicplayer.impl.MyApplicationImpl.CoverImg;

public abstract class BaseActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public <T extends View> T f(int id) {
        return super.findViewById(id);
    }
    public void loadPictures(){
        GHolder<String, Bitmap> gHolder=new GHolder<>();
        DisplayMetrics dm=getResources().getDisplayMetrics();
        int rw= (int) (dm.density*80);
        for (File f: CoverImg.listFiles()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds=true;
            BitmapFactory.decodeFile(f.getAbsolutePath(),options);
            int w=options.outWidth;
            options=new BitmapFactory.Options();
            options.inJustDecodeBounds=false;
            options.inSampleSize=rw/w;
            options.inPreferredConfig= Bitmap.Config.RGB_565;
            Bitmap bitmap=BitmapFactory.decodeFile(f.getAbsolutePath(),options);
            gHolder.add(f.getName(),bitmap);
        }
        GHolder.standardInstance.add("PlaylistPictures",gHolder);
        System.gc();
    }
}
